package info.mx.tracks.base

import androidx.test.espresso.IdlingPolicies.getDynamicIdlingResourceErrorPolicy
import androidx.test.espresso.IdlingPolicies.getMasterIdlingPolicy
import androidx.test.espresso.IdlingPolicies.setIdlingResourceTimeout
import androidx.test.espresso.IdlingPolicies.setMasterPolicyTimeout
import androidx.test.espresso.IdlingPolicy
import androidx.test.espresso.IdlingRegistry
import info.mx.tracks.ops.ImportIdlingResource
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.prefs.MxPreferences.PREFERENCES_NAME
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit


abstract class BaseSyncTest {

    private lateinit var resourcePolicy: IdlingPolicy
    private lateinit var masterPolicy: IdlingPolicy

    @get:Rule
    var nameRule = TestName()

    @Before
    fun registerIdlingResource() {
        masterPolicy = getMasterIdlingPolicy()
        resourcePolicy = getDynamicIdlingResourceErrorPolicy()

        setMasterPolicyTimeout(10 * 60, TimeUnit.SECONDS)
        setIdlingResourceTimeout(10 * 60, TimeUnit.SECONDS)

        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES)
        IdlingRegistry.getInstance().register(ImportIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        setMasterPolicyTimeout(masterPolicy.idleTimeout, masterPolicy.idleTimeoutUnit)
        setIdlingResourceTimeout(resourcePolicy.idleTimeout, resourcePolicy.idleTimeoutUnit)

        IdlingRegistry.getInstance().unregister(ImportIdlingResource.countingIdlingResource)
    }

    fun dontRememberLastUsedActivity() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, android.content.Context.MODE_PRIVATE)

        sharedPreferences.edit()
            .remove(MxPreferences.Keys.LAST_OPEN_START_ACTIVITY)
            .apply()
    }

}
