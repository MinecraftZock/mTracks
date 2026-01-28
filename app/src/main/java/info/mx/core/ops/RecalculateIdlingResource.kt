package info.mx.core.ops

import androidx.test.espresso.idling.CountingIdlingResource


object RecalculateIdlingResource {

    private const val RESOURCE = "recalculate"

    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}
