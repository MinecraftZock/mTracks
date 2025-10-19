package info.hannes.room.dao

import androidx.room.*
import info.hannes.room.entity.Videos
import kotlinx.coroutines.flow.Flow

@Dao
interface VideosDao {
    
    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<Videos>>
    
    @Query("SELECT * FROM videos WHERE _id = :id")
    suspend fun getVideoById(id: Long): Videos?
    
    @Query("SELECT * FROM videos WHERE restId = :restId")
    suspend fun getVideoByRestId(restId: Long): Videos?
    
    @Query("SELECT * FROM videos WHERE trackId = :trackId")
    fun getVideosByTrackId(trackId: Long): Flow<List<Videos>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Videos): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<Videos>)
    
    @Update
    suspend fun updateVideo(video: Videos)
    
    @Delete
    suspend fun deleteVideo(video: Videos)
    
    @Query("DELETE FROM videos WHERE _id = :id")
    suspend fun deleteVideoById(id: Long)
    
    @Query("DELETE FROM videos")
    suspend fun deleteAllVideos()
    
    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getVideoCount(): Int
    
    @Query("SELECT COUNT(*) FROM videos WHERE trackId = :trackId")
    suspend fun getVideoCountByTrackId(trackId: Long): Int
}