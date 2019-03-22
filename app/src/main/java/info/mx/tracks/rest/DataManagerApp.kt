package info.mx.tracks.rest

import info.mx.comlib.retrofit.service.data.Data
import info.mx.core.data.DataManagerBase
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Comment
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * It keeps a reference to every helper class and uses them to satisfy the requests coming from the UI.
 * Its methods make extensive use of Rx operators to combine, transform or filter the output coming from the helpers in order to generate the
 * desired output ready for the UI.
 * It returns observables that emit data models.
 */
class DataManagerApp(private var appApiClient: AppApiClient, private var mxDatabase: MxDatabase) : DataManagerBase() {

    fun updateRatingsForTrack(trackId: Long): Observable<Data<List<Comment>>> {
        val dbList = mxDatabase.commentDao().loadAllByTrackId(trackId)
            .map { dbData -> Data.db(dbData) }

        val networkList = appApiClient.appTracksService.getRatingsForTrack(trackId, basic)
            .map { data -> Data.network(data) }

        return Observable.zip(dbList.toObservable(), networkList.toObservable(), { t1, t2 -> Pair(t1, t2) })
            .flatMap { pair ->
                var dbHash = ""
                var netHash = ""
                pair.first.data.forEach { dbHash += it.toString() }
                pair.second.data.forEach { netHash += it.toString() }
                if (dbHash == netHash) {
                    var track = ""
                    if (pair.second.data.isNotEmpty())
                        track = pair.second.data[0].trackId.toString()
                    Timber.d("setDataEQ ${pair.first.data.size}/${pair.second.data.size} $track")
                    Observable.just(pair.first)
                } else {
                    var track = ""
                    if (pair.second.data.isNotEmpty())
                        track = pair.second.data[0].trackId.toString()

                    val toDelete = pair.first.data.filter { !pair.second.data.contains(it) }
                    val toInsert = pair.second.data.filter { !pair.first.data.contains(it) }

                    Timber.d("setDataUP ${toDelete.size}/${toInsert.size} $track")
                    mxDatabase.commentDao().updateComments(toDelete, toInsert)
                    Observable.just(pair.second)
                }
            }
    }

    /**
     * after POST the id is changed
     */
    fun pushRating(comment: Comment): Single<Comment> {
        return appApiClient.appTracksService.postRating(comment, basic)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMap { newComment ->
                mxDatabase.commentDao().updateComment(comment, newComment)
                Single.just(newComment)
            }
    }

}
