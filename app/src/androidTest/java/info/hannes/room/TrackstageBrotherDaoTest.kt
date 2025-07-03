package info.hannes.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import info.hannes.room.entity.TrackstageBrother
import info.hannes.room.dao.TrackstageBrotherDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TrackstageBrotherDaoTest {

    private lateinit var database: MxAdminDatabase
    private lateinit var trackstageBrotherDao: TrackstageBrotherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MxAdminDatabase::class.java
        ).allowMainThreadQueries().build()
        
        trackstageBrotherDao = database.trackstageBrotherDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetTrackstageBrother() = runBlocking {
        // Given
        val brother = TrackstageBrother(
            id = 1,
            restId = 100,
            trackRestId = 200,
            created = System.currentTimeMillis(),
            trackname = "Brother Track",
            longitude = 10.0,
            latitude = 20.0,
            country = "DE",
            androidid = "test-android-id"
        )

        // When
        trackstageBrotherDao.insertTrackstageBrother(brother)
        val retrievedBrother = trackstageBrotherDao.getTrackstageBrotherById(1)

        // Then
        assertNotNull(retrievedBrother)
        assertEquals("Brother Track", retrievedBrother?.trackname)
        assertEquals("DE", retrievedBrother?.country)
        assertEquals(10.0, retrievedBrother?.longitude, 0.001)
        assertEquals(20.0, retrievedBrother?.latitude, 0.001)
    }

    @Test
    fun getTrackstageBrothersByTrack() = runBlocking {
        // Given
        val brothers = listOf(
            TrackstageBrother(
                id = 1,
                restId = 100,
                trackRestId = 200,
                created = System.currentTimeMillis(),
                trackname = "Brother 1",
                longitude = 10.0,
                latitude = 20.0,
                country = "DE"
            ),
            TrackstageBrother(
                id = 2,
                restId = 101,
                trackRestId = 200,
                created = System.currentTimeMillis(),
                trackname = "Brother 2",
                longitude = 11.0,
                latitude = 21.0,
                country = "DE"
            ),
            TrackstageBrother(
                id = 3,
                restId = 102,
                trackRestId = 201,
                created = System.currentTimeMillis(),
                trackname = "Brother 3",
                longitude = 12.0,
                latitude = 22.0,
                country = "AT"
            )
        )

        // When
        trackstageBrotherDao.insertTrackstageBrothers(brothers)
        val trackBrothers = trackstageBrotherDao.getTrackstageBrothersByTrack(200).first()

        // Then
        assertEquals(2, trackBrothers.size)
        assertTrue(trackBrothers.all { it.trackRestId == 200L })
    }

    @Test
    fun getTrackstageBrothersByCountry() = runBlocking {
        // Given
        val brothers = listOf(
            TrackstageBrother(
                id = 1,
                restId = 100,
                trackRestId = 200,
                created = System.currentTimeMillis(),
                trackname = "German Brother",
                longitude = 10.0,
                latitude = 20.0,
                country = "DE"
            ),
            TrackstageBrother(
                id = 2,
                restId = 101,
                trackRestId = 201,
                created = System.currentTimeMillis(),
                trackname = "Austrian Brother",
                longitude = 11.0,
                latitude = 21.0,
                country = "AT"
            )
        )

        // When
        trackstageBrotherDao.insertTrackstageBrothers(brothers)
        val germanBrothers = trackstageBrotherDao.getTrackstageBrothersByCountry("DE").first()

        // Then
        assertEquals(1, germanBrothers.size)
        assertEquals("German Brother", germanBrothers.first().trackname)
    }

    @Test
    fun searchTrackstageBrothersByName() = runBlocking {
        // Given
        val brothers = listOf(
            TrackstageBrother(
                id = 1,
                restId = 100,
                trackRestId = 200,
                created = System.currentTimeMillis(),
                trackname = "Motocross Arena Berlin",
                longitude = 10.0,
                latitude = 20.0,
                country = "DE"
            ),
            TrackstageBrother(
                id = 2,
                restId = 101,
                trackRestId = 201,
                created = System.currentTimeMillis(),
                trackname = "Supercross Stadium",
                longitude = 11.0,
                latitude = 21.0,
                country = "DE"
            )
        )

        // When
        trackstageBrotherDao.insertTrackstageBrothers(brothers)
        val searchResults = trackstageBrotherDao.searchTrackstageBrothersByName("Berlin").first()

        // Then
        assertEquals(1, searchResults.size)
        assertEquals("Motocross Arena Berlin", searchResults.first().trackname)
    }

    @Test
    fun updateTrackstageBrother() = runBlocking {
        // Given
        val brother = TrackstageBrother(
            id = 1,
            restId = 100,
            trackRestId = 200,
            created = System.currentTimeMillis(),
            trackname = "Original Name",
            longitude = 10.0,
            latitude = 20.0,
            country = "DE"
        )

        trackstageBrotherDao.insertTrackstageBrother(brother)

        // When
        val updatedBrother = brother.copy(trackname = "Updated Name")
        trackstageBrotherDao.updateTrackstageBrother(updatedBrother)
        val retrievedBrother = trackstageBrotherDao.getTrackstageBrotherById(1)

        // Then
        assertNotNull(retrievedBrother)
        assertEquals("Updated Name", retrievedBrother?.trackname)
    }

    @Test
    fun deleteTrackstageBrother() = runBlocking {
        // Given
        val brother = TrackstageBrother(
            id = 1,
            restId = 100,
            trackRestId = 200,
            created = System.currentTimeMillis(),
            trackname = "Brother to Delete",
            longitude = 10.0,
            latitude = 20.0,
            country = "DE"
        )

        trackstageBrotherDao.insertTrackstageBrother(brother)

        // When
        trackstageBrotherDao.deleteTrackstageBrotherById(1)
        val retrievedBrother = trackstageBrotherDao.getTrackstageBrotherById(1)

        // Then
        assertNull(retrievedBrother)
    }

    @Test
    fun getTrackstageBrotherCount() = runBlocking {
        // Given
        val brothers = listOf(
            TrackstageBrother(
                id = 1,
                restId = 100,
                trackRestId = 200,
                created = System.currentTimeMillis(),
                trackname = "Brother 1",
                longitude = 10.0,
                latitude = 20.0,
                country = "DE"
            ),
            TrackstageBrother(
                id = 2,
                restId = 101,
                trackRestId = 201,
                created = System.currentTimeMillis(),
                trackname = "Brother 2",
                longitude = 11.0,
                latitude = 21.0,
                country = "AT"
            )
        )

        // When
        trackstageBrotherDao.insertTrackstageBrothers(brothers)
        val totalCount = trackstageBrotherDao.getTrackstageBrotherCount()
        val countByCountry = trackstageBrotherDao.getTrackstageBrotherCountByCountry("DE")

        // Then
        assertEquals(2, totalCount)
        assertEquals(1, countByCountry)
    }

    @Test
    fun insertConflictReplace() = runBlocking {
        // Given
        val brother1 = TrackstageBrother(
            id = 1,
            restId = 100,
            trackRestId = 200,
            created = System.currentTimeMillis(),
            trackname = "Original Brother",
            longitude = 10.0,
            latitude = 20.0,
            country = "DE"
        )

        val brother2 = TrackstageBrother(
            id = 1, // Same ID
            restId = 100,
            trackRestId = 200,
            created = System.currentTimeMillis(),
            trackname = "Replaced Brother",
            longitude = 11.0,
            latitude = 21.0,
            country = "AT"
        )

        // When
        trackstageBrotherDao.insertTrackstageBrother(brother1)
        trackstageBrotherDao.insertTrackstageBrother(brother2) // Should replace
        
        val retrievedBrother = trackstageBrotherDao.getTrackstageBrotherById(1)
        val totalCount = trackstageBrotherDao.getTrackstageBrotherCount()

        // Then
        assertNotNull(retrievedBrother)
        assertEquals("Replaced Brother", retrievedBrother?.trackname)
        assertEquals("AT", retrievedBrother?.country)
        assertEquals(1, totalCount) // Should still be 1, not 2
    }
}