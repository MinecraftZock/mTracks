package info.mx.tracks

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.location.LocationServices
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.tracklist.ActivityTrackList
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
        Espresso.pressBack()
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-init")
    }

    @Test
    fun searchTest() {
        // This is the first time settings activity with always changed version number
//        Espresso.pressBack()
        onView(withId(R.id.menu_search)).perform(click())

        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("aa"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-aa")
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("de"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-de")
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("ee"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-ee")
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("ck"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-ck")
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("sch"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-sch")
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("oo"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-oo")
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("tz"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-tz")
        onView(withId(R.id.search_src_text)).perform(clearText(), typeText("ff"))
        Thread.sleep(WAIT_EDIT)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-ff")
    }

    companion object {
        const val WAIT_EDIT = 200L
    }
}
