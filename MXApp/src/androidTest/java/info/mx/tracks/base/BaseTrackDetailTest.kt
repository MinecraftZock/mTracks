package info.mx.tracks.base

import android.Manifest
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.ops.google.PictureIdlingResource
import info.mx.tracks.trackdetail.ActivityTrackDetail
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


abstract class BaseTrackDetailTest(private val restTrackId: Long, private val pressBack: Boolean) : BaseSyncTest() {

    private val intent = Intent(ApplicationProvider.getApplicationContext(), ActivityTrackDetail::class.java).apply {
        putExtra(FragmentUpDown.CONTENT_URI, "content://info.mx.tracks.sqlite.mxinfodb/tracksGesSum")
//        putExtra(FragmentUpDown.CONTENT_URI, MxInfoDBContract.Tracksges.CONTENT_URI)
        putExtra(FragmentUpDown.RECORD_ID_LOCAL, restTrackId)
    }

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityTrackDetail>(intent)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Before
    fun pictureIdlingBefore() {
        registerIdlingResource()
        IdlingRegistry.getInstance().register(PictureIdlingResource.countingIdlingResource)
    }

    @After
    fun pictureIdlingBeforeAfter() {
        unregisterIdlingResource()
        IdlingRegistry.getInstance().unregister(PictureIdlingResource.countingIdlingResource)
    }

    @Test
    fun detailTrack() {
        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
        if (pressBack)
            Espresso.pressBack()
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
//        onView(withId(android.R.id.button2)).perform(click())
//        onView(withId(android.R.id.button1)).check(matches(isDisplayed()));
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2")
//
//        onData(withId(R.id.list_overview)).atPosition(1).perform(click())
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-d1")
//        onView(withId(R.id.tr_gen_detail_name)).check(matches(withText("Aalborg Daal Banen")));

        onView(withId(R.id.viewPager)).perform(swipeLeft())
        Thread.sleep(200)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-events")
        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withId(R.id.viewPager)).perform(swipeRight())
        Thread.sleep(200)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-comment")
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        Thread.sleep(200)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-info")

        onView(withId(R.id.menu_favorite)).perform(click())
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-favorite1")
        onView(withId(R.id.menu_favorite)).perform(click())
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-favorite2")

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-openMenu")
    }

//    @Test
//    fun optionMenuFlavor() {
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-open")
//
////        onView(withText("Show on map")).perform(click())
////        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-globus")
////        Thread.sleep(2000)
//
//        onView(withId(R.id.menu_favorite)).perform(click())
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-favorite1")
//    }
//
//    @Test
//    fun optionMenuMap() {
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-init")
//        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-open")
//    }
}
