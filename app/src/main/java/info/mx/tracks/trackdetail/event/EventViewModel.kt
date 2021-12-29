package info.mx.tracks.trackdetail.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import info.mx.tracks.room.entity.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

class EventViewModel() : ViewModel(), KoinComponent {

    private val repository: EventRepository by inject()

    private val coroutineContext: CoroutineContext = Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext + SupervisorJob())

    // Using LiveData and caching what allEventsByTrack returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    fun allEvents(trackId: Long): LiveData<List<Event>> = repository.allEventsByTrackId(trackId).asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(word: Event) = viewModelScope.launch {
        repository.insert(word)
    }

    fun getNewRemoteData(restId: Long) = viewModelScope.launch {
        scope.launch {
            repository.getNewRemoteData(restId)
        }
    }

    private fun eventExistsAsync(event: Event, eventText: String) =
        viewModelScope.async { //CoroutineScope(Dispatchers.IO).async or scope.async
            return@async repository.eventExists(event.trackRestId, event.androidid)
        }

    private fun eventExists(event: Event, eventText: String): Int = runBlocking {
        eventExistsAsync(event, eventText)
            .await()
    }

    fun addEvent(event: Event, eventText: String): Int {
        val count = eventExists(event, eventText)
        if (count == 0)
            scope.launch {
                repository.insertEventsAll(event)
                repository.pushEventsAllNonPushed()
            }
        return count
    }
}
