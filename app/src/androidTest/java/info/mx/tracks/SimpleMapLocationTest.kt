package info.mx.tracks

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
class SimpleMapLocationTest : BaseSyncTest() {

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
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ActivityMapExtension::class.java
        ).apply {
            // Set latitude and longitude
            putExtra(ActivityMapExtension.LAT, 48.1351) // Munich latitude
            putExtra(ActivityMapExtension.LON, 11.5820) // Munich longitude
        }

        // Step 2: Launch the activity
        ActivityScenario.launch<ActivityMapExtension>(intent).use {
            // Step 3: Wait for map to load
            Thread.sleep(2000)

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
}
