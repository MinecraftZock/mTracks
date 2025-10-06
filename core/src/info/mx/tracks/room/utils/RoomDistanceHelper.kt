package info.mx.tracks.room.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.view.View
import android.widget.TextView
import info.hannes.commonlib.DateHelper.getTimeStrFromMinutesDay
import info.hannes.commonlib.LocationHelper.getFormatDistance
import info.mx.tracks.common.SecHelper
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.room.DatabaseManager
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.text.DecimalFormat
import kotlin.math.roundToInt

object RoomDistanceHelper {
    private const val MAX_DISTANCE = 99999999
    private const val MAX_SECONDS = 24 * 3600 * 4

    @JvmStatic
    suspend fun calcDistance2TracksCrypt(
        lat: Double,
        lon: Double,
        updateGUI: Boolean,
        country: String?,
        context: Context
    ) {
        val df = DecimalFormat("0.00")
        val currentPos = Location("center")
        currentPos.latitude = lat
        currentPos.longitude = lon

        if (lat != 0.0) {
            val start = System.currentTimeMillis()
            val trackRepository = DatabaseManager.getTrackRepository(context)
            var zlr = 0

            try {
                val tracks = if (country != null) {
                    trackRepository.getTracksByCountry(country).first()
                } else {
                    trackRepository.getAllTracks().first()
                }

                tracks.forEach { track ->
                    zlr++
                    val dest = Location("dest")
                    dest.latitude = SecHelper.entcryptXtude(track.latitude)
                    dest.longitude = SecHelper.entcryptXtude(track.longitude)
                    val distance = currentPos.distanceTo(dest).roundToInt()
                    
                    // Update the track with the calculated distance
                    val updatedTrack = track.copy(distance2location = distance)
                    trackRepository.updateTrack(updatedTrack)
                }

                val seconds = (System.currentTimeMillis() - start) / 1000.0
                Timber.d(
                    "calcDistanceByCountry $zlr records set distance2current: LOC:${df.format(seconds)}s"
                )
            } catch (e: Exception) {
                Timber.e(e, "Error calculating distances")
            }
        }
    }

    @JvmStatic
    suspend fun updateDistanceTracksCurrent(
        lat: Double,
        lon: Double,
        context: Context
    ) {
        calcDistance2TracksCrypt(lat, lon, false, null, context)
    }

    @JvmStatic
    suspend fun updateDistanceTracksCurrentByCountry(
        lat: Double,
        lon: Double,
        country: String,
        context: Context
    ) {
        calcDistance2TracksCrypt(lat, lon, false, country, context)
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    fun setDistanceText2TextView(
        textView: TextView,
        distanceInMeter: Int,
        distanceInMinutes: Int
    ) {
        var text = ""
        textView.visibility = View.VISIBLE
        if (distanceInMeter < MAX_DISTANCE) {
            text = getFormatDistance(distanceInMeter.toDouble())
            if (distanceInMinutes < MAX_SECONDS / 60) {
                text += " / " + getTimeStrFromMinutesDay(distanceInMinutes)
            }
        }
        textView.text = text
    }

    @JvmStatic
    fun getFormattedDistance(distanceInMeter: Int): String {
        return if (distanceInMeter < MAX_DISTANCE) {
            getFormatDistance(distanceInMeter.toDouble())
        } else {
            ""
        }
    }

    @JvmStatic
    fun getFormattedTime(distanceInMinutes: Int): String {
        return if (distanceInMinutes < MAX_SECONDS / 60) {
            getTimeStrFromMinutesDay(distanceInMinutes)
        } else {
            ""
        }
    }
}