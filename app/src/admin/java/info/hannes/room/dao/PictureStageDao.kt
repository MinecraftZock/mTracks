package info.hannes.room.dao

import androidx.room.*
import info.hannes.room.entity.PictureStage
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureStageDao {
    
    @Query("SELECT * FROM pictures")
    fun getAllPictureStages(): Flow<List<PictureStage>>
    
    @Query("SELECT * FROM pictures WHERE _id = :id")
    suspend fun getPictureStageById(id: Long): PictureStage?
    
    @Query("SELECT * FROM pictures WHERE restId = :restId")
    suspend fun getPictureStageByRestId(restId: Long): PictureStage?
    
    @Query("SELECT * FROM pictures WHERE trackId = :trackId")
    fun getPictureStagesByTrackId(trackId: Long): Flow<List<PictureStage>>
    
    @Query("SELECT * FROM pictures WHERE trackRestId = :trackRestId")
    fun getPictureStagesByTrackRestId(trackRestId: Long): Flow<List<PictureStage>>
    
    @Query("SELECT * FROM pictures WHERE uninteressant = 0")
    fun getInterestingPictureStages(): Flow<List<PictureStage>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictureStage(picture: PictureStage): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictureStages(pictures: List<PictureStage>)
    
    @Update
    suspend fun updatePictureStage(picture: PictureStage)
    
    @Delete
    suspend fun deletePictureStage(picture: PictureStage)
    
    @Query("DELETE FROM pictures WHERE _id = :id")
    suspend fun deletePictureStageById(id: Long)
    
    @Query("DELETE FROM pictures")
    suspend fun deleteAllPictureStages()
    
    @Query("UPDATE pictures SET uninteressant = :uninteressant WHERE _id = :id")
    suspend fun updateInterestingStatus(id: Long, uninteressant: Int)
    
    @Query("SELECT COUNT(*) FROM pictures")
    suspend fun getPictureStageCount(): Int
    
    @Query("SELECT COUNT(*) FROM pictures WHERE trackId = :trackId")
    suspend fun getPictureStageCountByTrackId(trackId: Long): Int
}