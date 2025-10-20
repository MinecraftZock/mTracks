package info.mx.tracks

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.provider.Settings
import androidx.multidex.MultiDex
import com.google.android.libraries.places.api.Places
import com.google.firebase.crashlytics.FirebaseCrashlytics
import info.hannes.crashlytic.CrashlyticsTree
import info.hannes.timber.DebugFormatTree
import info.mx.tracks.koin.appModule
import info.mx.tracks.koin.dbModule
import info.mx.tracks.koin.flavorModule
import info.mx.tracks.koin.restModule
import info.mx.tracks.ops.OpSyncFromServerOperation
import info.mx.tracks.ops.google.OpGetRouteOperation
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.memory.MxMemDatabase
import info.mx.tracks.service.LocationJobService
import info.mx.tracks.service.LocationRecalculateService
import info.mx.tracks.service.RecalculateDistance
import info.mx.tracks.sqlite.MxInfoDBOpenHelper
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("Registered")
open class MxApplication : MxCoreApplication(), KoinComponent {

    private val mxMemDatabase: MxMemDatabase by inject()

    private val mxDatabase: MxDatabase by inject()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val intentService = Intent(context, LocationRecalculateService::class.java)
            val location = intent.getParcelableExtra<Location>(OpSyncFromServerOperation.LOCATION)
            val source = intent.getStringExtra(OpSyncFromServerOperation.SOURCE)
            intentService.putExtra(OpSyncFromServerOperation.LOCATION, location)
            intentService.putExtra(OpSyncFromServerOperation.SOURCE, source)
            LocationRecalculateService.enqueueWork(context, intentService)
        }
    }

    override fun onCreate() {
        isDebug = BuildConfig.DEBUG
//        isAdmin = isDebug
        isAdminOrDebug = isDebug
        showWeather = BuildConfig.SHOW_WEATHER

        // for 15 minutes disable admin
        if (!isGoogleTests) {
            isAdmin = resources.getBoolean(R.bool.isAdmin)// || isDebug
            isAdminOrDebug = isAdmin || isDebug
        }
        super.onCreate()

        logLevel = HttpLoggingInterceptor.Level.valueOf(BuildConfig.HTTP_LOGGING_LEVEL)

        RxJavaPlugins.setErrorHandler { throwable -> Timber.e(throwable) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, IntentFilter(OpSyncFromServerOperation.RECALC_TRACKS), Context.RECEIVER_NOT_EXPORTED)
        } else
            registerReceiver(broadcastReceiver, IntentFilter(OpSyncFromServerOperation.RECALC_TRACKS))

        LocationJobService.scheduleJob(applicationContext)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.GOOGLE_MAP_API_KEY))
        }

        startKoin {
            //androidLogger()
            androidContext(this@MxApplication)
            modules(appModule)
            modules(dbModule)
            modules(restModule)
            modules(flavorModule)
        }

        applicationScope.launch {
            prepareMemoryDatabase()
        }
    }

    @SuppressLint("CheckResult")
    private fun prepareMemoryDatabase() {
        mxDatabase.capturedLatLngDao().lastNonIgnoredLocation
            .subscribe(
                { (_, lat, lon) ->
                    val lastKnown = Location("lastKnown")
                    lastKnown.latitude = lat
                    lastKnown.longitude = lon
                    RecalculateDistance.calculateDistanceOnTracks(mxMemDatabase, lastKnown)
                },
                { Timber.e(it) },
                { RecalculateDistance.calculateDistanceOnTracks(mxMemDatabase, null) }
            )
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override val siteId: Int
        get() = 2

    override val trackerName: String
        get() = "mxPro"

    @SuppressLint("HardwareIds")
    final override fun setLogging2File(base: Context?) {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugFormatTree())
        } else {
            FirebaseCrashlytics.getInstance().setCustomKey("VERSION_NAME", BuildConfig.VERSION_NAME)
            Timber.plant(CrashlyticsTree(Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)))
        }
    }

    override fun confirmPicture(context: Context, restId: Long, statusCurrent: Int) = Unit

    override suspend fun checkToRepairOrSync() {
        applicationScope.async {
            if (MxPreferences.getInstance().repairDB) {
                MxInfoDBOpenHelper.getDatabase()?.close()
                val db = File(MxInfoDBOpenHelper.getDir(applicationContext))

                db.delete()
                MxPreferences.getInstance().edit().putRepairDB(false).commit()
            }
        }.await()
        OpGetRouteOperation.deleteOldRoutes()
        doSync(updateProvider = false, force = false, flavor = BuildConfig.FLAVOR)
    }

    companion object {

        private var runningTest: AtomicBoolean? = null
        private var runUnitTest: Boolean? = null
        val isGoogleTests: Boolean
            get() {
                val closeToNew = BuildConfig.UNIX_TIME_CREATED + 60 * 15 >= System.currentTimeMillis() / 1000
                return (closeToNew || isRunningEspresso() || isRunningUnitTests) && !isDebug
            }

        operator fun get(context: Context): MxApplication {
            return context.applicationContext as MxApplication
        }

        @Synchronized
        fun isRunningEspresso(): Boolean {
            if (runningTest == null) {
                var isTest: Boolean = try {
                    Class.forName("android.support.test.espresso.Espresso")
                    true
                } catch (e: ClassNotFoundException) {
                    false
                }

                if (!isTest) {
                    isTest = try {
                        Class.forName("androidx.test.espresso.Espresso")
                        true
                    } catch (e: ClassNotFoundException) {
                        false
                    }
                }

                runningTest = AtomicBoolean(isTest)
            }
            return runningTest!!.get()
        }

        /**
         * Returns true if we run the code under (unit) test conditions, false otherwise
         *
         * @return state if running under test
         */
        val isRunningUnitTests: Boolean
            get() {
                if (runUnitTest == null) {
                    runUnitTest = "true" == System.getProperty("run-under-test", "false") || !System.getProperty("java.vendor")!!.contains("Android")
                }
                return runUnitTest!!
            }
    }
}
