package info.mx.tracks

import android.Manifest
import android.graphics.Bitmap
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.settings.ActivityFilter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityFilterTest : BaseSyncTest() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityFilter>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun filterTest() {
        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
//        Espresso.pressBack()
        onView(withId(R.id.filter_only_open)).check(matches(isNotChecked()))
//            .perform(click()).check(matches(isChecked()))

        onView(withId(R.id.filter_only_open)).perform(click())
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2") })
    }

}
