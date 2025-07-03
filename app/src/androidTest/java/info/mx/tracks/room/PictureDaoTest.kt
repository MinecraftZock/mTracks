package info.mx.tracks.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import info.mx.tracks.room.entity.Picture
import info.mx.tracks.room.dao.PictureDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class PictureDaoTest {

    private lateinit var database: MxCoreDatabase
    private lateinit var pictureDao: PictureDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MxCoreDatabase::class.java
        ).allowMainThreadQueries().build()
        
        pictureDao = database.pictureDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetPicture() = runBlocking {
        // Given
        val picture = Picture(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            username = "testuser",
            comment = "Test comment",
            trackRestId = 200,
            approved = 1,
            deleted = 0
        )

        // When
        pictureDao.insertPicture(picture)
        val retrievedPicture = pictureDao.getPictureById(1)

        // Then
        assertNotNull(retrievedPicture)
        assertEquals("testuser", retrievedPicture?.username)
        assertEquals("Test comment", retrievedPicture?.comment)
        assertEquals(200, retrievedPicture?.trackRestId)
    }

    @Test
    fun getPicturesByTrack() = runBlocking {
        // Given
        val pictures = listOf(
            Picture(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                username = "user1",
                comment = "Picture 1",
                trackRestId = 200,
                approved = 1,
                deleted = 0
            ),
            Picture(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                username = "user2",
                comment = "Picture 2",
                trackRestId = 200,
                approved = 1,
                deleted = 0
            ),
            Picture(
                id = 3,
                restId = 102,
                changed = System.currentTimeMillis(),
                username = "user3",
                comment = "Picture 3",
                trackRestId = 201,
                approved = 1,
                deleted = 0
            )
        )

        // When
        pictureDao.insertPictures(pictures)
        val trackPictures = pictureDao.getPicturesByTrack(200).first()

        // Then
        assertEquals(2, trackPictures.size)
        assertTrue(trackPictures.all { it.trackRestId == 200L })
    }

    @Test
    fun getApprovedPictures() = runBlocking {
        // Given
        val pictures = listOf(
            Picture(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                username = "user1",
                comment = "Approved picture",
                trackRestId = 200,
                approved = 1,
                deleted = 0
            ),
            Picture(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                username = "user2",
                comment = "Pending picture",
                trackRestId = 200,
                approved = 0,
                deleted = 0
            )
        )

        // When
        pictureDao.insertPictures(pictures)
        val approvedPictures = pictureDao.getApprovedPictures().first()

        // Then
        assertEquals(1, approvedPictures.size)
        assertEquals("Approved picture", approvedPictures.first().comment)
    }

    @Test
    fun updateApprovalStatus() = runBlocking {
        // Given
        val picture = Picture(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            username = "user1",
            comment = "Test picture",
            trackRestId = 200,
            approved = 0,
            deleted = 0
        )

        pictureDao.insertPicture(picture)

        // When
        pictureDao.updateApprovalStatus(1, 1)
        val updatedPicture = pictureDao.getPictureById(1)

        // Then
        assertNotNull(updatedPicture)
        assertEquals(1, updatedPicture?.approved)
    }

    @Test
    fun updateDeletedStatus() = runBlocking {
        // Given
        val picture = Picture(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            username = "user1",
            comment = "Test picture",
            trackRestId = 200,
            approved = 1,
            deleted = 0
        )

        pictureDao.insertPicture(picture)

        // When
        pictureDao.updateDeletedStatus(1, 1)
        val updatedPicture = pictureDao.getPictureById(1)

        // Then
        assertNotNull(updatedPicture)
        assertEquals(1, updatedPicture?.deleted)
    }

    @Test
    fun getActivePictures() = runBlocking {
        // Given
        val pictures = listOf(
            Picture(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                username = "user1",
                comment = "Active picture",
                trackRestId = 200,
                approved = 1,
                deleted = 0
            ),
            Picture(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                username = "user2",
                comment = "Deleted picture",
                trackRestId = 200,
                approved = 1,
                deleted = 1
            )
        )

        // When
        pictureDao.insertPictures(pictures)
        val activePictures = pictureDao.getActivePictures().first()

        // Then
        assertEquals(1, activePictures.size)
        assertEquals("Active picture", activePictures.first().comment)
    }

    @Test
    fun deletePicture() = runBlocking {
        // Given
        val picture = Picture(
            id = 1,
            restId = 100,
            changed = System.currentTimeMillis(),
            username = "user1",
            comment = "Picture to delete",
            trackRestId = 200,
            approved = 1,
            deleted = 0
        )

        pictureDao.insertPicture(picture)

        // When
        pictureDao.deletePictureById(1)
        val retrievedPicture = pictureDao.getPictureById(1)

        // Then
        assertNull(retrievedPicture)
    }

    @Test
    fun getPictureCount() = runBlocking {
        // Given
        val pictures = listOf(
            Picture(
                id = 1,
                restId = 100,
                changed = System.currentTimeMillis(),
                username = "user1",
                comment = "Picture 1",
                trackRestId = 200,
                approved = 1,
                deleted = 0
            ),
            Picture(
                id = 2,
                restId = 101,
                changed = System.currentTimeMillis(),
                username = "user2",
                comment = "Picture 2",
                trackRestId = 201,
                approved = 1,
                deleted = 0
            )
        )

        // When
        pictureDao.insertPictures(pictures)
        val totalCount = pictureDao.getPictureCount()
        val trackCount = pictureDao.getPictureCountByTrack(200)

        // Then
        assertEquals(2, totalCount)
        assertEquals(1, trackCount)
    }
}