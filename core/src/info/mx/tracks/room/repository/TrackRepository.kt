package info.mx.tracks.room.repository

import info.mx.tracks.room.MxCoreDatabase
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.entity.Picture
import info.mx.tracks.room.entity.Favorit
import info.mx.tracks.room.entity.Country
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File

class TrackRepository(private val database: MxCoreDatabase) {
    
    private val trackDao = database.trackDao()
    private val pictureDao = database.pictureDao()
    private val favoritDao = database.favoritDao()
    private val countryDao = database.countryDao()
    
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
    
    // Favorit operations
    fun getAllFavorits(): Flow<List<Favorit>> = favoritDao.getAllFavorits()
    
    suspend fun getFavoritById(id: Long): Favorit? = favoritDao.getFavoritById(id)
    
    suspend fun getFavoritByTrackRestId(trackRestId: Long): Favorit? = favoritDao.getFavoritByTrackRestId(trackRestId)
    
    fun getFavoriteTrackIds(): Flow<List<Long>> = favoritDao.getFavoriteTrackIds()
    
    suspend fun insertFavorit(favorit: Favorit): Long = favoritDao.insertFavorit(favorit)
    
    suspend fun deleteFavorit(favorit: Favorit) = favoritDao.deleteFavorit(favorit)
    
    suspend fun deleteFavoritByTrackRestId(trackRestId: Long) = favoritDao.deleteFavoritByTrackRestId(trackRestId)
    
    // Country operations
    fun getAllCountries(): Flow<List<Country>> = countryDao.getAllCountries()
    
    suspend fun getCountryById(id: Long): Country? = countryDao.getCountryById(id)
    
    suspend fun getCountryByCode(countryCode: String): Country? = countryDao.getCountryByCode(countryCode)
    
    fun getVisibleCountries(): Flow<List<Country>> = countryDao.getVisibleCountries()
    
    suspend fun insertCountry(country: Country): Long = countryDao.insertCountry(country)
    
    suspend fun insertCountries(countries: List<Country>) = countryDao.insertCountries(countries)
    
    suspend fun updateCountryVisibility(id: Long, show: Long) = countryDao.updateCountryVisibility(id, show)
    
    suspend fun updateCountryVisibilityByCode(countryCode: String, show: Long) = countryDao.updateCountryVisibilityByCode(countryCode, show)
    
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
        favoritDao.deleteAllFavorits()
        countryDao.deleteAllCountries()
        // TODO: Add other DAOs when they are implemented
    }
}