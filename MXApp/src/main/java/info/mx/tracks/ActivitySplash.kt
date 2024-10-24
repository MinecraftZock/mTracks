package info.mx.tracks

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import info.hannes.commonlib.TrackingApplication
import info.hannes.commonlib.getPackageInfoCompat
import info.mx.tracks.databinding.ActivitySplashBinding
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.tracklist.ActivityTrackList
import info.mx.tracks.util.SystemUiHider
import timber.log.Timber

/**
 * An full-screen activity that shows and hides the system UI (i.e. status bar and info.mx.tracks.navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
class ActivitySplash : ActivityBase() {
    private var ms: Long = 0
    private val splashTime: Long = 1000
    private val splashActive = true
    private val paused = false

    private lateinit var binding: ActivitySplashBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        val versionName: String
        try {
            versionName = packageManager.getPackageInfoCompat(packageName, 0).versionName
            binding.viewVersion.text = versionName + (BuildConfig.FLAVOR + " " +
                    (if (TrackingApplication.isDebug) " Debug" else "") + if (MxCoreApplication.isAdmin) " Admin" else "")
        } catch (ignored: NameNotFoundException) {
        }

        openLastOpenedActivity()
        //        showSplashImage();

        //        showSplashShort();
    }

    private fun openLastOpenedActivity() {
        val lastOpenActivity = MxPreferences.getInstance().lastOpenStartActivity
        if (lastOpenActivity != null && lastOpenActivity == ActivityMapExtension::class.java.simpleName) {
            startMXActivity(ActivityMapExtension::class.java)
        } else {
            startMXActivity(ActivityTrackList::class.java)
        }
    }

    private fun showSplashImage() {
        val img = findViewById<ImageView>(R.id.fullscreen_content)
        resources.configuration
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            img.setImageResource(R.drawable.splass_landscape)
        } else {
            img.setImageResource(R.drawable.splash_portrait)
        }
    }

    private fun showSplashShort() {
        val myThread = object : Thread() {
            override fun run() {
                try {
                    while (splashActive && ms < splashTime) {
                        if (!paused) {
                            ms += 100
                        }
                        sleep(100)
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                } finally {
                    startMXActivity(ActivityTrackList::class.java)
                }
            }
        }
        myThread.start()
    }

    private fun startMXActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
        finish()
    }

}
