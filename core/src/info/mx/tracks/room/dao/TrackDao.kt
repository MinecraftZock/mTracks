package info.mx.tracks.room.dao

import androidx.room.*
import info.mx.tracks.room.entity.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    
    @Query("SELECT * FROM tracks")
    fun getAllTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE _id = :id")
    suspend fun getTrackById(id: Long): Track?
    
    @Query("SELECT * FROM tracks WHERE restId = :restId")
    suspend fun getTrackByRestId(restId: Long): Track?
    
    @Query("SELECT * FROM tracks WHERE country = :country")
    fun getTracksByCountry(country: String): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE trackname LIKE '%' || :name || '%'")
    fun searchTracksByName(name: String): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE favorite = 1")
    fun getFavoriteTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    fun getTracksInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): Flow<List<Track>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<Track>)
    
    @Update
    suspend fun updateTrack(track: Track)
    
    @Delete
    suspend fun deleteTrack(track: Track)
    
    @Query("DELETE FROM tracks WHERE _id = :id")
    suspend fun deleteTrackById(id: Long)
    
    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()
    
    @Query("UPDATE tracks SET favorite = :favorite WHERE _id = :id")
    suspend fun updateFavoriteStatus(id: Long, favorite: Int)
    
    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int
    
    @Query("SELECT COUNT(*) FROM tracks WHERE country = :country")
    suspend fun getTrackCountByCountry(country: String): Int
}