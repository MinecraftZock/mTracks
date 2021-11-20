package info.mx.tracks.trackdetail.comment

import androidx.lifecycle.*
import info.mx.tracks.room.entity.Comment
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CommentViewModel() : ViewModel(), KoinComponent {

    private val repository: CommentRepository by inject()

    // Using LiveData and caching what allCommentsByTrack returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    fun allCommentsByTrackId(trackId: Long): LiveData<List<Comment>> = repository.allCommentsByTrackId(trackId).asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(word: Comment) = viewModelScope.launch {
        repository.insert(word)
    }
}
