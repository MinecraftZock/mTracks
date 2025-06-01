package info.mx.tracks.base

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import info.mx.tracks.ops.CountingIdlingResourceSingleton
import info.mx.tracks.ops.google.RecalculateIdlingResource
import info.mx.tracks.ops.ImportIdlingResource
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
        IdlingRegistry.getInstance().register(RecalculateIdlingResource.recalculateIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(ImportIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(RecalculateIdlingResource.recalculateIdlingResource)
    }

    protected fun mockLocation() {
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
