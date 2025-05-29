package info.mx.tracks

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.view.Window
import info.hannes.commonlib.TrackingApplication
import info.hannes.commonlib.getPackageInfoCompat
import info.mx.tracks.databinding.ActivitySplashBinding
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.tracklist.ActivityTrackList

class ActivitySplash : ActivityBase() {
    private lateinit var binding: ActivitySplashBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        val versionName: String
        try {
            versionName = packageManager.getPackageInfoCompat(packageName, 0).versionName.toString()
            binding.viewVersion.text = versionName + (BuildConfig.FLAVOR + " " +
                    (if (TrackingApplication.isDebug) " Debug" else "") + if (MxCoreApplication.isAdmin) " Admin" else "")
        } catch (ignored: NameNotFoundException) {
        }

        openLastOpenedActivity()
    }

    private fun openLastOpenedActivity() {
        val lastOpenActivity = MxPreferences.getInstance().lastOpenStartActivity
        if (lastOpenActivity != null && lastOpenActivity == ActivityMapExtension::class.java.simpleName) {
            startMXActivity(ActivityMapExtension::class.java)
        } else {
            startMXActivity(ActivityTrackList::class.java)
        }
    }

    private fun startMXActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
        finish()
    }

}
