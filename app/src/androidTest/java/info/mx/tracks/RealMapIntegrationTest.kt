package info.mx.tracks

import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidmapsextensions.GoogleMap
import info.mx.core_generated.prefs.MxPreferences
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.map.MapIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealMapIntegrationTest : BaseSyncTest<ActivityMapExtension>() {
    override val screenshotMaps = false

    @Before
    fun setUpRealMap() {
        skipFirstAppUsage()
        MxPreferences.instance.edit().putMapType(0).commit()
        IdlingRegistry.getInstance().register(MapIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterMap() {
        IdlingRegistry.getInstance().unregister(MapIdlingResource.countingIdlingResource)
    }

    @Test
    fun testRealMapLoadsWithMockLocation() {
        startActivity<ActivityMapExtension>()
        assertMapRendering(GoogleMap.MAP_TYPE_NORMAL)
    }
}
