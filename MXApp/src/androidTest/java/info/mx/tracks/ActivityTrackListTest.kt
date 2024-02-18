package info.mx.tracks

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.location.LocationServices
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.tracklist.ActivityTrackList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityTrackListTest : BaseSyncTest() {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ActivityTrackList>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun showTrackListTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocationServices.getFusedLocationProviderClient(context).setMockMode(true)
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 42.125
        location.longitude = 55.123

        LocationServices.getFusedLocationProviderClient(context).setMockLocation(location)

        // This is the first time settings activity with always changed version number
        //onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")
        Espresso.pressBack()
        onView(isRoot()).captureToBitmap().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2")
    }

}
