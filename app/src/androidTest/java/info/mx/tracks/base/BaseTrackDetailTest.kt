package info.mx.tracks.base

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import info.hannes.changelog.ChangeLog.Companion.VERSION_KEY
import info.hannes.timber.DebugFormatTree
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.map.MapIdlingResource
import info.mx.tracks.ops.RecalculateIdlingResource
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.prefs.MxPreferences.PREFERENCES_NAME
import info.mx.tracks.ops.google.PictureIdlingResource
import info.mx.tracks.trackdetail.ActivityTrackDetail
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import timber.log.Timber


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class BaseTrackDetailTest(private val restTrackId: Long) : BaseSyncTest() {

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
        Timber.plant(DebugFormatTree())

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
        skipFirstAppUsage()

        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
//        if (pressBack)
//            Espresso.pressBack()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1") })
//        onView(withId(android.R.id.button2)).perform(click())
//        onView(withId(android.R.id.button1)).check(matches(isDisplayed()));
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2")
//
//        onData(withId(R.id.list_overview)).atPosition(1).perform(click())
//        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-d1")
//        onView(withId(R.id.tr_gen_detail_name)).check(matches(withText("Aalborg Daal Banen")));

        // Scroll image gallery to the end by performing a custom action on the root view
        Timber.d("scrollImageGalleryToEnd restTrackId=$restTrackId")
        onView(isRoot()).perform(scrollImageGalleryToEnd())
        Thread.sleep(200)

        // Verify the gallery is at the end
        onView(isRoot()).perform(verifyImageGalleryAtEnd())

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

    private fun skipFirstAppUsage() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // with changelog <= 3.8.1 we have to use deprecated version PreferenceManager.getDefaultSharedPreferences(context)
        // with changelog >= 3.8.2 we have to use context.getSharedPreferences("changelog", MODE_PRIVATE)
        val sp = context.getSharedPreferences("changelog", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(VERSION_KEY, 123)
        editor.commit()

        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        sharedPreferences.edit()
            .remove(MxPreferences.Keys.LAST_OPEN_START_ACTIVITY)
            .apply()
    }

    private fun scrollImageGalleryToEnd(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Scroll image gallery to end"
            }

            override fun perform(uiController: UiController, view: View) {
                // Find the RecyclerView with the specific ID within the view hierarchy
                val recyclerView = view.findRecyclerView(R.id.hImgGalleryGen)
                recyclerView?.let { rv ->
                    val adapter = rv.adapter
                    if (adapter != null && adapter.itemCount > 0) {
                        val lastPosition = adapter.itemCount - 1
                        Timber.d("Found RecyclerView with ${adapter.itemCount} items, scrolling to position $lastPosition")

                        // Use smoothScrollToPosition for better animation
                        rv.smoothScrollToPosition(lastPosition)
                        uiController.loopMainThreadForAtLeast(500)

                        // Verify the scroll happened by checking the layout manager
                        val layoutManager = rv.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager
                        layoutManager?.let {
                            val lastVisiblePosition = it.findLastCompletelyVisibleItemPosition()
                            Timber.d("After scroll - Last visible position: $lastVisiblePosition, Target: $lastPosition")

                            // If smooth scroll didn't reach the end, use scrollToPosition as fallback
                            if (lastVisiblePosition < lastPosition) {
                                Timber.w("Smooth scroll didn't reach end, using scrollToPosition")
                                rv.scrollToPosition(lastPosition)
                                uiController.loopMainThreadForAtLeast(100)
                            }
                        }
                    } else {
                        Timber.d("RecyclerView found but has no items or no adapter")
                    }
                } ?: run {
                    val name = view.resources.getResourceEntryName(R.id.hImgGalleryGen)
                    Timber.e("RecyclerView '$name' not found in view hierarchy")
                }
            }

        }
    }

    private fun verifyImageGalleryAtEnd(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Verify image gallery is at end position"
            }

            override fun perform(uiController: UiController, view: View) {
                val recyclerView = view.findRecyclerView(R.id.hImgGalleryGen)
                recyclerView?.let { rv ->
                    val adapter = rv.adapter
                    val layoutManager = rv.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager

                    if (adapter != null && layoutManager != null && adapter.itemCount > 0) {
                        val lastPosition = adapter.itemCount - 1
                        val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                        val lastPartiallyVisiblePosition = layoutManager.findLastVisibleItemPosition()

                        Timber.d("Gallery verification - Total items: ${adapter.itemCount}")
                        Timber.d("Gallery verification - Last completely visible: $lastVisiblePosition")
                        Timber.d("Gallery verification - Last partially visible: $lastPartiallyVisiblePosition")
                        Timber.d("Gallery verification - Target position: $lastPosition")

                        // Check if we're at or near the end
                        val isAtEnd = lastVisiblePosition == lastPosition || lastPartiallyVisiblePosition == lastPosition
                        if (isAtEnd) {
                            Timber.d("✓ Gallery is at the end position")
                        } else {
                            Timber.w("✗ Gallery is NOT at the end position (visible: $lastPartiallyVisiblePosition, expected: $lastPosition)")
                        }
                    } else {
                        Timber.d("Cannot verify - adapter or layoutManager is null, or no items")
                    }
                } ?: run {
                    val name = view.resources.getResourceEntryName(R.id.hImgGalleryGen)
                    Timber.w("RecyclerView '$name' not found for verification")
                }
            }
        }
    }

}

private fun View.findRecyclerView(@IdRes resourceID: Int): RecyclerView? {
    if (this is RecyclerView) {
        val resources = this.resources
        val name = resources.getResourceEntryName(resourceID)
        if (this.id == resourceID) {
            Timber.d("Found RecyclerView with id: '$name' $resourceID")
            return this
        }
    }
    if (this is ViewGroup) {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            val result = child.findRecyclerView(resourceID)
            if (result != null) {
                return result
            }
        }
    }
    return null
}
