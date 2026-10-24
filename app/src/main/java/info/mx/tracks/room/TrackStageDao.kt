package info.mx.tracks.room

import androidx.room.*
import info.mx.tracks.room.entity.TrackStage

@Dao
interface TrackStageDao {

//    @Query("SELECT * FROM TrackStage WHERE trackId = :trackId and note != '' and deleted != 1 order by changed desc")
//    fun getByTrackStageId(trackId: Long): Flow<List<TrackStage>>

    @get:Query("SELECT * FROM TrackStage")
    val all: List<TrackStage>

    @Query("DELETE FROM TrackStage where approved = -1")
    fun deleteNotApproved()

//    @Query("SELECT * FROM TrackStage WHERE country = :country")
//    fun emptyCountry(country: String): List<TrackStage>

//    @Query("SELECT avg(rating) FROM TrackStage WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
//    fun avgByTrackStageIdMT(trackId: Long): Float
//
//    @Query("SELECT * FROM TrackStage WHERE trackId = :trackId order by changed desc")
//    fun flowableAllByTrackStageId(trackId: Long): Flowable<List<TrackStage>>
//
//    @Query("SELECT * FROM TrackStage WHERE trackId = :trackId order by changed desc")
//    fun loadAllByTrackStageId(trackId: Long): Flow<List<TrackStage>>
//
//    @Query("SELECT * FROM TrackStage WHERE id < 0 order by changed desc")
//    fun commentAllNonPushed(): List<TrackStage>
//
//    @Query("SELECT * FROM TrackStage WHERE id < 0 order by changed desc")
//    fun allNonPushedRx(): Single<List<TrackStage>>
//
//    @Query("SELECT count(*) FROM TrackStage WHERE trackId = :trackId and androidid = :androidID and note = :note and deleted != 1 " + "order by changed desc")
//    fun commentExists(trackId: Long, androidID: String, note: String): Int
//
//    @Query("SELECT * FROM TrackStage WHERE id IN (:ids)")
//    fun loadAllByIds(ids: IntArray): List<TrackStage>
//
//    @Query("SELECT * FROM TrackStage WHERE id = :id")
//    fun loadById(id: Long): Maybe<TrackStage>
//
//    //    @Query("SELECT * FROM TrackStage WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
//    //    User findByName(String first, String last);

    @Insert
    fun insertTrackStagesAll(vararg comments: TrackStage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrackStagesAll(comments: List<TrackStage>)

    @Update
    fun update(trackStage: TrackStage)

//    @Delete
//    fun delete(vararg comments: TrackStage)
//
//    @Delete
//    fun delete(comments: List<TrackStage>)
//
//    @Transaction
//    fun updateTrackStage(commentDelete: TrackStage, commentNew: TrackStage) {
//        delete(commentDelete)
//        insertTrackStagesAll(commentNew)
//    }
//
//    @Transaction
//    fun updateTrackStages(commentOrigin: List<TrackStage>, commentNew: List<TrackStage>?) {
//        delete(commentOrigin)
//        commentNew?.let { insertTrackStagesAll(it) }
//    }

}
