package info.mx.tracks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.Suppress
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class SplashTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(ActivitySplash::class.java)

    @Test
    @Suppress
    fun splashTest() {
        val appCompatImageButton = onView(
                allOf(withContentDescription("Nach oben"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()))
        appCompatImageButton.perform(click())

        val appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        withParent(allOf(withClassName(`is`("com.android.internal.widget.ButtonBarLayout")),
                                withParent(withClassName(`is`("android.widget.LinearLayout"))))),
                        isDisplayed()))
        appCompatButton.perform(click())

        val appCompatImageButton2 = onView(
                allOf(withContentDescription("offen"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()))
        appCompatImageButton2.perform(click())

        val appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Filter"), isDisplayed()))
        appCompatCheckedTextView.perform(click())

        val appCompatImageButton3 = onView(
                allOf(withContentDescription("Nach oben"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()))
        appCompatImageButton3.perform(click())

        val appCompatTextView = onView(
                allOf(withId(android.R.id.title), isDisplayed()))
        appCompatTextView.perform(click())

        val appCompatTextView2 = onView(
                allOf(withId(android.R.id.title), withText("ABC"), isDisplayed()))
        appCompatTextView2.perform(click())

        val appCompatImageButton4 = onView(
                allOf(withContentDescription("offen"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()))
        appCompatImageButton4.perform(click())

        val appCompatCheckedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Landkarte"), isDisplayed()))
        appCompatCheckedTextView2.perform(click())

        val appCompatImageButton5 = onView(
                allOf(withContentDescription("offen"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()))
        appCompatImageButton5.perform(click())

        val appCompatCheckedTextView3 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Strecken"), isDisplayed()))
        appCompatCheckedTextView3.perform(click())
    }

}