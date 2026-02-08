package info.mx.core.sqlite

import android.database.Cursor
import android.location.Location
import info.mx.commonlib.LocationHelper.getFormatDistance
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.core_generated.sqlite.TracksgesRecord
import info.mx.tracks.common.SecHelper
import kotlin.math.roundToInt

class TracksDistanceRecord : TracksgesRecord(), Comparable<TracksDistanceRecord> {
    fun storeDistance2DB(currLoc: Location?): Int {
        var res = -1
        if (currLoc != null && currLoc.latitude != 0.0) {
            val locDB = Location("trackDB")
            locDB.latitude = SecHelper.entcryptXtude(this.latitude)
            locDB.longitude = SecHelper.entcryptXtude(this.longitude)

            val trackThis = Location("trackThis")
            trackThis.latitude = SecHelper.entcryptXtude(latitude)
            trackThis.longitude = SecHelper.entcryptXtude(longitude)

            val distDB = currLoc.distanceTo(trackThis).roundToInt()
            if (getFormatDistance(true, distDB) != getFormatDistance(
                    true,
                    distance2location.toInt()
                )
            ) {
                val trackDB = TracksRecord.get(this.id)
                if (trackDB != null) {
                    trackDB.setDistance2location(distDB.toLong())
                    res = distDB
                    trackDB.save(false)
                }
            }
        }
        return res
    }

    override fun compareTo(other: TracksDistanceRecord): Int {
        return if (this.distance2location == other.distance2location) {
            0
        } else {
            this.distance2location.compareTo(other.distance2location) // ascending
        }
    }

    companion object {
        private var distance = 0

        fun fromCursor(c: Cursor, currentPos: Location?): TracksDistanceRecord {
            val item = TracksDistanceRecord()

            item.setPropertiesFromCursor(c)

            item.makeDirty(false)

            if (currentPos != null) {
                val trackPos = Location("track")
                trackPos.latitude = SecHelper.entcryptXtude(item.latitude)
                trackPos.longitude = SecHelper.entcryptXtude(item.longitude)
                distance = trackPos.distanceTo(currentPos).roundToInt()
                item.setDistance2location(distance.toLong())
            }

            return item
        }
    }
}
