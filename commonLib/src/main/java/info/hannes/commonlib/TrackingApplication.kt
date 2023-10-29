package info.hannes.commonlib

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.matomo.sdk.Matomo
import org.matomo.sdk.Tracker
import org.matomo.sdk.TrackerBuilder

abstract class TrackingApplication : Application() {

    private var tracker: Tracker? = null

    /**
     * The siteID you specified for this application in Piwik.
     */
    abstract val siteId: Int

    /**
     * The URL of your remote Piwik server.
     */
    abstract val trackerUrl: String

    /**
     * The name of your tracker
     */
    abstract val trackerName: String

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        // init static Tracker
        androidId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        if (isDebug) {
            androidId = "debug"
        }
        applicationScope = MainScope()
        applicationScope.launch(Dispatchers.Default) {
            appTracker = getTracker()
            appTracker!!.userId = androidId
        }
    }

    @Synchronized
    fun getTracker(): Tracker? {
        if (tracker == null) {
            tracker = TrackerBuilder(trackerUrl, siteId, trackerName).build(Matomo.getInstance(this))
        }
        return tracker
    }

    companion object {
        var applicationScope = MainScope()
        var isDebug = false
        var appTracker: Tracker? = null
            private set
        var androidId = ""

        @Suppress("DEPRECATION")
        @SuppressLint("NewApi")
        fun getVersion(context: Context): Long {
            var version = 0
            try {
                val pInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0)
                version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        pInfo.longVersionCode.toInt()
                    } catch (e: NoSuchMethodError) {
                        // on Xperia XZ1 Compact I see
                        // java.lang.NoSuchMethodError: No virtual method getLongVersionCode()J in class Landroid/content/pm/PackageInfo
                        pInfo.versionCode
                    }
                } else {
                    pInfo.versionCode
                }
            } catch (ignored: NameNotFoundException) {
            }

            return version.toLong()
        }

        fun getVersionName(context: Context): String {
            var version = ""
            try {
                val pInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0)
                version = pInfo.versionName
            } catch (ignored: NameNotFoundException) {
            }

            return version
        }
    }

}
