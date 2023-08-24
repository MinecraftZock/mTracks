package info.mx.tracks.room

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface TracksDistanceDao {

    @get:Query("SELECT * FROM TracksDistance order by distance")
    val all: Flowable<List<TracksDistance>>

    @Query("DELETE FROM TracksDistance")
    fun deleteAll()

    @Insert
    fun insertAll(vararg arrayOfTracksDistances: TracksDistance)

    @Delete
    fun delete(tracksDistance: TracksDistance)

}
