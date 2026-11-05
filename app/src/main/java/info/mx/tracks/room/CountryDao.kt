package info.mx.tracks.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import info.mx.tracks.room.entity.Country
import info.mx.tracks.room.entity.CountryCount
import kotlinx.coroutines.flow.Flow


@Dao
interface CountryDao {

    @Query("SELECT * FROM Country WHERE country = :country")
    fun getCountry(country: String): List<Country>

    @get:Query("SELECT * FROM Country")
    val all: List<Country>

    @get:Query("SELECT * FROM Country where show = 1")
    val allShown: List<Country>

    @Query("SELECT * FROM CountryCount ORDER BY country")
    fun getAllCountryCount(): Flow<List<CountryCount>>

    @Query("DELETE FROM Country WHERE id IN (SELECT id FROM CountryCount WHERE count = 0)")
    fun cleanupFromEmptyCounties(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM Country WHERE country = :countryCode LIMIT 1)")
    fun exists(countryCode: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM Country WHERE country = :countryCode and show = 1 LIMIT 1)")
    fun isShown(countryCode: String): Boolean

    @Query("SELECT * FROM Country WHERE country = :countryCode")
    fun byCountryCode(countryCode: String): List<Country>

    @Query("SELECT * FROM Country WHERE id = :id")
    fun byId(id: Long): Country?

    @Query("DELETE FROM Country WHERE country = :countryCode")
    fun deleteByCountryCode(countryCode: String): Int

    @Query("UPDATE Country SET show = :show WHERE show != :show")
    fun updateShowByCountryCode(show: Boolean): Int

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

    @Insert
    fun insertCountry(vararg country: Country)

    @Upsert
    fun upsertCountry(vararg country: Country)

    @Update
    fun update(country: Country)

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
