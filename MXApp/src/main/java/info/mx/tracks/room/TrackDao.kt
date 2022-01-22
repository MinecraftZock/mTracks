package info.mx.tracks.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.entity.TrackStage

@Dao
interface TrackDao {

//    @Query("SELECT * FROM Track WHERE trackId = :trackId and note != '' and deleted != 1 order by changed desc")
//    fun getByTrackId(trackId: Long): Flow<List<Track>>

    @get:Query("SELECT * FROM Track")
    val all: List<Track>

    @Query("SELECT count(*) FROM Track")
    fun countAll(): Int

    @Query("SELECT * FROM Track WHERE country = :country")
    fun emptyCountry(country: String): List<Track>

    @Query("SELECT * FROM Trackstage WHERE restId > 0 and changed = 1")
    fun alreadyKnownAndUpdated(): List<TrackStage>

    @Query("SELECT max(changed) FROM Track")
    fun latest(): Long

    @Query("DELETE FROM Track where approved = -1 and changed != :maxChanged")
    fun deleteNotApproved(maxChanged: Long)

//    @Query("SELECT avg(rating) FROM Track WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
//    fun avgByTrackIdMT(trackId: Long): Float
//
//    @Query("SELECT * FROM Track WHERE trackId = :trackId order by changed desc")
//    fun flowableAllByTrackId(trackId: Long): Flowable<List<Track>>
//
//    @Query("SELECT * FROM Track WHERE trackId = :trackId order by changed desc")
//    fun loadAllByTrackId(trackId: Long): Flow<List<Track>>
//
//    @Query("SELECT * FROM Track WHERE id < 0 order by changed desc")
//    fun commentAllNonPushed(): List<Track>
//
//    @Query("SELECT * FROM Track WHERE id < 0 order by changed desc")
//    fun allNonPushedRx(): Single<List<Track>>
//
//    @Query("SELECT count(*) FROM Track WHERE trackId = :trackId and androidid = :androidID and note = :note and deleted != 1 " + "order by changed desc")
//    fun commentExists(trackId: Long, androidID: String, note: String): Int
//
//    @Query("SELECT * FROM Track WHERE id IN (:ids)")
//    fun loadAllByIds(ids: IntArray): List<Track>
//
//    @Query("SELECT * FROM Track WHERE id = :id")
//    fun loadById(id: Long): Maybe<Track>
//
//    //    @Query("SELECT * FROM Track WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
//    //    User findByName(String first, String last);
//
//    @Insert
//    fun insertTracksAll(vararg comments: Track)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertTracksAll(comments: List<Track>)

    @Update
    fun update(track: Track)

//    @Delete
//    fun delete(vararg comments: Track)
//
//    @Delete
//    fun delete(comments: List<Track>)
//
//    @Transaction
//    fun updateTrack(commentDelete: Track, commentNew: Track) {
//        delete(commentDelete)
//        insertTracksAll(commentNew)
//    }
//
//    @Transaction
//    fun updateTracks(commentOrigin: List<Track>, commentNew: List<Track>?) {
//        delete(commentOrigin)
//        commentNew?.let { insertTracksAll(it) }
//    }

}
