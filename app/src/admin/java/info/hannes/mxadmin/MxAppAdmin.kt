package info.hannes.mxadmin

import com.google.firebase.crashlytics.FirebaseCrashlytics
import info.hannes.crashlytic.CrashlyticsTree
import info.mx.core.util.NetworkUtils
import timber.log.Timber

class MxAppAdmin : MxAdminBaseApp() {

    override fun onCreate() {
        super.onCreate()

        addIPLogging()
    }

    override val siteId: Int
        get() = 2

    override val trackerName: String
        get() = "mxAdmin"

    private fun addIPLogging() {
        if (Timber.forest().first() is CrashlyticsTree) {
            FirebaseCrashlytics.getInstance().setCustomKey("IP", NetworkUtils.getIPAddress(true))
            FirebaseCrashlytics.getInstance().setCustomKey("wlan0", NetworkUtils.getMACAddress("wlan0"))
            FirebaseCrashlytics.getInstance().setCustomKey("eth0", NetworkUtils.getMACAddress("eth0"))
        }
    }

}
