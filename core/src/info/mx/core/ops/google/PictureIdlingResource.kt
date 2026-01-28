package info.mx.core.ops.google

import androidx.test.espresso.idling.CountingIdlingResource
import timber.log.Timber


object PictureIdlingResource {

    private const val RESOURCE = "PICTURE"
    private var count = 0

    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment(i: Int, countComplete: Int) {
        countingIdlingResource.increment()
        this.count++
        Timber.d("count=${this.count} i=$i count=$countComplete")
        countingIdlingResource.dumpStateToLogs()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            count--
            countingIdlingResource.decrement()
            Timber.d("count=${this.count} ${!countingIdlingResource.isIdleNow}")
        }
        countingIdlingResource.dumpStateToLogs()
    }
}
