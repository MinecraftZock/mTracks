package info.mx.tracks

import android.Manifest
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.uiautomator.executeShellCommandBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UActivityMapTest : BaseSyncTest() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityMapExtension>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Before
    fun grantPermission() {
        getInstrumentation()
            .uiAutomation
            .executeShellCommandBlocking("appops set ${BuildConfig.APPLICATION_ID} android:mock_location allow")
        mockLocation()
    }

    @Test
    fun smokeTestSimplyStart() {
        Thread.sleep(1000)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-open")
    }

    private fun mockLocation() {
        val lm = getApplicationContext<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)

        val mocLocationProvider = LocationManager.GPS_PROVIDER //lm.getBestProvider( criteria, true );

        lm.addTestProvider(
            mocLocationProvider, false, false,
            false, false, true, true, true,
            Criteria.POWER_LOW, Criteria.ACCURACY_FINE
        )
        lm.setTestProviderEnabled(mocLocationProvider, true)

        val loc = Location(mocLocationProvider)
        val mockLocation = Location(mocLocationProvider) // a string
        mockLocation.latitude = -12.902038
        mockLocation.longitude = -48.671337
        mockLocation.setAltitude(loc.altitude)
        mockLocation.time = System.currentTimeMillis()
        mockLocation.setAccuracy(1F)
        mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        lm.setTestProviderLocation(mocLocationProvider, mockLocation)
    }
}
