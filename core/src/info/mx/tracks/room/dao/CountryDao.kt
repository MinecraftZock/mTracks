package info.mx.tracks.room.dao

import androidx.room.*
import info.mx.tracks.room.entity.Country
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    
    @Query("SELECT * FROM country")
    fun getAllCountries(): Flow<List<Country>>
    
    @Query("SELECT * FROM country WHERE _id = :id")
    suspend fun getCountryById(id: Long): Country?
    
    @Query("SELECT * FROM country WHERE country = :countryCode")
    suspend fun getCountryByCode(countryCode: String): Country?
    
    @Query("SELECT * FROM country WHERE show = 1")
    fun getVisibleCountries(): Flow<List<Country>>
    
    @Query("SELECT * FROM country WHERE show = 0")
    fun getHiddenCountries(): Flow<List<Country>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: Country): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<Country>)
    
    @Update
    suspend fun updateCountry(country: Country)
    
    @Delete
    suspend fun deleteCountry(country: Country)
    
    @Query("DELETE FROM country WHERE _id = :id")
    suspend fun deleteCountryById(id: Long)
    
    @Query("DELETE FROM country")
    suspend fun deleteAllCountries()
    
    @Query("UPDATE country SET show = :show WHERE _id = :id")
    suspend fun updateCountryVisibility(id: Long, show: Long)
    
    @Query("UPDATE country SET show = :show WHERE country = :countryCode")
    suspend fun updateCountryVisibilityByCode(countryCode: String, show: Long)
    
    @Query("SELECT COUNT(*) FROM country")
    suspend fun getCountryCount(): Int
    
    @Query("SELECT COUNT(*) FROM country WHERE show = 1")
    suspend fun getVisibleCountryCount(): Int
}