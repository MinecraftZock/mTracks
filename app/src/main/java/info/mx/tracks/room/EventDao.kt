package info.mx.tracks.room

import androidx.room.*
import info.mx.tracks.room.entity.Event
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM Event WHERE trackRestId = :trackRestId and deleted != 1 order by changed desc")
    fun getByTrackId(trackRestId: Long): Flow<List<Event>>

    @get:Query("SELECT * FROM Event")
    val all: List<Event>

    @Query("SELECT * FROM Event WHERE trackRestId = :trackRestId order by changed desc")
    fun flowableAllByTrackId(trackRestId: Long): Flowable<List<Event>>

    @Query("SELECT * FROM Event WHERE trackRestId = :trackRestId order by changed desc")
    fun loadAllByTrackId(trackRestId: Long): Flow<List<Event>>

    @Query("SELECT * FROM Event WHERE id < 0 order by changed desc")
    fun eventAllNonPushed(): List<Event>

    @Query("SELECT * FROM Event WHERE id < 0 order by changed desc")
    fun allNonPushedRx(): Single<List<Event>>

    @Query("SELECT count(*) FROM Event WHERE trackRestId = :trackRestId and androidid = :androidID and deleted != 1 " + "order by changed desc")
    fun eventExists(trackRestId: Long, androidID: String): Int

    @Query("SELECT * FROM Event WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Event>

    @Query("SELECT * FROM Event WHERE id = :id")
    fun loadById(id: Long): Maybe<Event>

    //    @Query("SELECT * FROM Event WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    //    User findByName(String first, String last);

    @Insert
    fun insertEventsAll(vararg Events: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventsAll(Events: List<Event>)

    @Update
    fun update(Event: Event)

    @Delete
    fun delete(vararg Events: Event)

    @Delete
    fun delete(Events: List<Event>)

    @Transaction
    fun updateEvent(eventDelete: Event, eventNew: Event) {
        delete(eventDelete)
        insertEventsAll(eventNew)
    }

    @Transaction
    fun updateEvents(eventOrigin: List<Event>, eventNew: List<Event>?) {
        delete(eventOrigin)
        eventNew?.let { insertEventsAll(it) }
    }

}
