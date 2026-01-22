package info.mx.tracks

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.widget.ListView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.location.LocationServices
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.tracklist.ActivityTrackList
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityTrackListTest : BaseSyncTest() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityTrackList>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun showTrackListTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocationServices.getFusedLocationProviderClient(context).setMockMode(true)
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 42.125
        location.longitude = 55.123

        LocationServices.getFusedLocationProviderClient(context).setMockLocation(location)

        // This is the first time settings activity with always changed version number
        // Espresso.pressBackUnconditionally()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-init") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(10)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-10") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(20)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-20") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(30)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-30") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(40)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-40") })
    }

    @Test
    fun searchTest() {
        // This is the first time settings activity with always changed version number
//        // Espresso.pressBackUnconditionally()
        onView(withId(R.id.menu_search)).perform(click())

        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("aa"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-aa") })
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("de"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-de") })
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("ee"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-ee") })
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("ck"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-ck") })
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("sch"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-sch") })
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("oo"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-oo") })
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("tz"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-tz") })
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("ff"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-ff") })
    }


    @Test
    fun tab() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocationServices.getFusedLocationProviderClient(context).setMockMode(true)
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 42.125
        location.longitude = 55.123

        LocationServices.getFusedLocationProviderClient(context).setMockLocation(location)

        // This is the first time settings activity with always changed version number
        // Espresso.pressBackUnconditionally()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-init") })

        onView(withId(R.id.view_pager)).perform(swipeRight())
        Thread.sleep(200)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-star") })
        onView(withId(R.id.view_pager)).perform(swipeRight())
        Thread.sleep(200)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-distance") })
    }

    companion object {
        const val WAIT_EDIT = 200L
    }
}
