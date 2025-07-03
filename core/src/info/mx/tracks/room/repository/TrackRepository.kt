package info.mx.tracks.room.repository

import info.mx.tracks.room.MxCoreDatabase
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.entity.Picture
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File

class TrackRepository(private val database: MxCoreDatabase) {
    
    private val trackDao = database.trackDao()
    private val pictureDao = database.pictureDao()
    
    fun getAllTracks(): Flow<List<Track>> = trackDao.getAllTracks()
    
    suspend fun getTrackById(id: Long): Track? = trackDao.getTrackById(id)
    
    suspend fun getTrackByRestId(restId: Long): Track? = trackDao.getTrackByRestId(restId)
    
    fun getTracksByCountry(country: String): Flow<List<Track>> = trackDao.getTracksByCountry(country)
    
    fun searchTracksByName(name: String): Flow<List<Track>> = trackDao.searchTracksByName(name)
    
    fun getFavoriteTracks(): Flow<List<Track>> = trackDao.getFavoriteTracks()
    
    suspend fun insertTrack(track: Track): Long = trackDao.insertTrack(track)
    
    suspend fun insertTracks(tracks: List<Track>) = trackDao.insertTracks(tracks)
    
    suspend fun updateTrack(track: Track) = trackDao.updateTrack(track)
    
    suspend fun deleteTrack(track: Track) = trackDao.deleteTrack(track)
    
    suspend fun deleteTrackById(id: Long) = trackDao.deleteTrackById(id)
    
    suspend fun updateFavoriteStatus(id: Long, favorite: Int) = trackDao.updateFavoriteStatus(id, favorite)
    
    suspend fun getTrackCount(): Int = trackDao.getTrackCount()
    
    fun getAllPictures(): Flow<List<Picture>> = pictureDao.getAllPictures()
    
    suspend fun getPictureById(id: Long): Picture? = pictureDao.getPictureById(id)
    
    fun getPicturesByTrack(trackRestId: Long): Flow<List<Picture>> = pictureDao.getPicturesByTrack(trackRestId)
    
    suspend fun insertPicture(picture: Picture): Long = pictureDao.insertPicture(picture)
    
    suspend fun insertPictures(pictures: List<Picture>) = pictureDao.insertPictures(pictures)
    
    suspend fun updatePicture(picture: Picture) = pictureDao.updatePicture(picture)
    
    suspend fun deletePicture(picture: Picture) = pictureDao.deletePicture(picture)
    
    suspend fun deletePictureById(id: Long) = pictureDao.deletePictureById(id)
    
    suspend fun clearAllData() {
        // Delete local files first
        val pictures = pictureDao.getAllPictures()
        pictures.collect { pictureList ->
            pictureList.forEach { picture ->
                picture.localfile?.let { localFile ->
                    if (localFile.isNotEmpty()) {
                        val file = File(localFile)
                        if (file.exists()) {
                            Timber.d("Delete local file %s", localFile)
                            file.delete()
                        }
                    }
                }
                picture.localthumb?.let { localThumb ->
                    if (localThumb.isNotEmpty()) {
                        val file = File(localThumb)
                        if (file.exists()) {
                            Timber.d("Delete thumb file %s", localThumb)
                            file.delete()
                        }
                    }
                }
            }
        }
        
        // Clear all database tables
        pictureDao.deleteAllPictures()
        trackDao.deleteAllTracks()
        // TODO: Add other DAOs when they are implemented
    }
}