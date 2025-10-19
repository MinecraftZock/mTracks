package info.mx.tracks.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.entity.Picture
import info.mx.tracks.room.entity.Favorit
import info.mx.tracks.room.entity.Country
import info.mx.tracks.room.repository.TrackRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TrackRepositoryTest {

    private lateinit var database: MxCoreDatabase
    private lateinit var repository: TrackRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MxCoreDatabase::class.java
        ).allowMainThreadQueries().build()
        
        repository = TrackRepository(database)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testCompleteTrackWorkflow() = runBlocking {
        // Given - Create countries
        val countries = listOf(
            Country(id = 1, country = "DE", show = 1),
            Country(id = 2, country = "AT", show = 1)
        )
        repository.insertCountries(countries)

        // Create tracks
        val tracks = listOf(
            Track(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                trackname = "German Track",
                longitude = 10.0,
                latitude = 20.0,
                approved = 1,
                country = "DE"
            ),
            Track(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                trackname = "Austrian Track",
                longitude = 11.0,
                latitude = 21.0,
                approved = 1,
                country = "AT"
            )
        )
        repository.insertTracks(tracks)

        // Create pictures for tracks
        val pictures = listOf(
            Picture(
                id = 1,
                restId = 200,
                changed = System.currentTimeMillis(),
                username = "user1",
                comment = "Picture for German track",
                trackRestId = 100,
                approved = 1
            ),
            Picture(
                id = 2,
                restId = 201,
                changed = System.currentTimeMillis(),
                username = "user2",
                comment = "Picture for Austrian track",
                trackRestId = 101,
                approved = 1
            )
        )
        repository.insertPictures(pictures)

        // Create favorites
        val favorit = Favorit(id = 1, trackRestId = 100)
        repository.insertFavorit(favorit)

        // When - Test operations
        val allTracks = repository.getAllTracks().first()
        val germanTracks = repository.getTracksByCountry("DE").first()
        val trackPictures = repository.getPicturesByTrack(100).first()
        val favoriteIds = repository.getFavoriteTrackIds().first()
        val visibleCountries = repository.getVisibleCountries().first()

        // Then - Verify results
        assertEquals(2, allTracks.size)
        assertEquals(1, germanTracks.size)
        assertEquals("German Track", germanTracks.first().trackname)
        
        assertEquals(1, trackPictures.size)
        assertEquals("Picture for German track", trackPictures.first().comment)
        
        assertEquals(1, favoriteIds.size)
        assertEquals(100L, favoriteIds.first())
        
        assertEquals(2, visibleCountries.size)
        assertTrue(visibleCountries.any { it.country == "DE" })
        assertTrue(visibleCountries.any { it.country == "AT" })
    }

    @Test
    fun testFavoriteTrackOperations() = runBlocking {
        // Given
        val track = Track(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            trackname = "Test Track",
            longitude = 10.0,
            latitude = 20.0,
            approved = 1,
            country = "DE",
            favorite = 0
        )
        repository.insertTrack(track)

        // When - Add to favorites using repository
        val favorit = Favorit(id = 1, trackRestId = 100)
        repository.insertFavorit(favorit)
        
        // Also update track favorite status
        repository.updateFavoriteStatus(1, 1)

        // Then
        val favoriteTrackIds = repository.getFavoriteTrackIds().first()
        val updatedTrack = repository.getTrackById(1)
        val favoriteFromRepo = repository.getFavoritByTrackRestId(100)

        assertTrue(favoriteTrackIds.contains(100L))
        assertEquals(1, updatedTrack?.favorite)
        assertNotNull(favoriteFromRepo)
    }

    @Test
    fun testCountryVisibilityManagement() = runBlocking {
        // Given
        val countries = listOf(
            Country(id = 1, country = "DE", show = 1),
            Country(id = 2, country = "AT", show = 0),
            Country(id = 3, country = "CH", show = 1)
        )
        repository.insertCountries(countries)

        // When
        repository.updateCountryVisibilityByCode("AT", 1) // Make Austria visible
        repository.updateCountryVisibilityByCode("CH", 0) // Hide Switzerland

        // Then
        val visibleCountries = repository.getVisibleCountries().first()
        val hiddenCountries = repository.getHiddenCountries().first()

        assertEquals(2, visibleCountries.size) // DE and AT
        assertEquals(1, hiddenCountries.size) // CH
        
        assertTrue(visibleCountries.any { it.country == "DE" })
        assertTrue(visibleCountries.any { it.country == "AT" })
        assertTrue(hiddenCountries.any { it.country == "CH" })
    }

    @Test
    fun testSearchAndFilterOperations() = runBlocking {
        // Given
        val tracks = listOf(
            Track(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                trackname = "Motocross Arena Berlin",
                longitude = 10.0,
                latitude = 20.0,
                approved = 1,
                country = "DE"
            ),
            Track(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                trackname = "Supercross Stadium Munich",
                longitude = 11.0,
                latitude = 21.0,
                approved = 1,
                country = "DE"
            ),
            Track(
                id = 3,
                restId = 102,
                changed = System.currentTimeMillis(),
                trackname = "Dirt Track Vienna",
                longitude = 12.0,
                latitude = 22.0,
                approved = 1,
                country = "AT"
            )
        )
        repository.insertTracks(tracks)

        // When
        val berlinTracks = repository.searchTracksByName("Berlin").first()
        val germanTracks = repository.getTracksByCountry("DE").first()
        val allTracks = repository.getAllTracks().first()

        // Then
        assertEquals(1, berlinTracks.size)
        assertEquals("Motocross Arena Berlin", berlinTracks.first().trackname)
        
        assertEquals(2, germanTracks.size)
        assertTrue(germanTracks.all { it.country == "DE" })
        
        assertEquals(3, allTracks.size)
    }

    @Test
    fun testClearAllData() = runBlocking {
        // Given - Add data to all tables
        val track = Track(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            trackname = "Test Track",
            longitude = 10.0,
            latitude = 20.0,
            approved = 1,
            country = "DE"
        )
        repository.insertTrack(track)

        val picture = Picture(
            id = 1,
            restId = 200,
            changed = System.currentTimeMillis(),
            username = "user1",
            comment = "Test picture",
            trackRestId = 100,
            approved = 1
        )
        repository.insertPicture(picture)

        val favorit = Favorit(id = 1, trackRestId = 100)
        repository.insertFavorit(favorit)

        val country = Country(id = 1, country = "DE", show = 1)
        repository.insertCountry(country)

        // Verify data exists
        assertEquals(1, repository.getTrackCount())

        // When
        repository.clearAllData()

        // Then
        assertEquals(0, repository.getTrackCount())
        assertEquals(0, repository.getAllPictures().first().size)
        assertEquals(0, repository.getAllFavorits().first().size)
        assertEquals(0, repository.getAllCountries().first().size)
    }
}