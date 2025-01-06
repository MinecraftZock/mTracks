package info.mx.tracks.map

import androidx.test.espresso.idling.CountingIdlingResource
import timber.log.Timber


object MapIdlingResource {

    private const val RESOURCE = "MAP-LOAD"
    private var count = 0

    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        val stack = Thread.currentThread().stackTrace
        countingIdlingResource.increment()
        count++
        Timber.d("count=$count ${stack[3]}")
        countingIdlingResource.dumpStateToLogs()
    }

    fun decrement() {
        val stack = Thread.currentThread().stackTrace
        Timber.d("count=$count isIdleNow=${countingIdlingResource.isIdleNow} ${stack[3]}")
        if (!countingIdlingResource.isIdleNow) {
            count--
            countingIdlingResource.decrement()
        }
        countingIdlingResource.dumpStateToLogs()
    }
}
