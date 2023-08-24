package info.mx.tracks.ops

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.tracks.MxCoreApplication.Companion.isAdmin
import info.mx.tracks.common.LoggingHelper.setMessage
import info.mx.tracks.common.SecHelper
import info.mx.tracks.ops.ImportHelper.getDistance
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

internal class OpRecalcDistanceOperation : AbstractOpRecalcDistanceOperation() {
    private val genauigkeit = if (isAdmin) 150 else 350
    private val sdf = SimpleDateFormat("hh:mm:ss")
    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        return try {
            val locCalcTo = Location("xyz")
            locCalcTo.latitude = args.lat
            locCalcTo.longitude = args.lon
            locCalcTo.provider = args.provider
            val starttime = System.currentTimeMillis()
            var wann = sdf.format(Date(System.currentTimeMillis()))
            val resolver = context.applicationContext.contentResolver
            val cursor = resolver.query(Tracks.CONTENT_URI, PROJECTION, null, null, Tracks._ID)
            var first = true
            var last = false
            var distance: Long
            var meter: Long = 0
            while (cursor!!.moveToNext()) {
                val lat = SecHelper.entcryptXtude(cursor.getDouble(1))
                val lon = SecHelper.entcryptXtude(cursor.getDouble(2))
                if (lat != 0.0 && lon != 0.0) {
                    distance = getDistance(locCalcTo, lat, lon)
                    if (first) {
                        meter = abs(distance - cursor.getLong(3))
                        if ((distance / genauigkeit).toFloat().roundToInt() == (cursor.getLong(
                                3
                            ) / genauigkeit).toFloat().roundToInt()
                        ) {
                            if (isAdmin) {
                                setMessage(wann + " passt id:" + cursor.getLong(0) + " dist:" + meter + "m")
                            }
                            break // nichts zu machen
                        } else {
                            if (isAdmin) {
                                setMessage(wann + " ungleich id:" + cursor.getLong(0) + " dist:" + meter + "m")
                            }
                        }
                        first = false
                    }
                    last = cursor.position + 1 == cursor.count
                    Tracks.newBuilder()
                        .setDistance2location(distance)
                        .update(cursor.getLong(0), last)
                }
            }
            cursor.close()
            val estimatedTime = System.currentTimeMillis() - starttime
            if (!first && isAdmin) {
                wann = sdf.format(Date(System.currentTimeMillis()))
                setMessage(wann + " " + last + estimatedTime + " msec dist:" + meter + "m")
            }
            val bundle = Bundle()
            OperationResult.ok(bundle)
        } catch (e: Exception) {
            Timber.e(e)
            if (isAdmin) {
                Toast.makeText(context.applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            OperationResult.error(e)
        }
    }

    companion object {
        private val PROJECTION =
            arrayOf(Tracks._ID, Tracks.LATITUDE, Tracks.LONGITUDE, Tracks.DISTANCE2LOCATION)
    }
}