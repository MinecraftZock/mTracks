package info.mx.tracks.uiautomator

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import info.mx.tracks.BuildConfig
import info.mx.tracks.R
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * https://developer.android.com/training/testing/ui-testing/uiautomator-testing#java
 * https://www.guru99.com/uiautomatorviewer-tutorial.html
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    private lateinit var device: UiDevice

    @get:Rule
    var nameRule = TestName()

    /**
     * Create launcher Intent Use PackageManager to get the launcher package name
     *
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private val launcherPackageName: String
        get() {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            val pm = getApplicationContext<Context>().packageManager
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return resolveInfo!!.activityInfo.packageName
        }

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(getInstrumentation())

        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage = launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT.toLong())

        // Launch the app
        val context = getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(MX_PACKAGE)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(MX_PACKAGE).depth(0)), LAUNCH_TIMEOUT.toLong())
    }

    @Test
    fun checkPreconditions() {
        assertThat(device, notNullValue())
        val bundle = Bundle()
        bundle.putString("checkPreconditions", "1")
        getInstrumentation().sendStatus(0, bundle)
    }

//    @Test
    fun startDetailFromList() {
        acceptChangesIfNeeded()
        allowPermissionsIfNeeded()

        if (onSettingsActivity()) {
            device.pressBack()
        }
        onView(isRoot()).perform(waitId(R.id.layoutPoiHeaderMain, TimeUnit.SECONDS.toMillis(WAIT_FOR_IMPORT)))
        device.findObject(By.res(MX_PACKAGE, "layoutPoiHeaderMain"))
                .click()

        clickOkIfNeeded()

        device.pressMenu()

        clickOkIfNeeded()

        device.pressBack()

        val bundle = Bundle()
        bundle.putString("startDetailFromList", "1")
        getInstrumentation().sendStatus(0, bundle)
    }

    @Test
    fun openMap() {
        acceptChangesIfNeeded()
        allowPermissionsIfNeeded()

        if (onSettingsActivity()) {
            device.pressBack()
        }

        onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1")

        val openButton = onView(
                Matchers.allOf(ViewMatchers.withContentDescription("open"),
                        ViewMatchers.withParent(withId(R.id.toolbar)),
                        ViewMatchers.isDisplayed())
        )
        onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2")
        openButton.perform(ViewActions.click())
        onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-3")

        val menuMap = device.findObject(UiSelector().className("android.widget.CheckedTextView").text("Map"))
        if (menuMap.exists()) {
            try {
                menuMap.click()
            } catch (exception: UiObjectNotFoundException) {
                Log.e("Menu not exist", "design_menu_item_text")
            }
        }

        val clusterIcon = device.findObject(
                UiSelector().className("android.widget.ImageView")
                        .resourceId("$MX_PACKAGE:id/map_cluster_btn"))
        assertTrue(clusterIcon.exists())

        clickOkIfNeeded()

        val bundle = Bundle()
        bundle.putString("openMap", "0")
        getInstrumentation().sendStatus(0, bundle)
        onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-4")
    }

    private fun allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            val allowPermissions = device.findObject(
                    UiSelector().className("android.widget.Button")
                            .resourceId("com.android.packageinstaller:id/permission_allow_button"))
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click()
                } catch (e: UiObjectNotFoundException) {
                    Log.e(this.javaClass.name, "There is no permissions dialog to interact with ", e)
                }

            }
        }
    }

    private fun acceptChangesIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            val okButton = device.findObject(UiSelector().text(getApplicationContext<Context>().getString(android.R.string.ok)))
            if (okButton.exists()) {
                try {
                    okButton.click()
                } catch (e: UiObjectNotFoundException) {
                    Timber.e(e)
                }

            }
        }
    }

    private fun clickOkIfNeeded() {
        val okButton = device.findObject(
                UiSelector().className("android.widget.Button")
                        .resourceId("android:id/button1"))
        if (okButton.exists()) {
            try {
                okButton.click()
            } catch (ignored: UiObjectNotFoundException) {
            }

        }
    }

    private fun onSettingsActivity(): Boolean {
        val version = device.findObject(
                UiSelector().className("android.widget.TextView")
                        .resourceId("$MX_PACKAGE:id/setting_version"))
        return version.exists()
    }

    companion object {

        private const val MX_PACKAGE = BuildConfig.APPLICATION_ID
        private const val LAUNCH_TIMEOUT = 5000
        private const val WAIT_FOR_IMPORT = 255L

        /**
         * Perform action of waiting for a specific view id.
         * https://stackoverflow.com/questions/21417954/espresso-thread-sleep
         */
        @Suppress("SameParameterValue")
        private fun waitId(viewId: Int, millis: Long): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return isRoot()
                }

                override fun getDescription(): String {
                    return "wait for a specific view with id <$viewId> during $millis millis."
                }

                override fun perform(uiController: UiController, view: View) {
                    uiController.loopMainThreadUntilIdle()
                    val startTime = System.currentTimeMillis()
                    val endTime = startTime + millis
                    val viewMatcher = withId(viewId)

                    do {
                        for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                            // found view with required ID
                            if (viewMatcher.matches(child)) {
                                return
                            }
                        }

                        uiController.loopMainThreadForAtLeast(50)
                    } while (System.currentTimeMillis() < endTime)

                    // timeout happens
                    throw PerformException.Builder()
                            .withActionDescription(this.description)
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(TimeoutException())
                            .build()
                }
            }
        }
    }

}
