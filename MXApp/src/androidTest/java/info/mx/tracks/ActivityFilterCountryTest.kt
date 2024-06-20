package info.mx.tracks

import android.Manifest
import android.widget.ListView
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.settings.ActivityFilterCountry
import org.hamcrest.CoreMatchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityFilterCountryTest : BaseSyncTest() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityFilterCountry>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    @AllowFlaky(attempts = 2)
    fun smokeTestSimplyStart() {
        onView(withId(R.id.action_settings_filter_country)).perform(click())
        Thread.sleep(1000)

        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
//        Espresso.pressBack()
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2")
        onView(withId(android.R.id.list)).perform(swipeDown())
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-3")
        onData(anything())
            .inAdapterView(isAssignableFrom(ListView::class.java))
            .atPosition(3)
            .perform(click())
        Thread.sleep(1000)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-4")

        onView(withId(android.R.id.list)).perform(swipeDown())
        onView(withId(android.R.id.list)).perform(swipeDown())
        onView(withId(android.R.id.list)).perform(swipeDown())
        onView(withId(android.R.id.list)).perform(swipeDown())
        // hide USA
        onData(anything())
            .inAdapterView(isAssignableFrom(ListView::class.java))
            .atPosition(42)
            .perform(click())
        Thread.sleep(1000)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-5")
    }

}
