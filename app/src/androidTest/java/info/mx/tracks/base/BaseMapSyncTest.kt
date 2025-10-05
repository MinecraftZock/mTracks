package info.mx.tracks.base

import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import info.mx.tracks.ops.CountingIdlingResourceSingleton
import info.mx.tracks.ops.MapIdlingResourceSingleton
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit


abstract class BaseMapSyncTest: BaseSyncTest() {

    @Before
    fun registerMapIdlingResource() {
        IdlingPolicies.setIdlingResourceTimeout(4, TimeUnit.MINUTES)
        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES);
        IdlingRegistry.getInstance().register(MapIdlingResourceSingleton.idlingResource)
    }

    @After
    fun unregisterMapIdlingResource() {
        IdlingRegistry.getInstance().unregister(MapIdlingResourceSingleton.idlingResource)
    }

}
