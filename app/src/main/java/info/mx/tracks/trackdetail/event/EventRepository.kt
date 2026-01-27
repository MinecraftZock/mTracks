package info.mx.tracks.trackdetail.event

import androidx.annotation.WorkerThread
import info.mx.tracks.data.DataManagerBase
import info.mx.tracks.rest.AppApiClient
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class EventRepository : KoinComponent {

    private val mxDatabase: MxDatabase by inject()
    private val appApiClient: AppApiClient by inject()

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun allEventsByTrackId(trackId: Long): Flow<List<Event>> = mxDatabase.eventDao().getByTrackId(trackId)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(word: Event) {
        mxDatabase.eventDao().insertEventsAll(word)
    }

    @WorkerThread
    suspend fun getNewRemoteData(restId: Long) {
        val dbList = mxDatabase.eventDao().loadAllByTrackId(restId).first()

        val networkList = appApiClient.appTracksService.callEventsForTrack(restId, DataManagerBase.basic).execute().body()

        var dbHash = ""
        var netHash = ""
        dbList.forEach { dbHash += it.toString() }
        networkList?.forEach { netHash += it.toString() }
        if (dbHash != netHash) {
            var track = ""
            networkList?.let { netList ->
                track = netList[0].trackRestId.toString()
            }

            val toDelete = dbList.filter { !(networkList?.contains(it) ?: false) }
            val toInsert = networkList?.filter { !dbList.contains(it) }

            Timber.d("setDataUP ${toDelete.size}/${toInsert?.size} $track")
            mxDatabase.eventDao().updateEvents(toDelete, toInsert)
        }
    }

    fun eventExists(restId: Long, androidID: String) = mxDatabase.eventDao().eventExists(restId, androidID)

    fun insertEventsAll(event: Event) = mxDatabase.eventDao().insertEventsAll(event)

    fun pushEventsAllNonPushed() {
        mxDatabase.eventDao().eventAllNonPushed().forEach { itemNewCandidate ->
            //dataManagerApp.pushRatingRx(item)
            try {
                val eventCall = appApiClient.appTracksService.postEvent(itemNewCandidate, DataManagerBase.basic).execute()
                eventCall.body()?.let { itemRest ->
                    if (eventCall.isSuccessful)
                        mxDatabase.eventDao().updateEvent(eventDelete = itemNewCandidate, eventNew = itemRest)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}
