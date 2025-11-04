package info.mx.tracks.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.dao.TrackDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TrackDaoTest {

    private lateinit var database: MxCoreDatabase
    private lateinit var trackDao: TrackDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MxCoreDatabase::class.java
        ).allowMainThreadQueries().build()
        
        trackDao = database.trackDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetTrack() = runBlocking {
        // Given
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

        // When
        trackDao.insertTrack(track)
        val retrievedTrack = trackDao.getTrackById(1)

        // Then
        assertNotNull(retrievedTrack)
        assertEquals("Test Track", retrievedTrack?.trackname)
        assertEquals("DE", retrievedTrack?.country)
        assertEquals(10.0, retrievedTrack?.longitude, 0.001)
        assertEquals(20.0, retrievedTrack?.latitude, 0.001)
    }

    @Test
    fun insertMultipleTracksAndGetAll() = runBlocking {
        // Given
        val tracks = listOf(
            Track(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                trackname = "Track 1",
                longitude = 10.0,
                latitude = 20.0,
                approved = 1,
                country = "DE"
            ),
            Track(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                trackname = "Track 2",
                longitude = 11.0,
                latitude = 21.0,
                approved = 1,
                country = "AT"
            )
        )

        // When
        trackDao.insertTracks(tracks)
        val allTracks = trackDao.getAllTracks().first()

        // Then
        assertEquals(2, allTracks.size)
        assertTrue(allTracks.any { it.trackname == "Track 1" })
        assertTrue(allTracks.any { it.trackname == "Track 2" })
    }

    @Test
    fun getTracksByCountry() = runBlocking {
        // Given
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

        // When
        trackDao.insertTracks(tracks)
        val germanTracks = trackDao.getTracksByCountry("DE").first()

        // Then
        assertEquals(1, germanTracks.size)
        assertEquals("German Track", germanTracks.first().trackname)
    }

    @Test
    fun searchTracksByName() = runBlocking {
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
                trackname = "Supercross Stadium",
                longitude = 11.0,
                latitude = 21.0,
                approved = 1,
                country = "DE"
            )
        )

        // When
        trackDao.insertTracks(tracks)
        val searchResults = trackDao.searchTracksByName("Berlin").first()

        // Then
        assertEquals(1, searchResults.size)
        assertEquals("Motocross Arena Berlin", searchResults.first().trackname)
    }

    @Test
    fun updateTrack() = runBlocking {
        // Given
        val track = Track(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            trackname = "Original Name",
            longitude = 10.0,
            latitude = 20.0,
            approved = 1,
            country = "DE"
        )

        trackDao.insertTrack(track)

        // When
        val updatedTrack = track.copy(trackname = "Updated Name")
        trackDao.updateTrack(updatedTrack)
        val retrievedTrack = trackDao.getTrackById(1)

        // Then
        assertNotNull(retrievedTrack)
        assertEquals("Updated Name", retrievedTrack?.trackname)
    }

    @Test
    fun deleteTrack() = runBlocking {
        // Given
        val track = Track(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            trackname = "Track to Delete",
            longitude = 10.0,
            latitude = 20.0,
            approved = 1,
            country = "DE"
        )

        trackDao.insertTrack(track)

        // When
        trackDao.deleteTrackById(1)
        val retrievedTrack = trackDao.getTrackById(1)

        // Then
        assertNull(retrievedTrack)
    }

    @Test
    fun updateFavoriteStatus() = runBlocking {
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

        trackDao.insertTrack(track)

        // When
        trackDao.updateFavoriteStatus(1, 1)
        val favoriteTrack = trackDao.getTrackById(1)

        // Then
        assertNotNull(favoriteTrack)
        assertEquals(1, favoriteTrack?.favorite)
    }

    @Test
    fun getFavoriteTracks() = runBlocking {
        // Given
        val tracks = listOf(
            Track(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                trackname = "Favorite Track",
                longitude = 10.0,
                latitude = 20.0,
                approved = 1,
                country = "DE",
                favorite = 1
            ),
            Track(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                trackname = "Regular Track",
                longitude = 11.0,
                latitude = 21.0,
                approved = 1,
                country = "DE",
                favorite = 0
            )
        )

        // When
        trackDao.insertTracks(tracks)
        val favoriteTracks = trackDao.getFavoriteTracks().first()

        // Then
        assertEquals(1, favoriteTracks.size)
        assertEquals("Favorite Track", favoriteTracks.first().trackname)
    }

    @Test
    fun getTrackCount() = runBlocking {
        // Given
        val tracks = listOf(
            Track(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                trackname = "Track 1",
                longitude = 10.0,
                latitude = 20.0,
                approved = 1,
                country = "DE"
            ),
            Track(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                trackname = "Track 2",
                longitude = 11.0,
                latitude = 21.0,
                approved = 1,
                country = "AT"
            )
        )

        // When
        trackDao.insertTracks(tracks)
        val count = trackDao.getTrackCount()
        val countByCountry = trackDao.getTrackCountByCountry("DE")

        // Then
        assertEquals(2, count)
        assertEquals(1, countByCountry)
    }
}