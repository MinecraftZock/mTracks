package info.mx.tracks.ops.google

import androidx.test.espresso.idling.CountingIdlingResource
import timber.log.Timber


object RecalculateIdlingResource {

    private const val RESOURCE = "RECALC"
    private var count = 0

    val recalculateIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment(i: Int, countComplete: Int) {
        recalculateIdlingResource.increment()
        this.count++
        Timber.d("() count=${this.count} i=$i count=$countComplete")
        recalculateIdlingResource.dumpStateToLogs()
    }

    fun decrement() {
        if (!recalculateIdlingResource.isIdleNow) {
            count--
            recalculateIdlingResource.decrement()
            Timber.d("() $count ${!recalculateIdlingResource.isIdleNow}")
        }
        recalculateIdlingResource.dumpStateToLogs()
    }
}
