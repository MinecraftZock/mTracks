package info.mx.tracks.trackdetail.detail

import androidx.annotation.WorkerThread
import info.mx.tracks.rest.AppApiClient
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Track
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrackDetailRepository : KoinComponent {

    private val mxDatabase: MxDatabase by inject()
    private val appApiClient: AppApiClient by inject()

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
//    fun allCommentsByTrack(trackId: Long): Flow<List<Track>> = mxDatabase.trackDao().loadById(trackId)

    fun getById(trackId: Long): Flow<Track> = mxDatabase.trackDao().loadById(trackId)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(track: Track) {
        mxDatabase.trackDao().insertTrack(track)
    }
//
//    @WorkerThread
//    suspend fun getNewRemoteData(restId: Long) {
//        val dbList = mxDatabase.commentDao().loadAllByTrackId(restId).first()
//
//        val networkList = appApiClient.appTracksService.callRatingsForTrack(restId, DataManagerBase.basic).execute().body()
//
//        var dbHash = ""
//        var netHash = ""
//        dbList.forEach { dbHash += it.toString() }
//        networkList?.forEach { netHash += it.toString() }
//        if (dbHash != netHash) {
//            var track = ""
//            networkList?.let { netList ->
//                track = netList[0].trackId.toString()
//            }
//
//            val toDelete = dbList.filter { !(networkList?.contains(it) ?: false) }
//            val toInsert = networkList?.filter { !dbList.contains(it) }
//
//            Timber.d("setDataUP ${toDelete.size}/${toInsert?.size} $track")
//            mxDatabase.commentDao().updateComments(toDelete, toInsert)
//        }
//    }
//
//    fun commentExists(restId: Long, androidID: String, comment: String) = mxDatabase.commentDao().commentExists(restId, androidID, comment)
//
//    fun insertCommentsAll(comment: Comment) = mxDatabase.commentDao().insertCommentsAll(comment)
//
//    fun pushCommentsAllNonPushed() {
//        mxDatabase.commentDao().commentAllNonPushed().forEach { itemNewCandidate ->
//            //dataManagerApp.pushRatingRx(item)
//            try {
//
//                val commentCall = appApiClient.appTracksService.postRating(itemNewCandidate, DataManagerBase.basic).execute()
//                commentCall.body()?.let { itemRest ->
//                    if (commentCall.isSuccessful)
//                        mxDatabase.commentDao().updateComment(commentDelete = itemNewCandidate, commentNew = itemRest)
//                }
//            } catch (e: Exception) {
//                Timber.e(e)
//            }
//        }
//    }

}
