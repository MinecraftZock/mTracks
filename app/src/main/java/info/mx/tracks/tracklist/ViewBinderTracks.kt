package info.mx.tracks.tracklist

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.location.Location
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import info.hannes.commonlib.DateHelper
import info.mx.commonlib.LocationHelper.getFormatDistance
import info.mx.tracks.BuildConfig
import info.mx.tracks.R
import info.mx.tracks.common.BitmapHelper
import info.mx.tracks.common.SecHelper
import info.mx.tracks.common.setDayLayout
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.MxInfoDBContract.TracksGesSum
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracksges
import info.mx.core_generated.sqlite.TracksGesSumRecord
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.core_generated.sqlite.TracksgesRecord
import info.mx.tracks.tools.PermissionHelper
import info.mx.tracks.util.getDrawableIdentifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.Locale
import kotlin.math.roundToInt

class ViewBinderTracks(private val context: Context, myLoc: Location?, withSum: Boolean) : SimpleCursorAdapter.ViewBinder, KoinComponent {

    private val shortWeekdays: Array<String> = DateHelper.shortWeekdays
    private val withSum: Boolean
    private val showKm: Boolean
    private var myLoc: Location?

    val permissionHelper: PermissionHelper by inject()

    init {
        val prefs = MxPreferences.getInstance()
        showKm = prefs.unitsKm
        this.myLoc = myLoc
        this.withSum = withSum
    }

    fun setMyLocation(myLoc: Location?) {
        this.myLoc = myLoc
    }

    @SuppressLint("DiscouragedApi")
    override fun setViewValue(view: View, tracksGesSumCursor: Cursor, columnIndex: Int): Boolean {
        var res = false
        try {
            if (view.id == R.id.tr_list_event) {
                val value = tracksGesSumCursor.getString(columnIndex)
                val ly = view.parent as LinearLayout
                ly.visibility = if (value == null || value == "") View.GONE else View.VISIBLE
            } else if (view.id == R.id.tr_track_access) {
                var iconRes = R.drawable.flag_blau_hell
                val value = tracksGesSumCursor.getString(columnIndex)
                if (value != null) {
                    iconRes = when (value) {
                        "R" -> R.drawable.flag_race_hell
                        "M" -> R.drawable.flag_member_hell
                        "D" -> R.drawable.flag_dealer_large2x
                        else -> R.drawable.flag_blau_hell
                    }
                }
                val viewTrackInf = getViewTrackInfoFromCursor(tracksGesSumCursor, withSum)
                (view as ImageView).setImageBitmap(
                    BitmapHelper.getBitmap(
                        context,
                        iconRes,
                        viewTrackInf.trackStatus
                    )
                )
                res = true
            } else if (view.id == R.id.tr_camera) {
                val value = tracksGesSumCursor.getInt(columnIndex)
                view.visibility = if (value == 0) ImageView.GONE else ImageView.VISIBLE
                res = true
            } else if (view.id == R.id.tr_calendar) {
                val value = tracksGesSumCursor.getInt(columnIndex)
                view.visibility = if (value == 0) ImageView.GONE else ImageView.VISIBLE
                res = true
            } else if (view.id == R.id.tr_country) {
                if (tracksGesSumCursor.getString(columnIndex) != null) {
                    val value = tracksGesSumCursor.getString(columnIndex).lowercase(Locale.getDefault()) + "2x"
                    val id = context.resources.getDrawableIdentifier(value, context.packageName)
                    (view as ImageView).setImageResource(id)
                }
                res = true
            } else if (view.id == R.id.tr_ratingBar) {
                val value = tracksGesSumCursor.getFloat(columnIndex)
                val ratingBar = view as RatingBar
                ratingBar.rating = value
                res = true
            } else if (view.id == R.id.tr_name) {
                var valueN = tracksGesSumCursor.getString(columnIndex)
                if (valueN == null ||
                    valueN.trim { it <= ' ' } == "" &&
                    BuildConfig.FLAVOR.equals("admin", ignoreCase = true)
                ) {
                    val viewTrackInf = getViewTrackInfoFromCursor(tracksGesSumCursor, withSum)
                    val track = TracksRecord.get(viewTrackInf.id)
                    if (track != null) {
                        valueN = track.restId.toString() + " " + track.approved
                    }
                }
                (view as TextView).text = valueN
                res = true
            } else if (view.id == R.id.tr_mo ||
                view.id == R.id.tr_tu ||
                view.id == R.id.tr_we ||
                view.id == R.id.tr_th ||
                view.id == R.id.tr_fr ||
                view.id == R.id.tr_sa ||
                view.id == R.id.tr_so
            ) {
                val value = tracksGesSumCursor.getInt(columnIndex)
                view.setDayLayout(value == 1)
                when (view.id) {
                    R.id.tr_mo -> (view as TextView).text = shortWeekdays[2]
                    R.id.tr_tu -> (view as TextView).text = shortWeekdays[3]
                    R.id.tr_we -> (view as TextView).text = shortWeekdays[4]
                    R.id.tr_th -> (view as TextView).text = shortWeekdays[5]
                    R.id.tr_fr -> (view as TextView).text = shortWeekdays[6]
                    R.id.tr_sa -> (view as TextView).text = shortWeekdays[7]
                    R.id.tr_so -> (view as TextView).text = shortWeekdays[1]
                }
                res = true
            } else if (columnIndex == tracksGesSumCursor.getColumnIndexOrThrow(TracksGesSum.DISTANCE2LOCATION)) {
                if (permissionHelper.hasLocationPermission()) {
                    (view.parent as ViewGroup).visibility = View.VISIBLE
                    val distanceOld = tracksGesSumCursor.getInt(columnIndex)
                    var distNew = distanceOld
                    if (myLoc != null) {
                        val viewTrackInf = getViewTrackInfoFromCursor(tracksGesSumCursor, withSum)
                        val trackLoc = Location("track")
                        trackLoc.latitude = SecHelper.entcryptXtude(viewTrackInf.latitude)
                        trackLoc.longitude = SecHelper.entcryptXtude(viewTrackInf.longitude)
                        distNew = myLoc!!.distanceTo(trackLoc).roundToInt()
                        if (getFormatDistance(showKm, distNew) != getFormatDistance(
                                showKm,
                                distanceOld
                            )
                        ) {
                            val track = TracksRecord.get(viewTrackInf.id)
                            if (track != null) {
                                track.distance2location = distNew.toLong()
                                track.save(false)
                            }
                        }
                    }
                    (view as TextView).text = getFormatDistance(showKm, distNew)
                    if (distanceOld != 0) {
                        view.setTextColor(Color.WHITE)
                    } else {
                        view.setTextColor(
                            ContextCompat.getColor(
                                context, R.color.distance_font
                            )
                        )
                    }
                } else {
                    (view.parent as ViewGroup).visibility = View.INVISIBLE
                }
                res = true
            }
        } catch (e: Exception) {
            try {
                val viewTrackInf = getViewTrackInfoFromCursor(tracksGesSumCursor, withSum)
                val track = TracksRecord.get(viewTrackInf.id)
                if (track != null) {
                    val trackName = track.trackname
                    Timber.e(trackName)
                }
            } catch (_: Exception) {
            }
            Timber.e(e)
        }
        return res
    }

    private fun getViewTrackInfoFromCursor(
        cursor: Cursor,
        withSum: Boolean
    ): ViewTrackInfoFromCursor {
        val result = ViewTrackInfoFromCursor()
        if (withSum) {
            val trackGes = TracksGesSumRecord.fromCursor(cursor)
            result.id = trackGes.id
            result.latitude = trackGes.latitude
            result.longitude = trackGes.longitude
            result.trackStatus = trackGes.trackstatus
        } else {
            val trackGes = TracksgesRecord.fromCursor(cursor)
            result.id = trackGes.id
            result.latitude = trackGes.latitude
            result.longitude = trackGes.longitude
            result.trackStatus = trackGes.trackstatus
        }
        return result
    }

    private class ViewTrackInfoFromCursor {
        var trackStatus: String? = null
        var latitude = 0.0
        var longitude = 0.0
        var id: Long = 0
    }

    companion object {
        val projectionGesSum = arrayOf(
            TracksGesSum.N_U_EVENTS,
            TracksGesSum.TRACKNAME,
            TracksGesSum.DISTANCE2LOCATION,
            TracksGesSum.RATING,
            TracksGesSum.OPENMONDAYS,
            TracksGesSum.OPENTUESDAYS,
            TracksGesSum.OPENWEDNESDAY,
            TracksGesSum.OPENTHURSDAY,
            TracksGesSum.OPENFRIDAY,
            TracksGesSum.OPENSATURDAY,
            TracksGesSum.OPENSUNDAY,
            TracksGesSum.COUNTRY,
            TracksGesSum.TRACKACCESS,
            TracksGesSum.PICTURECOUNT,
            TracksGesSum.EVENTCOUNT,
            TracksGesSum._ID
        )
        val toGesSum = intArrayOf(
            R.id.tr_list_event,
            R.id.tr_name,
            R.id.tr_detail_distance,
            R.id.tr_ratingBar,
            R.id.tr_mo,
            R.id.tr_tu,
            R.id.tr_we,
            R.id.tr_th,
            R.id.tr_fr,
            R.id.tr_sa,
            R.id.tr_so,
            R.id.tr_country,
            R.id.tr_track_access,
            R.id.tr_camera,
            R.id.tr_calendar
        )
        val projectionGes = arrayOf(
            Tracksges.N_U_EVENTS,
            Tracksges.TRACKNAME,
            Tracksges.DISTANCE2LOCATION,
            Tracksges.RATING,
            Tracksges.OPENMONDAYS,
            Tracksges.OPENTUESDAYS,
            Tracksges.OPENWEDNESDAY,
            Tracksges.OPENTHURSDAY,
            Tracksges.OPENFRIDAY,
            Tracksges.OPENSATURDAY,
            Tracksges.OPENSUNDAY,
            Tracksges.COUNTRY,
            Tracksges.TRACKACCESS,
            Tracksges._ID
        )
        val toGes = intArrayOf(
            R.id.tr_list_event,
            R.id.tr_name,
            R.id.tr_detail_distance,
            R.id.tr_ratingBar,
            R.id.tr_mo,
            R.id.tr_tu,
            R.id.tr_we,
            R.id.tr_th,
            R.id.tr_fr,
            R.id.tr_sa,
            R.id.tr_so,
            R.id.tr_country,
            R.id.tr_calendar
        )
    }

}
