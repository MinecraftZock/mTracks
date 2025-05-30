package info.mx.tracks

import android.graphics.Bitmap
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.map.MapIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UActivityMapTest : BaseSyncTest() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityMapExtension>()

    @Before
    fun startMainActivityFromHomeScreen() {
        IdlingRegistry.getInstance().register(MapIdlingResource.countingIdlingResource)
    }

    @After
    fun cleanUp() {
        IdlingRegistry.getInstance().unregister(MapIdlingResource.countingIdlingResource)
    }

    @Test
    fun smokeTestSimplyStart() {
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-open") })
    }

}
