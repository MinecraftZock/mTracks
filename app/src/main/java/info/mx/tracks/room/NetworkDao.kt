package info.mx.tracks.room

import androidx.room.*
import info.mx.tracks.room.entity.Network

@Dao
interface NetworkDao {

    @get:Query("SELECT * FROM Network")
    val all: List<Network>

//    @Query("SELECT * FROM Network WHERE trackId = :trackId and note != '' and deleted != 1 order by changed desc")
//    fun getByTrackId(trackId: Long): Flow<List<Comment>>
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
//    fun networkAllNonPushed(): List<Comment>
//
//    @Query("SELECT * FROM Comment WHERE id < 0 order by changed desc")
//    fun allNonPushedRx(): Single<List<Comment>>
//
//    @Query("SELECT count(*) FROM Comment WHERE trackId = :trackId and androidid = :androidID and note = :note and deleted != 1 " + "order by changed desc")
//    fun networkExists(trackId: Long, androidID: String, note: String): Int
//
//    @Query("SELECT * FROM Comment WHERE id IN (:ids)")
//    fun loadAllByIds(ids: IntArray): List<Comment>
//
//    @Query("SELECT * FROM Comment WHERE id = :id")
//    fun loadById(id: Long): Maybe<Comment>

    //    @Query("SELECT * FROM Comment WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    //    User findByName(String first, String last);

    @Insert
    fun insertNetworkAll(vararg networks: Network)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNetworkAll(networks: List<Network>)

    @Update
    fun update(network: Network)

    @Query("DELETE FROM Network")
    fun deleteAll()

    @Delete
    fun delete(vararg networks: Network)

    @Delete
    fun delete(networks: List<Network>)

    @Transaction
    fun updateComment(networkDelete: Network, networkNew: Network) {
        delete(networkDelete)
        insertNetworkAll(networkNew)
    }

    @Transaction
    fun updateComments(networkOrigin: List<Network>, networkNew: List<Network>?) {
        delete(networkOrigin)
        networkNew?.let { insertNetworkAll(it) }
    }

}
