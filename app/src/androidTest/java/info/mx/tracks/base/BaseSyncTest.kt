package info.mx.tracks.base

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import info.hannes.changelog.ChangeLog.Companion.VERSION_KEY
import info.mx.tracks.ops.ImportIdlingResource
import info.mx.tracks.ops.RecalculateIdlingResource
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.prefs.MxPreferences.PREFERENCES_NAME
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit


abstract class BaseSyncTest {

    @get:Rule
    var nameRule = TestName()

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
