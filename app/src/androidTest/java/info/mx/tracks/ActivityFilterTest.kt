package info.mx.tracks

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.prefs.MxPreferences
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

    override fun additionBeforeAction() {
        InstrumentationRegistry.getInstrumentation().targetContext.clearSharedPrefs()
    }

    private fun Context.clearSharedPrefs() =
        this.getSharedPreferences(MxPreferences.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().clear().commit()

    @Test
    fun filterTest() {
        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
//        Espresso.pressBack()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2") })
    }

}
