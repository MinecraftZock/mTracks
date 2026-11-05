package info.mx.tracks.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import info.mx.tracks.room.entity.CapturedLatLng
import info.mx.tracks.service.RecalculateDistance
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface CapturedLatLngDao {

    @get:Query("SELECT * FROM CapturedLatLng order by id desc")
    val all: Flowable<List<CapturedLatLng>>

    @get:Query("SELECT * FROM CapturedLatLng where aktion != \"" + RecalculateDistance.ACTION_IGNORED + "\" order by id")
    val allNonIgnored: Flowable<List<CapturedLatLng>>

    @get:Query("SELECT * FROM CapturedLatLng where aktion != \"" + RecalculateDistance.ACTION_IGNORED + "\" order by id desc limit 1")
    val lastNonIgnoredLocation: Maybe<CapturedLatLng>

    @Query("SELECT * FROM CapturedLatLng WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<CapturedLatLng>

    @Query("SELECT count(*) FROM CapturedLatLng")
    fun count(): Int

    //    @Query("SELECT * FROM Comment WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    //    User findByName(String first, String last)

    @Insert
    fun insertAll(vararg capturedLatLngs: CapturedLatLng)

    @Delete
    fun delete(capturedLatLng: CapturedLatLng)

}
