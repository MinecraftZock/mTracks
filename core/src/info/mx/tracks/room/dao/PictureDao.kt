package info.mx.tracks.room.dao

import androidx.room.*
import info.mx.tracks.room.entity.Picture
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {
    
    @Query("SELECT * FROM pictures")
    fun getAllPictures(): Flow<List<Picture>>
    
    @Query("SELECT * FROM pictures WHERE _id = :id")
    suspend fun getPictureById(id: Long): Picture?
    
    @Query("SELECT * FROM pictures WHERE restId = :restId")
    suspend fun getPictureByRestId(restId: Long): Picture?
    
    @Query("SELECT * FROM pictures WHERE trackRestId = :trackRestId")
    fun getPicturesByTrack(trackRestId: Long): Flow<List<Picture>>
    
    @Query("SELECT * FROM pictures WHERE approved = 1")
    fun getApprovedPictures(): Flow<List<Picture>>
    
    @Query("SELECT * FROM pictures WHERE deleted = 0")
    fun getActivePictures(): Flow<List<Picture>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: Picture): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictures(pictures: List<Picture>)
    
    @Update
    suspend fun updatePicture(picture: Picture)
    
    @Delete
    suspend fun deletePicture(picture: Picture)
    
    @Query("DELETE FROM pictures WHERE _id = :id")
    suspend fun deletePictureById(id: Long)
    
    @Query("DELETE FROM pictures")
    suspend fun deleteAllPictures()
    
    @Query("UPDATE pictures SET approved = :approved WHERE _id = :id")
    suspend fun updateApprovalStatus(id: Long, approved: Int)
    
    @Query("UPDATE pictures SET deleted = :deleted WHERE _id = :id")
    suspend fun updateDeletedStatus(id: Long, deleted: Int)
    
    @Query("SELECT COUNT(*) FROM pictures")
    suspend fun getPictureCount(): Int
    
    @Query("SELECT COUNT(*) FROM pictures WHERE trackRestId = :trackRestId")
    suspend fun getPictureCountByTrack(trackRestId: Long): Int
}