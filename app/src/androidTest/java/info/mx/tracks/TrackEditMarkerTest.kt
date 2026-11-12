package info.mx.tracks

import android.graphics.Bitmap
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.maps.GoogleMap
import com.robotoworks.mechanoid.db.SQuery
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.common.SecHelper
import info.mx.tracks.map.FragmentMapScroll
import info.mx.tracks.map.MapIdlingResource
import info.mx.tracks.trackdetail.ActivityTrackEdit
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class TrackEditMarkerTest : BaseSyncTest<ActivityTrackEdit>() {
    @Before
    fun registerMap() {
        skipFirstAppUsage()
        IdlingRegistry.getInstance().register(MapIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterMap() {
        IdlingRegistry.getInstance().unregister(MapIdlingResource.countingIdlingResource)
    }

    @Test
    fun testMarkerCameraAndHintIgnoreUsageCounter() {
        val track = requireNotNull(
            SQuery.newQuery().expr(Tracks.TRACKNAME, SQuery.Op.EQ, "2B Racing MX Park")
                .selectFirst<TracksRecord>(Tracks.CONTENT_URI)
        )
        val originalCount = MxPreferences.instance.markerShowLongClickText
        var firstCapture: Bitmap? = null
        try {
            for (count in listOf(0, 100)) {
                MxPreferences.instance.edit().putMarkerShowLongClickText(count).commit()
                startActivity<ActivityTrackEdit>(restTrackId = track.id)
                val ready = CountDownLatch(1)
                val actualMap = AtomicReference<GoogleMap>()
                requireNotNull(activityScenario).onActivity { activity ->
                    val edit = requireNotNull(activity.supportFragmentManager.findFragmentById(R.id.fragment_container))
                    val fragment = edit.childFragmentManager.findFragmentById(R.id.map_edit) as FragmentMapScroll
                    fragment.getMapAsync {
                        actualMap.set(it)
                        ready.countDown()
                    }
                }
                assertTrue("Edit map did not become ready", ready.await(10, TimeUnit.SECONDS))
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    val camera = requireNotNull(actualMap.get()).cameraPosition
                    assertEquals(SecHelper.entcryptXtude(track.latitude), camera.target.latitude, 0.00001)
                    assertEquals(SecHelper.entcryptXtude(track.longitude), camera.target.longitude, 0.00001)
                    assertEquals(15f, camera.zoom, 0.001f)
                    assertEquals(count, MxPreferences.instance.markerShowLongClickText)
                }
                onView(withId(R.id.map_edit)).perform(captureToBitmap {
                    val previous = firstCapture
                    if (previous == null) {
                        firstCapture = it
                    } else {
                        assertTrue("Marker rendering changed with the hint usage counter", previous.sameAs(it))
                    }
                })
                requireNotNull(activityScenario).close()
                activityScenario = null
                Espresso.onIdle()
                assertTrue("Closed edit map leaked a load", MapIdlingResource.countingIdlingResource.isIdleNow)
            }
        } finally {
            MxPreferences.instance.edit().putMarkerShowLongClickText(originalCount).commit()
        }
    }
}
