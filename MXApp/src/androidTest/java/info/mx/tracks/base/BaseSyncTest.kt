package info.mx.tracks.base

import androidx.test.espresso.IdlingPolicies.getDynamicIdlingResourceErrorPolicy
import androidx.test.espresso.IdlingPolicies.getMasterIdlingPolicy
import androidx.test.espresso.IdlingPolicies.setIdlingResourceTimeout
import androidx.test.espresso.IdlingPolicies.setMasterPolicyTimeout
import androidx.test.espresso.IdlingPolicy
import androidx.test.espresso.IdlingRegistry
import info.mx.tracks.ops.CountingIdlingResourceSingleton
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

        IdlingRegistry.getInstance().register(CountingIdlingResourceSingleton.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        setMasterPolicyTimeout(masterPolicy.idleTimeout, masterPolicy.idleTimeoutUnit)
        setIdlingResourceTimeout(resourcePolicy.idleTimeout, resourcePolicy.idleTimeoutUnit)

        IdlingRegistry.getInstance().unregister(CountingIdlingResourceSingleton.countingIdlingResource)
    }

}
