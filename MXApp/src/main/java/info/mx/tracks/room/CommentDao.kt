package info.mx.tracks.room

import androidx.room.*
import info.mx.tracks.room.entity.Comment
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface CommentDao {

    @get:Query("SELECT * FROM Comment")
    val all: List<Comment>

    @Query("SELECT * FROM Comment WHERE trackId = :trackId and note != '' and deleted != 1 order by changed desc")
    fun loadByTrackId(trackId: Long): Flowable<List<Comment>>

    @Query("SELECT avg(rating) FROM Comment WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
    fun avgByTrackId(trackId: Long): Maybe<Float>

    @Query("SELECT avg(rating) FROM Comment WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
    fun avgByTrackIdMT(trackId: Long): Float

    @Query("SELECT * FROM Comment WHERE trackId = :trackId order by changed desc")
    fun loadAllByTrackId(trackId: Long): Flowable<List<Comment>>

    @Query("SELECT * FROM Comment WHERE id < 0 order by changed desc")
    fun allNonPushed(): Single<List<Comment>>

    @Query("SELECT count(*) FROM Comment WHERE trackId = :trackId and androidid = :androidID and note = :note and deleted != 1 " + "order by changed desc")
    fun commentExistsRx(trackId: Long, androidID: String, note: String): Single<Int>

    @Query("SELECT * FROM Comment WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Comment>

    @Query("SELECT * FROM Comment WHERE id = :id")
    fun loadById(id: Long): Maybe<Comment>

    //    @Query("SELECT * FROM Comment WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    //    User findByName(String first, String last);

    @Insert
    fun insertAll(vararg comments: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(comments: List<Comment>)

    @Update
    fun update(comment: Comment)

    @Delete
    fun delete(vararg comments: Comment)

    @Delete
    fun delete(comments: List<Comment>)

    @Transaction
    fun updateComment(commentOrigin: Comment, commentNew: Comment) {
        delete(commentOrigin)
        insertAll(commentNew)
    }

    @Transaction
    fun updateComments(commentOrigin: List<Comment>, commentNew: List<Comment>) {
        delete(commentOrigin)
        insertAll(commentNew)
    }

}
