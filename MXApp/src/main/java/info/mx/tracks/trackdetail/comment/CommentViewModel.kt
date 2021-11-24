package info.mx.tracks.trackdetail.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import info.mx.tracks.room.entity.Comment
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.coroutines.CoroutineContext

class CommentViewModel() : ViewModel(), KoinComponent {

    private val repository: CommentRepository by inject()

    private val coroutineContext: CoroutineContext = Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext + SupervisorJob())

    // Using LiveData and caching what allCommentsByTrack returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    fun allComments(trackId: Long): LiveData<List<Comment>> = repository.allCommentsByTrack(trackId).asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(word: Comment) = viewModelScope.launch {
        repository.insert(word)
    }

    fun getNewRemoteData(restId: Long) = viewModelScope.launch {
        scope.launch {
            repository.getNewRemoteData(restId)
        }
    }

    private fun commentExistsAsync(comment: Comment, commentText: String) =
        viewModelScope.async { //CoroutineScope(Dispatchers.IO).async or scope.async
            return@async repository.commentExists(comment.trackId, comment.androidid, commentText)
        }

    private fun commentExists(comment: Comment, commentText: String): Int = runBlocking {
        commentExistsAsync(comment, commentText)
            .await()
    }

    fun addComment(comment: Comment, commentText: String): Int {
        val count = commentExists(comment, commentText)
        if (count == 0)
            scope.launch {
                repository.insertCommentsAll(comment)
                repository.pushCommentsAllNonPushed()
            }
        return count
    }
}
