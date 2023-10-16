package info.mx.tracks

import android.Manifest
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.trackdetail.ActivityTrackDetail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityTrackDetailTest : BaseSyncTest() {

    private val intent = Intent(ApplicationProvider.getApplicationContext(), ActivityTrackDetail::class.java).apply {
        putExtra(FragmentUpDown.CONTENT_URI, "content://info.mx.tracks.sqlite.mxinfodb/tracksGesSum")
//        putExtra(FragmentUpDown.CONTENT_URI, MxInfoDBContract.Tracksges.CONTENT_URI)
        putExtra(FragmentUpDown.RECORD_ID_LOCAL, 1900L)
    }

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityTrackDetail>(intent)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun detailTrack() {
        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
        Espresso.pressBack()
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
//        onView(withId(android.R.id.button2)).perform(click())
//        onView(withId(android.R.id.button1)).check(matches(isDisplayed()));
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2")
//
//        onData(withId(R.id.list_overview)).atPosition(1).perform(click())
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-d1")
//        onView(withId(R.id.tr_gen_detail_name)).check(matches(withText("Aalborg Daal Banen")));

        onView(withId(R.id.viewPager)).perform(swipeLeft())
        Thread.sleep(100)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-events")
        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withId(R.id.viewPager)).perform(swipeRight())
        Thread.sleep(100)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-comment")
    }

}
