package info.mx.tracks.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TrackDao {

//    @get:Query("SELECT * FROM Comment")
//    val all: List<Comment>
//
//    @Query("SELECT avg(rating) FROM Comment WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
//    fun avgByTrackId(trackId: Long): Maybe<Float>
//
//    @Query("SELECT avg(rating) FROM Comment WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
//    fun avgByTrackIdMT(trackId: Long): Float
//
//    @Query("SELECT * FROM Comment WHERE trackId = :trackId order by changed desc")
//    fun flowableAllByTrackId(trackId: Long): Flowable<List<Comment>>
//
//    @Query("SELECT * FROM Comment WHERE trackId = :trackId order by changed desc")
//    fun loadAllByTrackId(trackId: Long): Flow<List<Comment>>
//
//    @Query("SELECT * FROM Comment WHERE id < 0 order by changed desc")
//    fun commentAllNonPushed(): List<Comment>
//
//    @Query("SELECT * FROM Comment WHERE id < 0 order by changed desc")
//    fun allNonPushedRx(): Single<List<Comment>>

    @Query("SELECT count(*) FROM Track")
    fun countAll(): Int

//    @Query("SELECT * FROM Comment WHERE id IN (:ids)")
//    fun loadAllByIds(ids: IntArray): List<Comment>
//
//    @Query("SELECT * FROM Comment WHERE id = :id")
//    fun loadById(id: Long): Maybe<Comment>
//
//    //    @Query("SELECT * FROM Comment WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
//    //    User findByName(String first, String last);
//
//    @Insert
//    fun insertCommentsAll(vararg comments: Comment)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertCommentsAll(comments: List<Comment>)
//
//    @Update
//    fun update(comment: Comment)
//
//    @Delete
//    fun delete(vararg comments: Comment)
//
//    @Delete
//    fun delete(comments: List<Comment>)
//
//    @Transaction
//    fun updateComment(commentDelete: Comment, commentNew: Comment) {
//        delete(commentDelete)
//        insertCommentsAll(commentNew)
//    }
//
//    @Transaction
//    fun updateComments(commentOrigin: List<Comment>, commentNew: List<Comment>?) {
//        delete(commentOrigin)
//        commentNew?.let { insertCommentsAll(it) }
//    }

}
