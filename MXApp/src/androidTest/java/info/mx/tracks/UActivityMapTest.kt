package info.mx.tracks

import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.map.ActivityMapExtension
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UActivityMapTest: BaseSyncTest() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityMapExtension>()

    @Test
    fun smokeTestSimplyStart() {
        Espresso.pressBack()
        Thread.sleep(100)
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-open")
    }

}
