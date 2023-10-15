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
import info.mx.tracks.settings.ActivityFilterCountry
import info.mx.tracks.tools.ReverseProtocol
import org.hamcrest.Matchers.anything
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
    fun smokeTestSimplyStart() {
        onData(anything())
//        onData(instanceOf(MyAdapterItem::class.java))
            .atPosition(0)
            .usingAdapterViewProtocol(ReverseProtocol())
            .perform(click())

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
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-4")
    }

}
