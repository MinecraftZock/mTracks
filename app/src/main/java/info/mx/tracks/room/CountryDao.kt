package info.mx.tracks.room

import androidx.room.Dao
import androidx.room.Query
import info.mx.tracks.room.entity.Country

@Dao
interface CountryDao {

    @Query("SELECT * FROM Country WHERE country = :country")
    fun getCountry(country: String): List<Country>

    @get:Query("SELECT * FROM Country")
    val all: List<Country>

//    @Query("SELECT avg(rating) FROM Country WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
//    fun avgByTrackId(trackId: Long): Maybe<Float>
//
//    @Query("SELECT avg(rating) FROM Country WHERE trackId = :trackId and note != '' and deleted != 1 and androidid != \"debug\" order by changed desc")
//    fun avgByTrackIdMT(trackId: Long): Float
//
//    @Query("SELECT * FROM Country WHERE trackId = :trackId order by changed desc")
//    fun flowableAllByTrackId(trackId: Long): Flowable<List<Country>>
//
//    @Query("SELECT * FROM Country WHERE trackId = :trackId order by changed desc")
//    fun loadAllByTrackId(trackId: Long): Flow<List<Country>>
//
//    @Query("SELECT * FROM Country WHERE id < 0 order by changed desc")
//    fun commentAllNonPushed(): List<Country>
//
//    @Query("SELECT * FROM Country WHERE id < 0 order by changed desc")
//    fun allNonPushedRx(): Single<List<Country>>
//
//    @Query("SELECT count(*) FROM Country WHERE trackId = :trackId and androidid = :androidID and note = :note and deleted != 1 " + "order by changed desc")
//    fun commentExists(trackId: Long, androidID: String, note: String): Int
//
//    @Query("SELECT * FROM Country WHERE id IN (:ids)")
//    fun loadAllByIds(ids: IntArray): List<Country>
//
//    @Query("SELECT * FROM Country WHERE id = :id")
//    fun loadById(id: Long): Maybe<Country>
//
//    //    @Query("SELECT * FROM Country WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
//    //    User findByName(String first, String last);
//
//    @Insert
//    fun insertCountrysAll(vararg comments: Country)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertCountrysAll(comments: List<Country>)
//
//    @Update
//    fun update(comment: Country)
//
//    @Delete
//    fun delete(vararg comments: Country)
//
//    @Delete
//    fun delete(comments: List<Country>)
//
//    @Transaction
//    fun updateCountry(commentDelete: Country, commentNew: Country) {
//        delete(commentDelete)
//        insertCountrysAll(commentNew)
//    }
//
//    @Transaction
//    fun updateCountrys(commentOrigin: List<Country>, commentNew: List<Country>?) {
//        delete(commentOrigin)
//        commentNew?.let { insertCountrysAll(it) }
//    }

}
