package info.hannes.mxadmin.crashlytic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import info.hannes.crashlytic.CrashlyticsTree
import info.mx.tracks.MxApplication
import info.mx.tracks.R
import info.mx.tracks.databinding.ActivityCrashlyticBinding
import timber.log.Timber

class ActivityCrashlytic : AppCompatActivity() {

    private lateinit var binding: ActivityCrashlyticBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashlyticBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener { finish() }

        binding.containerActivityCrashlytic.buttonException.isEnabled = !MxApplication.isGoogleTests
        binding.containerActivityCrashlytic.buttonException.setOnClickListener {
            if (Timber.forest().first() is CrashlyticsTree) {
                Toast.makeText(this, "force crash ${info.hannes.logcat.BuildConfig.VERSIONNAME}", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(
                    { throw RuntimeException("Test Crash ${info.hannes.logcat.BuildConfig.VERSIONNAME}") },
                    3000
                )
            } else
                Toast.makeText(this, "Tree is ${Timber.forest().first().javaClass.simpleName}", Toast.LENGTH_LONG).show()
        }
        binding.containerActivityCrashlytic.buttonEThrowable.setOnClickListener {
            Timber.w("first")
            Timber.e("second")
            Timber.e(RuntimeException("Test queue e(throwable)"))
        }
        binding.containerActivityCrashlytic.buttonDirect.setOnClickListener {
            FirebaseCrashlytics.getInstance().log("first")
            FirebaseCrashlytics.getInstance().log("Direct queue e(throwable)")
        }
        binding.containerActivityCrashlytic.buttonEText.setOnClickListener { Timber.e("Test e(text)") }
        binding.containerActivityCrashlytic.buttonW.setOnClickListener { Timber.w("Test w(text)") }

        binding.containerActivityCrashlytic.textTree.text = Timber.forest().first().javaClass.simpleName
    }

}
