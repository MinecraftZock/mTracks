package info.mx.tracks.map

import androidx.test.espresso.idling.CountingIdlingResource
import timber.log.Timber


object MapIdlingResource {

    private const val RESOURCE = "MAP-LOAD"
    private var count = 0

    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
        count++
        Timber.d("count=$count")
        countingIdlingResource.dumpStateToLogs()
    }

    fun decrement(tag: String) {
        Timber.d("count=$count isIdleNow=${countingIdlingResource.isIdleNow} tag=$tag")
        if (!countingIdlingResource.isIdleNow) {
            count--
            countingIdlingResource.decrement()
        }
        countingIdlingResource.dumpStateToLogs()
    }
}
