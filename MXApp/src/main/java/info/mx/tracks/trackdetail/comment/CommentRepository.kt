package info.mx.tracks.trackdetail.comment

import androidx.annotation.WorkerThread
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Comment
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CommentRepository : KoinComponent {

    private val mxDatabase: MxDatabase by inject()

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun allCommentsByTrack(trackId: Long): Flow<List<Comment>> = mxDatabase.commentDao().getByTrackId(trackId)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(word: Comment) {
        mxDatabase.commentDao().insertAll(word)
    }

}
