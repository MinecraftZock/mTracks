package info.hannes.room.dao

import androidx.room.*
import info.hannes.room.entity.TrackstageRid
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackstageRidDao {
    
    @Query("SELECT * FROM trackstageRid")
    fun getAllTrackstageRids(): Flow<List<TrackstageRid>>
    
    @Query("SELECT * FROM trackstageRid WHERE _id = :id")
    suspend fun getTrackstageRidById(id: Long): TrackstageRid?
    
    @Query("SELECT * FROM trackstageRid WHERE restId = :restId")
    suspend fun getTrackstageRidByRestId(restId: Long): TrackstageRid?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackstageRid(rid: TrackstageRid): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackstageRids(rids: List<TrackstageRid>)
    
    @Update
    suspend fun updateTrackstageRid(rid: TrackstageRid)
    
    @Delete
    suspend fun deleteTrackstageRid(rid: TrackstageRid)
    
    @Query("DELETE FROM trackstageRid WHERE _id = :id")
    suspend fun deleteTrackstageRidById(id: Long)
    
    @Query("DELETE FROM trackstageRid")
    suspend fun deleteAllTrackstageRids()
    
    @Query("SELECT COUNT(*) FROM trackstageRid")
    suspend fun getTrackstageRidCount(): Int
}