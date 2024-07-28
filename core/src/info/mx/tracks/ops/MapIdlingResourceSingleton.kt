package info.mx.tracks.ops

import androidx.test.espresso.idling.CountingIdlingResource


object MapIdlingResourceSingleton {

    private const val RESOURCE = "MAO"

    val idlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        idlingResource.increment()
    }

    fun decrement() {
        if (!idlingResource.isIdleNow) {
            idlingResource.decrement()
        }
    }
}
