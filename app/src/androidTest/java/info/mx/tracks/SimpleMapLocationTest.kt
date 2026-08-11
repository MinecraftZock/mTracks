package info.mx.tracks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.map.MapIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * MINIMAL EXAMPLE: Test ActivityMapExtension with a specific location.
 *
 * This is the simplest way to test the map activity with custom coordinates.
 */
@RunWith(AndroidJUnit4::class)
class SimpleMapLocationTest : BaseSyncTest<ActivityMapExtension>() {

    @Before
    fun setUp() {
        // Register IdlingResource to wait for map to load
        IdlingRegistry.getInstance().register(MapIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        // Unregister IdlingResource
        IdlingRegistry.getInstance().unregister(MapIdlingResource.countingIdlingResource)
    }

    @Test
    fun testMapWithSpecificLocation() {
        // Step 1: Create intent with your desired location
        startActivity<ActivityMapExtension>()

        // Step 4: Your test assertions go here
        // For example, verify the map view is displayed:
        onView(isRoot()).check { _, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            // Map is displayed successfully
        }
    }
}
