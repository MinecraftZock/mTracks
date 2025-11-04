package info.mx.tracks.room.dao

import androidx.room.*
import info.mx.tracks.room.entity.Favorit
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritDao {
    
    @Query("SELECT * FROM favorits")
    fun getAllFavorits(): Flow<List<Favorit>>
    
    @Query("SELECT * FROM favorits WHERE _id = :id")
    suspend fun getFavoritById(id: Long): Favorit?
    
    @Query("SELECT * FROM favorits WHERE trackRestId = :trackRestId")
    suspend fun getFavoritByTrackRestId(trackRestId: Long): Favorit?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorit(favorit: Favorit): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorits(favorits: List<Favorit>)
    
    @Update
    suspend fun updateFavorit(favorit: Favorit)
    
    @Delete
    suspend fun deleteFavorit(favorit: Favorit)
    
    @Query("DELETE FROM favorits WHERE _id = :id")
    suspend fun deleteFavoritById(id: Long)
    
    @Query("DELETE FROM favorits WHERE trackRestId = :trackRestId")
    suspend fun deleteFavoritByTrackRestId(trackRestId: Long)
    
    @Query("DELETE FROM favorits")
    suspend fun deleteAllFavorits()
    
    @Query("SELECT COUNT(*) FROM favorits")
    suspend fun getFavoritCount(): Int
    
    @Query("SELECT trackRestId FROM favorits")
    fun getFavoriteTrackIds(): Flow<List<Long>>
}