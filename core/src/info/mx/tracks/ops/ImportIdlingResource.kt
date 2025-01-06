package info.mx.tracks.ops

import androidx.test.espresso.idling.CountingIdlingResource


object ImportIdlingResource {

    private const val RESOURCE = "IMPORT"

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
