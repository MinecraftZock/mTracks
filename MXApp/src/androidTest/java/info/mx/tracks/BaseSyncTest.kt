package info.mx.tracks

import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import info.mx.tracks.ops.CountingIdlingResourceSingleton
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit


abstract class BaseSyncTest {

    @get:Rule
    var nameRule = TestName()

    @Before
    fun registerIdlingResource() {
        IdlingPolicies.setIdlingResourceTimeout(4, TimeUnit.MINUTES)
        IdlingRegistry.getInstance().register(CountingIdlingResourceSingleton.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(CountingIdlingResourceSingleton.countingIdlingResource)
    }

}
