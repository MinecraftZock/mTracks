package info.mx.core.sqlite

import kotlin.Comparable
import kotlin.Double
import kotlin.Int

open class TracksDistanceSmall(val id: Long, @JvmField val lat: Double, @JvmField val lon: Double) :
    Comparable<TracksDistanceSmall> {
    var distance: Int = 0

    override fun compareTo(other: TracksDistanceSmall): Int {
        return if (this.distance == other.distance) {
            0
        } else {
            this.distance.toLong().compareTo(other.distance.toLong()) // ascending
        }
    }

}
