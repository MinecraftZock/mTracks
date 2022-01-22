package info.mx.tracks.room

import androidx.room.*
import info.mx.tracks.room.entity.Picture
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {

    @Query("SELECT * FROM Picture WHERE id = :id")
    fun getById(id: Long): Picture?

    @Query("DELETE FROM Picture where approved = -1")
    fun deleteNotApproved()

    @Query("SELECT * FROM Picture WHERE trackId = :trackRestId and deleted != 1 order by changed desc")
    fun getByTrackId(trackRestId: Long): Flow<List<Picture>>

    @get:Query("SELECT * FROM Picture")
    val all: List<Picture>

    @Query("SELECT max(changed) FROM Picture")
    fun getNewest(): Int

    @Query("SELECT * FROM Picture WHERE trackId = :trackRestId order by changed desc")
    fun flowableAllByTrackId(trackRestId: Long): Flowable<List<Picture>>

    @Query("SELECT * FROM Picture WHERE trackId = :trackRestId order by changed desc")
    fun loadAllByTrackId(trackRestId: Long): Flow<List<Picture>>

    @Query("SELECT * FROM Picture WHERE id < 0 order by changed desc")
    fun eventAllNonPushed(): List<Picture>

    @Query("SELECT * FROM Picture WHERE id < 0 order by changed desc")
    fun allNonPushedRx(): Single<List<Picture>>

    @Query("SELECT count(*) FROM Picture WHERE trackId = :trackRestId and androidid = :androidID and deleted != 1 " + "order by changed desc")
    fun eventExists(trackRestId: Long, androidID: String): Int

    @Query("SELECT * FROM Picture WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Picture>

    @Query("SELECT * FROM Picture WHERE id = :id")
    fun loadById(id: Long): Maybe<Picture>

    //    @Query("SELECT * FROM Picture WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    //    User findByName(String first, String last);

    @Insert
    fun insertPicturesAll(vararg Pictures: Picture)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPicturesAll(Pictures: List<Picture>)

    @Update
    fun update(Picture: Picture)

    @Delete
    fun delete(vararg Pictures: Picture)

    @Delete
    fun delete(Pictures: List<Picture>)

    @Transaction
    fun updatePicture(eventDelete: Picture, eventNew: Picture) {
        delete(eventDelete)
        insertPicturesAll(eventNew)
    }

    @Transaction
    fun updatePictures(eventOrigin: List<Picture>, eventNew: List<Picture>?) {
        delete(eventOrigin)
        eventNew?.let { insertPicturesAll(it) }
    }

}
