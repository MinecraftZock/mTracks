package info.mx.tracks.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import info.mx.tracks.room.entity.Country
import info.mx.tracks.room.entity.Favorit
import info.mx.tracks.room.dao.CountryDao
import info.mx.tracks.room.dao.FavoritDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AdditionalEntitiesDaoTest {

    private lateinit var database: MxCoreDatabase
    private lateinit var countryDao: CountryDao
    private lateinit var favoritDao: FavoritDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MxCoreDatabase::class.java
        ).allowMainThreadQueries().build()
        
        countryDao = database.countryDao()
        favoritDao = database.favoritDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testCountryOperations() = runBlocking {
        // Given
        val countries = listOf(
            Country(id = 1, country = "DE", show = 1),
            Country(id = 2, country = "AT", show = 1),
            Country(id = 3, country = "CH", show = 0)
        )

        // When
        countryDao.insertCountries(countries)
        val allCountries = countryDao.getAllCountries().first()
        val visibleCountries = countryDao.getVisibleCountries().first()
        val hiddenCountries = countryDao.getHiddenCountries().first()

        // Then
        assertEquals(3, allCountries.size)
        assertEquals(2, visibleCountries.size)
        assertEquals(1, hiddenCountries.size)
        
        assertTrue(visibleCountries.any { it.country == "DE" })
        assertTrue(visibleCountries.any { it.country == "AT" })
        assertTrue(hiddenCountries.any { it.country == "CH" })
    }

    @Test
    fun testCountryVisibilityUpdate() = runBlocking {
        // Given
        val country = Country(id = 1, country = "DE", show = 0)
        countryDao.insertCountry(country)

        // When
        countryDao.updateCountryVisibilityByCode("DE", 1)
        val updatedCountry = countryDao.getCountryByCode("DE")

        // Then
        assertNotNull(updatedCountry)
        assertEquals(1, updatedCountry?.show)
    }

    @Test
    fun testFavoritOperations() = runBlocking {
        // Given
        val favorits = listOf(
            Favorit(id = 1, trackRestId = 100),
            Favorit(id = 2, trackRestId = 101),
            Favorit(id = 3, trackRestId = 102)
        )

        // When
        favoritDao.insertFavorits(favorits)
        val allFavorits = favoritDao.getAllFavorits().first()
        val favoriteTrackIds = favoritDao.getFavoriteTrackIds().first()

        // Then
        assertEquals(3, allFavorits.size)
        assertEquals(3, favoriteTrackIds.size)
        assertTrue(favoriteTrackIds.contains(100L))
        assertTrue(favoriteTrackIds.contains(101L))
        assertTrue(favoriteTrackIds.contains(102L))
    }

    @Test
    fun testFavoritByTrackRestId() = runBlocking {
        // Given
        val favorit = Favorit(id = 1, trackRestId = 100)
        favoritDao.insertFavorit(favorit)

        // When
        val retrievedFavorit = favoritDao.getFavoritByTrackRestId(100)
        val nonExistentFavorit = favoritDao.getFavoritByTrackRestId(999)

        // Then
        assertNotNull(retrievedFavorit)
        assertEquals(100L, retrievedFavorit?.trackRestId)
        assertNull(nonExistentFavorit)
    }

    @Test
    fun testDeleteFavoritByTrackRestId() = runBlocking {
        // Given
        val favorit = Favorit(id = 1, trackRestId = 100)
        favoritDao.insertFavorit(favorit)

        // When
        favoritDao.deleteFavoritByTrackRestId(100)
        val deletedFavorit = favoritDao.getFavoritByTrackRestId(100)

        // Then
        assertNull(deletedFavorit)
    }

    @Test
    fun testCountryCount() = runBlocking {
        // Given
        val countries = listOf(
            Country(id = 1, country = "DE", show = 1),
            Country(id = 2, country = "AT", show = 1),
            Country(id = 3, country = "CH", show = 0)
        )

        // When
        countryDao.insertCountries(countries)
        val totalCount = countryDao.getCountryCount()
        val visibleCount = countryDao.getVisibleCountryCount()

        // Then
        assertEquals(3, totalCount)
        assertEquals(2, visibleCount)
    }

    @Test
    fun testFavoritConflictReplace() = runBlocking {
        // Given
        val favorit1 = Favorit(id = 1, trackRestId = 100)
        val favorit2 = Favorit(id = 1, trackRestId = 101) // Same ID, different trackRestId

        // When
        favoritDao.insertFavorit(favorit1)
        favoritDao.insertFavorit(favorit2) // Should replace

        val retrievedFavorit = favoritDao.getFavoritById(1)
        val count = favoritDao.getFavoritCount()

        // Then
        assertNotNull(retrievedFavorit)
        assertEquals(101L, retrievedFavorit?.trackRestId)
        assertEquals(1, count) // Should still be 1, not 2
    }

    @Test
    fun testCountryConflictReplace() = runBlocking {
        // Given
        val country1 = Country(id = 1, country = "DE", show = 0)
        val country2 = Country(id = 1, country = "AT", show = 1) // Same ID, different country

        // When
        countryDao.insertCountry(country1)
        countryDao.insertCountry(country2) // Should replace

        val retrievedCountry = countryDao.getCountryById(1)
        val count = countryDao.getCountryCount()

        // Then
        assertNotNull(retrievedCountry)
        assertEquals("AT", retrievedCountry?.country)
        assertEquals(1, retrievedCountry?.show)
        assertEquals(1, count) // Should still be 1, not 2
    }
}