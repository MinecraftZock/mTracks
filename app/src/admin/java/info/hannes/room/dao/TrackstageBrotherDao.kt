package info.hannes.room.dao

import androidx.room.*
import info.hannes.room.entity.TrackstageBrother
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackstageBrotherDao {
    
    @Query("SELECT * FROM trackstageBrother")
    fun getAllTrackstageBrothers(): Flow<List<TrackstageBrother>>
    
    @Query("SELECT * FROM trackstageBrother WHERE _id = :id")
    suspend fun getTrackstageBrotherById(id: Long): TrackstageBrother?
    
    @Query("SELECT * FROM trackstageBrother WHERE restId = :restId")
    suspend fun getTrackstageBrotherByRestId(restId: Long): TrackstageBrother?
    
    @Query("SELECT * FROM trackstageBrother WHERE trackRestId = :trackRestId")
    fun getTrackstageBrothersByTrack(trackRestId: Long): Flow<List<TrackstageBrother>>
    
    @Query("SELECT * FROM trackstageBrother WHERE Country = :country")
    fun getTrackstageBrothersByCountry(country: String): Flow<List<TrackstageBrother>>
    
    @Query("SELECT * FROM trackstageBrother WHERE Trackname LIKE '%' || :name || '%'")
    fun searchTrackstageBrothersByName(name: String): Flow<List<TrackstageBrother>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackstageBrother(brother: TrackstageBrother): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackstageBrothers(brothers: List<TrackstageBrother>)
    
    @Update
    suspend fun updateTrackstageBrother(brother: TrackstageBrother)
    
    @Delete
    suspend fun deleteTrackstageBrother(brother: TrackstageBrother)
    
    @Query("DELETE FROM trackstageBrother WHERE _id = :id")
    suspend fun deleteTrackstageBrotherById(id: Long)
    
    @Query("DELETE FROM trackstageBrother")
    suspend fun deleteAllTrackstageBrothers()
    
    @Query("SELECT COUNT(*) FROM trackstageBrother")
    suspend fun getTrackstageBrotherCount(): Int
    
    @Query("SELECT COUNT(*) FROM trackstageBrother WHERE Country = :country")
    suspend fun getTrackstageBrotherCountByCountry(country: String): Int
}