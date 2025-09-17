package info.mx.tracks.base

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import info.mx.tracks.ops.ImportIdlingResource
import info.mx.tracks.room.DatabaseManager
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.entity.Country
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit

/**
 * Base test class that sets up Room database with test data for UI tests
 */
abstract class BaseRoomTest {

    @get:Rule
    var nameRule = TestName()

    @Before
    fun registerIdlingResource() {
        IdlingPolicies.setIdlingResourceTimeout(4, TimeUnit.MINUTES)
        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES)
        IdlingRegistry.getInstance().register(ImportIdlingResource.countingIdlingResource)
        
        // Setup test data in Room database
        setupTestData()
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(ImportIdlingResource.countingIdlingResource)
    }

    private fun setupTestData() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val repository = DatabaseManager.getTrackRepository(context)
        
        // Clear existing data
        repository.clearAllData()
        
        // Setup test countries
        val countries = listOf(
            Country(id = 1, country = "DE", show = 1),
            Country(id = 2, country = "AT", show = 1),
            Country(id = 3, country = "CH", show = 1),
            Country(id = 4, country = "FR", show = 1),
            Country(id = 5, country = "IT", show = 1)
        )
        repository.insertCountries(countries)
        
        // Setup test tracks
        val tracks = generateTestTracks()
        repository.insertTracks(tracks)
    }
    
    private fun generateTestTracks(): List<Track> {
        val countries = listOf("DE", "AT", "CH", "FR", "IT")
        val trackTypes = listOf("Motocross", "Supercross", "Enduro", "Trial", "Speedway")
        val cities = listOf("Berlin", "Munich", "Vienna", "Zurich", "Paris", "Rome", "Hamburg", "Cologne")
        
        return (1..50).map { i ->
            Track(
                id = i.toLong(),
                restId = (100 + i).toLong(),
                changed = System.currentTimeMillis(),
                trackname = "${trackTypes[i % trackTypes.size]} Arena ${cities[i % cities.size]} $i",
                longitude = 8.0 + (i % 10) * 0.5,
                latitude = 47.0 + (i % 8) * 0.3,
                approved = 1,
                country = countries[i % countries.size],
                favorite = if (i % 7 == 0) 1 else 0,
                tracklength = 800 + (i % 5) * 200,
                difficulty = i % 5 + 1,
                distance2location = i * 100,
                kidstrack = if (i % 10 == 0) 1 else 0
            )
        }
    }

    /**
     * Subclasses can override this to add additional test data
     */
    protected open fun addAdditionalTestData() = runBlocking {
        // Override in subclasses if needed
    }
}