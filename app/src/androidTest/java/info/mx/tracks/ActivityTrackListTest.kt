package info.mx.tracks

import android.database.Cursor
import android.graphics.Bitmap
import android.widget.ListView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.robotoworks.mechanoid.db.SQuery
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.CountryRecord
import info.mx.core_generated.sqlite.MxInfoDBContract.Country
import info.mx.core_generated.sqlite.MxInfoDBContract.TracksGesSum
import info.mx.tracks.base.BaseSyncTest
import info.mx.tracks.tracklist.ActivityTrackList
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityTrackListTest : BaseSyncTest<ActivityTrackList>() {

    @Test
    fun munichSetupReplacesPreviousCountrySelection() {
        Country.newBuilder().setShow(0).update(SQuery.newQuery(), true)
        Country.newBuilder().setShow(1)
            .update(SQuery.newQuery().expr(Country.COUNTRY, SQuery.Op.EQ, "US"), true)
        MxPreferences.instance.firstTimeLocation = true
        MxPreferences.instance.onlyOpen = true

        prepareMunichCountryFilter()

        assertTrue("Previous track filter must be reset", !MxPreferences.instance.onlyOpen)
        startActivity<ActivityTrackList>()
        assertSearchResultsRespectCountries("initial list")
    }

    @Test
    fun showTrackListTest() {
        startActivity<ActivityTrackList>()

        // This is the first time settings activity with always changed version number
        // Espresso.pressBackUnconditionally()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-init") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(10)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-10") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(20)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-20") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(30)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-30") })

        Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.isAssignableFrom(ListView::class.java))
            .atPosition(40)
            .perform(scrollTo())
        Thread.sleep(1000)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-scroll-40") })
    }

    @Test
    fun searchTest() {
        startActivity<ActivityTrackList>()

        onView(withId(R.id.menu_search)).perform(click())

        for (term in listOf("aa", "de", "ee", "ck", "sch", "oo", "tz", "ff")) {
            onView(withId(R.id.search_src_text)).perform(clearText(), typeText(term))
            Espresso.onIdle()
            assertMunichCountryFilter()
            assertSearchResultsRespectCountries(term)
            onView(isRoot()).perform(captureToBitmap { bitmap: Bitmap ->
                bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-search-$term")
            })
        }
    }


    @Test
    fun tab() {
        startActivity<ActivityTrackList>()

        // This is the first time settings activity with always changed version number
        // Espresso.pressBackUnconditionally()
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-init") })

        onView(withId(R.id.view_pager)).perform(swipeRight())
        Thread.sleep(200)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-star") })
        onView(withId(R.id.view_pager)).perform(swipeRight())
        Thread.sleep(200)
        onView(isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-distance") })
    }

    private fun assertSearchResultsRespectCountries(term: String) {
        val allowedCountries = SQuery.newQuery().expr(Country.SHOW, SQuery.Op.EQ, 1)
            .select<CountryRecord>(Country.CONTENT_URI).map { it.country }.toSet()
        // Check the actual list cursor, including off-screen rows, rather than rebuilding the query.
        onView(allOf(withId(R.id.list_overview), withParent(isDisplayed()))).check { view, error ->
            if (error != null) throw error
            val adapter = (view as ListView).adapter as CursorAdapter
            val cursor: Cursor = requireNotNull(adapter.cursor) { "Search '$term' has not loaded" }
            if (term == "de") {
                assertTrue("Search 'de' must contain results, not just an empty filtered list", cursor.count > 0)
            }
            val position = cursor.position
            try {
                cursor.moveToPosition(-1)
                while (cursor.moveToNext()) {
                    val country = cursor.getString(cursor.getColumnIndexOrThrow(TracksGesSum.COUNTRY))
                    val track = cursor.getString(cursor.getColumnIndexOrThrow(TracksGesSum.TRACKNAME))
                    assertTrue("Search '$term' returned US track '$track'", country != "US")
                    assertTrue("Search '$term' returned excluded country '$country' for '$track'", country in allowedCountries)
                }
            } finally {
                cursor.moveToPosition(position)
            }
        }
    }
}
