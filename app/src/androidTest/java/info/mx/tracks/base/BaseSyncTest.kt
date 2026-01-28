package info.mx.tracks.base

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import info.hannes.changelog.ChangeLog.Companion.VERSION_KEY
import info.mx.core.ops.ImportIdlingResource
import info.mx.core.ops.RecalculateIdlingResource
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.prefs.MxPreferences.PREFERENCES_NAME
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.map.ActivityMapExtension
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit


abstract class BaseSyncTest<T : AppCompatActivity> {

    @get:Rule
    var nameRule = TestName()

    var activityScenario: ActivityScenario<out T>? = null

    @Before
    fun registerIdlingResource() {
        IdlingPolicies.setIdlingResourceTimeout(4, TimeUnit.MINUTES)
        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES)
        IdlingRegistry.getInstance().register(ImportIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(RecalculateIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(ImportIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(RecalculateIdlingResource.countingIdlingResource)
        activityScenario?.close()
    }

    inline fun <reified A : T> startActivity(
        lat: Double = 48.1351,
        lon: Double = 11.5820,
        contentUri: String? = null,
        restTrackId: Long = -1L
    ) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), A::class.java).apply {
            if (lat + lon != 0.0) {
                putExtra(ActivityMapExtension.LAT, lat)
                putExtra(ActivityMapExtension.LON, lon)
            }
            contentUri?.let {
                putExtra(FragmentUpDown.CONTENT_URI, contentUri)
            }
            if (restTrackId > -1)
                putExtra(FragmentUpDown.RECORD_ID_LOCAL, restTrackId)

        }
        activityScenario = ActivityScenario.launch<A>(intent)
        Thread.sleep(2000)
    }

    fun dontRememberLastUsedActivity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        sharedPreferences.edit()
            .remove(MxPreferences.Keys.LAST_OPEN_START_ACTIVITY)
            .apply()
    }

    fun skipFirstAppUsage() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // with changelog <= 3.8.1 we have to use deprecated version PreferenceManager.getDefaultSharedPreferences(context)
        // with changelog >= 3.8.2 we have to use context.getSharedPreferences("changelog", MODE_PRIVATE)
        val sp = context.getSharedPreferences("changelog", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(VERSION_KEY, 123)
        editor.commit()

        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        sharedPreferences.edit()
            .remove(MxPreferences.Keys.LAST_OPEN_START_ACTIVITY)
            .apply()
    }
}
