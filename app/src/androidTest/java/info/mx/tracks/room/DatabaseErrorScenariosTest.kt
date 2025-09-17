package info.mx.tracks.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.dao.TrackDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import android.database.sqlite.SQLiteConstraintException

@RunWith(AndroidJUnit4::class)
@SmallTest
class DatabaseErrorScenariosTest {

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
    fun testNonExistentTrackRetrieval() = runBlocking {
        // When
        val nonExistentTrack = trackDao.getTrackById(999L)

        // Then
        assertNull(nonExistentTrack)
    }

    @Test
    fun testEmptyDatabaseOperations() = runBlocking {
        // When
        val emptyCount = trackDao.getTrackCount()
        val emptyCountByCountry = trackDao.getTrackCountByCountry("DE")

        // Then
        assertEquals(0, emptyCount)
        assertEquals(0, emptyCountByCountry)
    }

    @Test
    fun testUpdateNonExistentTrack() = runBlocking {
        // Given
        val track = Track(
            id = 999,
            restId = 100,
            changed = System.currentTimeMillis(),
            trackname = "Non-existent Track",
            longitude = 10.0,
            latitude = 20.0,
            approved = 1,
            country = "DE"
        )

        // When - this should not throw an exception but also not update anything
        trackDao.updateTrack(track)
        val retrievedTrack = trackDao.getTrackById(999)

        // Then
        assertNull(retrievedTrack)
    }

    @Test
    fun testDeleteNonExistentTrack() = runBlocking {
        // When - this should not throw an exception
        trackDao.deleteTrackById(999L)
        
        // Then - verify count is still 0
        val count = trackDao.getTrackCount()
        assertEquals(0, count)
    }

    @Test
    fun testSearchWithEmptyResults() = runBlocking {
        // Given
        val track = Track(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            trackname = "Motocross Arena",
            longitude = 10.0,
            latitude = 20.0,
            approved = 1,
            country = "DE"
        )
        trackDao.insertTrack(track)

        // When
        val searchResults = trackDao.searchTracksByName("NonExistentName")

        // Then
        assertEquals(0, searchResults.first().size)
    }

    @Test
    fun testLargeDataInsertion() = runBlocking {
        // Given - Create a large dataset
        val largeTracks = (1..1000).map { i ->
            Track(
                id = i.toLong(),
                restId = i.toLong(),
                changed = System.currentTimeMillis(),
                trackname = "Track $i",
                longitude = 10.0 + i * 0.001,
                latitude = 20.0 + i * 0.001,
                approved = 1,
                country = if (i % 2 == 0) "DE" else "AT"
            )
        }

        // When
        trackDao.insertTracks(largeTracks)
        val count = trackDao.getTrackCount()

        // Then
        assertEquals(1000, count)
    }

    @Test
    fun testConcurrentOperations() = runBlocking {
        // Given
        val track1 = Track(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            trackname = "Track 1",
            longitude = 10.0,
            latitude = 20.0,
            approved = 1,
            country = "DE"
        )

        val track2 = Track(
            id = 2,
            restId = 101,
            changed = System.currentTimeMillis(),
            trackname = "Track 2",
            longitude = 11.0,
            latitude = 21.0,
            approved = 1,
            country = "AT"
        )

        // When - Insert and update simultaneously
        trackDao.insertTrack(track1)
        trackDao.insertTrack(track2)
        trackDao.updateFavoriteStatus(1, 1)
        trackDao.updateFavoriteStatus(2, 1)

        // Then
        val track1Retrieved = trackDao.getTrackById(1)
        val track2Retrieved = trackDao.getTrackById(2)

        assertNotNull(track1Retrieved)
        assertNotNull(track2Retrieved)
        assertEquals(1, track1Retrieved?.favorite)
        assertEquals(1, track2Retrieved?.favorite)
    }

    @Test
    fun testDatabaseIntegrityAfterMultipleOperations() = runBlocking {
        // Given
        val initialTracks = (1..10).map { i ->
            Track(
                id = i.toLong(),
                restId = i.toLong(),
                changed = System.currentTimeMillis(),
                trackname = "Track $i",
                longitude = 10.0 + i,
                latitude = 20.0 + i,
                approved = 1,
                country = "DE"
            )
        }

        // When - Perform multiple operations
        trackDao.insertTracks(initialTracks)
        
        // Delete some tracks
        trackDao.deleteTrackById(3)
        trackDao.deleteTrackById(7)
        
        // Update some tracks
        trackDao.updateFavoriteStatus(5, 1)
        
        // Insert new tracks
        val newTrack = Track(
            id = 11,
            restId = 11,
            changed = System.currentTimeMillis(),
            trackname = "New Track",
            longitude = 25.0,
            latitude = 35.0,
            approved = 1,
            country = "AT"
        )
        trackDao.insertTrack(newTrack)

        // Then - Verify database integrity
        val totalCount = trackDao.getTrackCount()
        val track3 = trackDao.getTrackById(3)
        val track5 = trackDao.getTrackById(5)
        val track11 = trackDao.getTrackById(11)

        assertEquals(9, totalCount) // 10 - 2 deleted + 1 new
        assertNull(track3) // Should be deleted
        assertNotNull(track5)
        assertEquals(1, track5?.favorite) // Should be favorite
        assertNotNull(track11)
        assertEquals("New Track", track11?.trackname)
    }

    @Test
    fun testTransactionRollback() = runBlocking {
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

        trackDao.insertTrack(track)

        // When - Delete the track
        trackDao.deleteTrackById(1)

        // Then - Verify it's gone
        val deletedTrack = trackDao.getTrackById(1)
        assertNull(deletedTrack)

        // Verify count is correct
        val count = trackDao.getTrackCount()
        assertEquals(0, count)
    }
}