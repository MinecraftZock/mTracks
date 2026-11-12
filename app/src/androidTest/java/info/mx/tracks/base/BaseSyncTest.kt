package info.mx.tracks.base

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.OnMapReadyCallback
import com.androidmapsextensions.SupportMapFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.changelog.ChangeLog.Companion.VERSION_KEY
import info.mx.core.ops.ImportIdlingResource
import info.mx.core.ops.RecalculateIdlingResource
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.prefs.MxPreferences.Companion.PREFERENCES_NAME
import info.mx.core_generated.prefs.MxPreferences.Keys
import info.mx.core_generated.sqlite.MxInfoDBContract.Country
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.QueryHelper
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.map.FragmentMap
import info.mx.tracks.map.MapRenderingMode
import info.mx.tracks.service.RecalculateDistance
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference


abstract class BaseSyncTest<T : AppCompatActivity> {

    @get:Rule
    var nameRule = TestName()

    @get:Rule
    val locationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var activityScenario: ActivityScenario<out T>? = null

    private var originalMockLocationMode: Int? = null
    private var mockLocationEnabled = false
    protected open val screenshotMaps = true

    @Before
    fun registerIdlingResource() {
        if (!screenshotMaps) {
            assumeTrue(
                "Real-map integration tests require -Pandroid.testInstrumentationRunnerArguments.realMaps=true",
                InstrumentationRegistry.getArguments().getString("realMaps") == "true"
            )
            val mapKey = ApplicationProvider.getApplicationContext<Context>().getString(R.string.GOOGLE_MAP_API_KEY)
            assertTrue(
                "Real-map integration requires a configured Maps key authorized for this app's signing certificate",
                mapKey.isNotBlank() && mapKey != "dummy"
            )
        }
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            MapRenderingMode.screenshotMode = screenshotMaps
        }
        IdlingPolicies.setIdlingResourceTimeout(4, TimeUnit.MINUTES)
        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES)
        IdlingRegistry.getInstance().register(ImportIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(RecalculateIdlingResource.countingIdlingResource)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appOps = context.getSystemService(AppOpsManager::class.java)
        originalMockLocationMode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_MOCK_LOCATION, context.applicationInfo.uid, context.packageName
        )
        setMockLocationAppOp(AppOpsManager.MODE_ALLOWED)
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        Tasks.await(locationClient.setMockMode(true), 10, TimeUnit.SECONDS)
        mockLocationEnabled = true
        Tasks.await(locationClient.setMockLocation(munichLocation()), 10, TimeUnit.SECONDS)

        // An idle import can also mean a failed import; require the success marker and countries.
        val deadline = SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(4)
        while (MxPreferences.instance.lastSyncTime == 0L ||
            !ImportIdlingResource.countingIdlingResource.isIdleNow ||
            SQuery.newQuery().count(Country.CONTENT_URI) <= 2
        ) {
            assertTrue(
                "Screenshot setup requires a successful sync and a populated country table; check backend/import errors",
                SystemClock.elapsedRealtime() < deadline
            )
            SystemClock.sleep(100)
        }
        Espresso.onIdle()

        Tasks.await(locationClient.setMockLocation(munichLocation()), 10, TimeUnit.SECONDS)
        prepareMunichCountryFilter()
        if (screenshotMaps) {
            MxPreferences.instance.edit()
                .putMapZoom(12f)
                .putMapLatitude(MUNICH_LATITUDE.toFloat())
                .putMapLongitude(MUNICH_LONGITUDE.toFloat())
                .putMapTraffic(false)
                .commit()
        }
    }

    @After
    fun unregisterIdlingResource() {
        try {
            activityScenario?.close()
            activityScenario = null
        } finally {
            try {
                if (mockLocationEnabled) {
                    val context = ApplicationProvider.getApplicationContext<Context>()
                    Tasks.await(LocationServices.getFusedLocationProviderClient(context).setMockMode(false), 10, TimeUnit.SECONDS)
                    mockLocationEnabled = false
                }
            } finally {
                try {
                    originalMockLocationMode?.let { setMockLocationAppOp(it) }
                    originalMockLocationMode = null
                } finally {
                    InstrumentationRegistry.getInstrumentation().runOnMainSync {
                        MapRenderingMode.screenshotMode = false
                    }
                    IdlingRegistry.getInstance().unregister(ImportIdlingResource.countingIdlingResource)
                    IdlingRegistry.getInstance().unregister(RecalculateIdlingResource.countingIdlingResource)
                }
            }
        }
    }

    inline fun <reified A : T> startActivity(
        lat: Double = MUNICH_LATITUDE,
        lon: Double = MUNICH_LONGITUDE,
        contentUri: String? = null,
        restTrackId: Long = -1L
    ) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), A::class.java).apply {
            if (lat + lon != 0.0) {
                putExtra(ActivityMapExtension.LAT, lat)
                putExtra(ActivityMapExtension.LON, lon)
            }
            contentUri?.let {
                putExtra(FragmentUpDown.CONTENT_URI, contentUri)
            }
            if (restTrackId > -1)
                putExtra(FragmentUpDown.RECORD_ID_LOCAL, restTrackId)

        }
        activityScenario = ActivityScenario.launch<A>(intent)
        Espresso.onIdle()
        // Foreground-only location permission does not allow this check during setup.
        assertMunichDeviceLocation()
        // Only verify country filtering if Munich coordinates were used
        if (lat == MUNICH_LATITUDE && lon == MUNICH_LONGITUDE) {
            assertMunichCountryFilter()
        }
    }

    fun assertMunichDeviceLocation() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val actualLocation = Tasks.await(
            LocationServices.getFusedLocationProviderClient(context).lastLocation, 10, TimeUnit.SECONDS
        )
        requireNotNull(actualLocation) { "Mock location was not delivered to the foreground activity" }
        assertEquals(MUNICH_LATITUDE, actualLocation.latitude, 0.00001)
        assertEquals(MUNICH_LONGITUDE, actualLocation.longitude, 0.00001)
    }

    fun assertMapRendering(
        expectedMapType: Int,
        expectedLatitude: Double = MUNICH_LATITUDE,
        expectedLongitude: Double = MUNICH_LONGITUDE
    ) {
        val ready = CountDownLatch(1)
        val actualMap = AtomicReference<GoogleMap>()
        requireNotNull(activityScenario).onActivity { activity ->
            val fragment = activity.supportFragmentManager.findFragmentById(R.id.fragmentMap) as? FragmentMap
            val mapFragment = requireNotNull(fragment).childFragmentManager.findFragmentById(R.id.frag_map) as? SupportMapFragment
            requireNotNull(mapFragment).getExtendedMapAsync(object : OnMapReadyCallback {
                override fun onMapReady(googleMap: GoogleMap) {
                    actualMap.set(googleMap)
                    ready.countDown()
                }
            })
        }
        assertTrue("Map did not become ready", ready.await(10, TimeUnit.SECONDS))
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val map = requireNotNull(actualMap.get())
            assertEquals(expectedMapType, map.mapType)
            assertTrue("App-owned track markers were not drawn", map.markers.isNotEmpty())
            if (screenshotMaps) {
                assertTrue("Screenshot map must not render traffic", !map.isTrafficEnabled)
                assertTrue("Screenshot map must not render device location", !map.isMyLocationEnabled)
                assertEquals(12f, map.cameraPosition.zoom, 0.001f)
                assertEquals(expectedLatitude, map.cameraPosition.target.latitude, 0.00001)
                assertEquals(expectedLongitude, map.cameraPosition.target.longitude, 0.00001)
            }
        }
    }

    fun assertMunichCountryFilter() {
        assertTrue("Automatic location filtering did not finish", MxPreferences.instance.firstTimeLocation)
        assertEquals(
            "Germany must be visible for Munich", 1,
            SQuery.newQuery().expr(Country.COUNTRY, SQuery.Op.EQ, "DE")
                .expr(Country.SHOW, SQuery.Op.EQ, 1).count(Country.CONTENT_URI)
        )
        assertEquals(
            "USA must be hidden for Munich", 1,
            SQuery.newQuery().expr(Country.COUNTRY, SQuery.Op.EQ, "US")
                .expr(Country.SHOW, SQuery.Op.EQ, 0).count(Country.CONTENT_URI)
        )
    }

    fun prepareMunichCountryFilter() {
        firstTimeImport()
        QueryHelper.resetFilter()
        val context = ApplicationProvider.getApplicationContext<Context>()
        RecalculateDistance(context).recalculateTracks(munichLocation(), "screenshot-test")
        Espresso.onIdle()
        assertMunichCountryFilter()
    }

    private fun munichLocation() = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = MUNICH_LATITUDE
        longitude = MUNICH_LONGITUDE
        accuracy = 1f
        time = System.currentTimeMillis()
        elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
    }

    private fun setMockLocationAppOp(mode: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val modeName = when (mode) {
            AppOpsManager.MODE_ALLOWED -> "allow"
            AppOpsManager.MODE_IGNORED -> "ignore"
            AppOpsManager.MODE_ERRORED -> "deny"
            AppOpsManager.MODE_DEFAULT -> "default"
            AppOpsManager.MODE_FOREGROUND -> "foreground"
            else -> error("Unsupported mock location app-op mode: $mode")
        }
        val output = ParcelFileDescriptor.AutoCloseInputStream(
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "appops set ${context.packageName} android:mock_location $modeName"
            )
        ).bufferedReader().use { it.readText() }
        assertTrue("Could not set mock location app-op: $output", output.isBlank())
    }

    fun firstTimeImport() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        sharedPreferences.edit()
            .remove(Keys.FIRST_TIME_USE)
            .remove(Keys.FIRST_TIME_LOCATION)
            .remove(Keys.FIRST_TIME_COUNTRY)
            .apply()
    }

    fun dontRememberLastUsedActivity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        sharedPreferences.edit()
            .remove(Keys.LAST_OPEN_START_ACTIVITY)
            .apply()
    }

    fun skipFirstAppUsage() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // with changelog <= 3.8.1 we have to use deprecated version PreferenceManager.getDefaultSharedPreferences(context)
        // with changelog >= 3.8.2 we have to use context.getSharedPreferences("changelog", MODE_PRIVATE)
        val sp = context.getSharedPreferences("changelog", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(VERSION_KEY, 123)
        editor.commit()

        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        sharedPreferences.edit()
            .remove(Keys.LAST_OPEN_START_ACTIVITY)
            .apply()
    }

    companion object {
        const val MUNICH_LATITUDE = 48.1351
        const val MUNICH_LONGITUDE = 11.5820
    }
}
