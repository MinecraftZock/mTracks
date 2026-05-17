package info.mx.tracks

import android.graphics.Bitmap
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.captureToBitmap
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
 * Espresso test for ActivityMapExtension with specific location set via Intent.
 *
 * This test demonstrates how to launch the activity with a predefined location
 * using Intent extras for LAT (latitude) and LON (longitude).
 */
@RunWith(AndroidJUnit4::class)
class ActivityMapExtensionLocationTest : BaseSyncTest<ActivityMapExtension>() {

    @Before
    fun registerMapIdlingResource() {
        IdlingRegistry.getInstance().register(MapIdlingResource.countingIdlingResource)
        skipFirstAppUsage()
    }

    @After
    fun unregisterMapIdlingResource() {
        IdlingRegistry.getInstance().unregister(MapIdlingResource.countingIdlingResource)
    }

    /**
     * Test launching the map activity with a specific location (New York City).
     */
    @Test
    fun testMapWithNewYorkLocation() {
        // Create intent with specific location (New York City)
        startActivity<ActivityMapExtension>(40.7128, -74.0060)

        // Verify activity is displayed and capture screenshot
        onView(isRoot()).perform(captureToBitmap { bitmap: Bitmap ->
            bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-nyc-location")
        })
    }

    /**
     * Test launching the map activity with a different location (Tokyo).
     */
    @Test
    fun testMapWithTokyoLocation() {
        // Create intent with specific location (Tokyo)
        startActivity<ActivityMapExtension>(35.6762, 139.6503)

        // Verify activity is displayed and capture screenshot
        onView(isRoot()).perform(captureToBitmap { bitmap: Bitmap ->
            bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-tokyo-location")
        })
    }

    /**
     * Test launching the map activity with European location (Berlin).
     */
    @Test
    fun testMapWithBerlinLocation() {
        // Create intent with specific location (Berlin)
        startActivity<ActivityMapExtension>(52.5200, 13.4050)

        // Verify activity is displayed and capture screenshot
        onView(isRoot()).perform(captureToBitmap { bitmap: Bitmap ->
            bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-berlin-location")
        })
    }

    /**
     * Test with a motocross track location (example: custom track coordinates).
     */
    @Test
    fun testMapWithMXTrackLocation() {
        // Create intent with specific location (example MX track)
        startActivity<ActivityMapExtension>()

        // Verify activity is displayed and capture screenshot
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap ->
                bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-mx-track-location")
            })

        // You can add more assertions here, for example:
        // - Check if map is visible
        // - Check if specific UI elements are present
        // - Verify map markers are displayed
    }

    /**
     * Test without location extras (should use default behavior).
     */
    @Test
    fun testMapWithoutLocation() {
        startActivity<ActivityMapExtension>(0.0, 0.0)

        // Verify activity is displayed and capture screenshot
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap ->
                bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-default-location")
            })
    }
}
