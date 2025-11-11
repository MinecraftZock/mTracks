package info.mx.tracks

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.text.Spanned
import com.robotoworks.mechanoid.Mechanoid
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.net.ServiceClient
import com.robotoworks.mechanoid.ops.Ops
import info.mx.comlib.prefs.CommLibPrefs
import info.mx.tracks.koin.CoreKoinContext
import info.mx.tracks.koin.coreModule
import info.mx.tracks.ops.AbstractOpPostRatingsOperation
import info.mx.tracks.ops.AbstractOpSyncFromServerOperation
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.rest.MxInfo
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.PicturesRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.koinApplication
import org.matomo.sdk.extra.TrackHelper
import timber.log.Timber
import java.io.File

abstract class MxCoreApplication : MxAccessApplication() {

    override fun onCreate() {
        super.onCreate()

        setLogging2File(this)

        Mechanoid.init(this)
        CommLibPrefs.init(this)

        applicationScope.launch(Dispatchers.Default) {
            createApiClient()
            checkToRepairOrSync()
        }

        readSettings()

        CoreKoinContext.koinApplication = koinApplication {
            //androidContext(this.applicationContext)
            modules(coreModule)
        }
    }

    override val trackerUrl = "https://www.mxtracks.info/piwik/piwik.php"

    protected open fun setLogging2File(base: Context?) = Unit

    abstract suspend fun checkToRepairOrSync()

    abstract fun confirmPicture(context: Context, restId: Long, statusCurrent: Int)

    companion object {

        var isAdmin = false
        var isAdminOrDebug = isAdmin || BuildConfig.DEBUG
        var showWeather = true
        private const val SYNC_WAIT = (1000 * 60 * 60 * 12).toLong()
        lateinit var mxInfo: MxInfo
            protected set
        private var mxServerUrl: String? = null
        private var trackApp = true
        var isEmulator = Build.HARDWARE.equals("ranchu")
        var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE

        fun readSettings() {
            trackApp = !isAdmin
        }

        fun getFilesDir(context: Context?): String {
            var path = ""
            if (context != null) {
                if (/*!MxCoreApplication.isAdmin || */context.getExternalFilesDir(null) == null && context.filesDir != null) {
                    path = context.filesDir.path
                } else if (context.getExternalFilesDir(null) != null) {
                    path = context.getExternalFilesDir(null)!!.path
                }
            }
            return path
        }

        val isProduction: Boolean
            get() = mxServerUrl!!.contains(".info")

        fun doSync(updateProvider: Boolean, force: Boolean, flavor: String) {
            if (MxPreferences.getInstance().lastSyncTime + SYNC_WAIT < System.currentTimeMillis() || force) {
                val intentM = AbstractOpPostRatingsOperation.newIntent()
                Ops.execute(intentM)

                val intent = AbstractOpSyncFromServerOperation.newIntent(updateProvider, flavor)
                Ops.execute(intent)
            }
        }

        fun createApiClient() {
            val prefs = CommLibPrefs.instance
            mxServerUrl = prefs.serverUrl
            if (mxServerUrl!!.substring(mxServerUrl!!.length - 1) != "/") {
                mxServerUrl += "/"
            }
            mxInfo = MxInfo(prefs.serverUrl) //, BuildConfig.DEBUG TODO
            setHeader(mxInfo)
        }

        private fun setHeader(client: ServiceClient) {
            for (param in aadhresUParams) {
                client.setHeader(param.key, param.value)
            }
        }

        fun clearDB() {
            // cache
            val recThumbs = SQuery.newQuery().expr(MxInfoDBContract.Pictures.LOCALTHUMB, SQuery.Op.NEQ, "")
                .select<PicturesRecord>(MxInfoDBContract.Pictures.CONTENT_URI, MxInfoDBContract.Pictures._ID)
            for (rec in recThumbs) {
                val file = File(rec.localthumb)
                Timber.d("Delete thumb %s", rec.localthumb)
                file.delete()
            }
            val recLocal = SQuery.newQuery().expr(MxInfoDBContract.Pictures.LOCALFILE, SQuery.Op.NEQ, "")
                .select<PicturesRecord>(MxInfoDBContract.Pictures.CONTENT_URI, MxInfoDBContract.Pictures._ID)
            for (rec in recLocal) {
                val file = File(rec.localfile)
                Timber.d("Delete local %s", rec.localfile)
                file.delete()
            }
            SQuery.newQuery().delete(MxInfoDBContract.Pictures.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Ratings.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Favorits.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Tracks.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Events.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Country.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Series.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Weather.CONTENT_URI)
            SQuery.newQuery().delete(MxInfoDBContract.Trackstage.CONTENT_URI)
        }

        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        fun showDlgHtml(context: Context, title: Spanned, text: Spanned) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage(text)
                .setCancelable(true)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // MainActivity.this.finish();
                }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        fun trackEvent(category: String, action: String) {
            if (trackApp) {
                TrackHelper.track().screen(category).title(action).with(appTracker)
            }
        }

        fun trackActivity(nameActivity: String) {
            var name = nameActivity
            name = name.replace("Activity", "").replace("Fragment", "")
            if (name != "MenuAdmin" && trackApp) {
                Timber.d(name)
                TrackHelper.track().screen("/track/view").title(name).with(appTracker)
            }
        }
    }
}
