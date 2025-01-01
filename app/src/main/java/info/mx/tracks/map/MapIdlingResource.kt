package info.mx.tracks.map

import androidx.test.espresso.idling.CountingIdlingResource
import timber.log.Timber


object MapIdlingResource {

    private const val RESOURCE = "map-load"
    private var count = 0

    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment(countComplete: Int) {
        countingIdlingResource.increment()
        this.count++
        Timber.d("count=${this.count} countComplete=$countComplete")
        countingIdlingResource.dumpStateToLogs()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            count--
            countingIdlingResource.decrement()
            Timber.d("count=$count ${!countingIdlingResource.isIdleNow}")
        } else
            Timber.d("count=$count ${!countingIdlingResource.isIdleNow}")
        countingIdlingResource.dumpStateToLogs()
    }
}
