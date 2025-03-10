package info.mx.tracks.base

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.ops.RecalculateIdlingResource
import info.mx.tracks.map.MapIdlingResource
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
        IdlingRegistry.getInstance().register(RecalculateIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(MapIdlingResource.countingIdlingResource)
    }

    @After
    fun pictureIdlingBeforeAfter() {
        unregisterIdlingResource()
        IdlingRegistry.getInstance().unregister(PictureIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(RecalculateIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(MapIdlingResource.countingIdlingResource)
    }

    @Test
    fun detailTrack() {
        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
        if (pressBack)
            Espresso.pressBack()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1") })
//        onView(withId(android.R.id.button2)).perform(click())
//        onView(withId(android.R.id.button1)).check(matches(isDisplayed()));
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2")
//
//        onData(withId(R.id.list_overview)).atPosition(1).perform(click())
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-d1")
//        onView(withId(R.id.tr_gen_detail_name)).check(matches(withText("Aalborg Daal Banen")));

        onView(withId(R.id.viewPager)).perform(swipeLeft())
        Thread.sleep(200)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-events") })
        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withId(R.id.viewPager)).perform(swipeRight())
        Thread.sleep(200)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-comment") })
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        Thread.sleep(200)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-info") })

        // favorite
        onView(withId(R.id.menu_favorite)).perform(click())
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-favorite1") })
        onView(withId(R.id.menu_favorite)).perform(click())
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-favorite2") })
        Thread.sleep(5000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-favorite3") })

        // up/down
        onView(withId(R.id.menu_next)).perform(click())
        Thread.sleep(4000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-next") })
        onView(withId(R.id.menu_prev)).perform(click())
        Thread.sleep(4000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-prev") })

        // edit
        onView(withId(R.id.menu_track_edit)).perform(click())
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-editTop") })
        onView(withId(R.id.scrollviewEdit)).perform(swipeDown())
        onView(withId(R.id.scrollviewEdit)).perform(swipeDown())
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-editBottom") })
        Espresso.pressBack()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-editFinished") })

        // open menu
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-openMenu") })

        // map
        onView(withText("Show on map")).perform(click())
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-map") })
    }

}
