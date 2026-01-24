package info.mx.tracks.tracklist

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import info.hannes.commonlib.DateHelper
import info.hannes.commonlib.LocationHelper.getFormatDistance
import info.mx.tracks.R
import info.mx.tracks.common.BitmapHelper
import info.mx.tracks.common.setDayLayout
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.room.memory.entity.TracksDistance
import info.mx.tracks.sqlite.TracksGesSumRecord
import info.mx.tracks.util.getDrawableIdentifier
import java.util.*
import kotlin.math.roundToInt

class AdapterTracksSort(private val context: Context) : BaseAdapter() {
    private var tracksDistance: List<TracksDistance>? = null
    private var tracksCached: Array<TracksGesSumRecord?>
    private val shortWeekdays: Array<String> = DateHelper.shortWeekdays
    private val showKm: Boolean

    init {
        val prefs = MxPreferences.getInstance()
        showKm = prefs.unitsKm
        tracksCached = arrayOfNulls(0)
    }

    fun updateTracks(tracksDistance: List<TracksDistance>) {
        this.tracksDistance = tracksDistance
        tracksCached = arrayOfNulls(tracksDistance.size)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (tracksDistance == null) 0 else tracksDistance!!.size
    }

    override fun getItem(position: Int): TracksDistance {
        return tracksDistance!![position]
    }

    override fun getItemId(position: Int): Long {
        return tracksDistance!![position].id!!
    }

    override fun getView(position: Int, convertViewGiven: View?, parent: ViewGroup?): View {
        var convertView = convertViewGiven
        val tr_list_event: TextView
        val tr_name: TextView
        val tr_distance: TextView
        val tr_ratingBar: RatingBar
        val tr_mo: TextView
        val tr_tu: TextView
        val tr_we: TextView
        val tr_th: TextView
        val tr_fr: TextView
        val tr_sa: TextView
        val tr_so: TextView
        val tr_country: ImageView
        val tr_track_access: ImageView
        val tr_camera: ImageView
        val tr_calendar: ImageView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_track, parent, false)
            tr_list_event = convertView.findViewById(R.id.tr_list_event)
            tr_name = convertView.findViewById(R.id.tr_name)
            tr_distance = convertView.findViewById(R.id.tr_detail_distance)
            tr_ratingBar = convertView.findViewById(R.id.tr_ratingBar)
            tr_mo = convertView.findViewById(R.id.tr_mo)
            tr_tu = convertView.findViewById(R.id.tr_tu)
            tr_we = convertView.findViewById(R.id.tr_we)
            tr_th = convertView.findViewById(R.id.tr_th)
            tr_fr = convertView.findViewById(R.id.tr_fr)
            tr_sa = convertView.findViewById(R.id.tr_sa)
            tr_so = convertView.findViewById(R.id.tr_so)
            tr_country = convertView.findViewById(R.id.tr_country)
            tr_track_access = convertView.findViewById(R.id.tr_track_access)
            tr_camera = convertView.findViewById(R.id.tr_camera)
            tr_calendar = convertView.findViewById(R.id.tr_calendar)
            convertView.tag = ViewHolderTracksDist(
                tr_list_event = tr_list_event,
                tr_name = tr_name,
                tr_distance = tr_distance,
                tr_ratingBar = tr_ratingBar,
                tr_mo = tr_mo,
                tr_tu = tr_tu,
                tr_we = tr_we,
                tr_th = tr_th,
                tr_fr = tr_fr,
                tr_sa = tr_sa,
                tr_so = tr_so,
                tr_country = tr_country,
                tr_track_access = tr_track_access,
                tr_camera = tr_camera,
                tr_calendar = tr_calendar
            )
        } else {
            val viewHolder = convertView.tag as ViewHolderTracksDist
            tr_name = viewHolder.tr_name
            tr_distance = viewHolder.tr_distance
            tr_ratingBar = viewHolder.tr_ratingBar
            tr_mo = viewHolder.tr_mo
            tr_tu = viewHolder.tr_tu
            tr_we = viewHolder.tr_we
            tr_th = viewHolder.tr_th
            tr_fr = viewHolder.tr_fr
            tr_sa = viewHolder.tr_sa
            tr_so = viewHolder.tr_so
            tr_country = viewHolder.tr_country
            tr_track_access = viewHolder.tr_track_access
            tr_camera = viewHolder.tr_camera
            tr_calendar = viewHolder.tr_calendar
        }
        val (id1, _, _, distance) = getItem(position)
        var track = tracksCached[position]
        if (track == null) {
            track = TracksGesSumRecord.get(id1!!)
            tracksCached[position] = track
        }
        if (track != null) {
            tr_name.text = track.trackname
            var iconRes = R.drawable.flag_blau_hell
            if (track.trackaccess != null) {
                iconRes = when (track.trackaccess) {
                    "R" -> R.drawable.flag_race_hell
                    "M" -> R.drawable.flag_member_hell
                    "D" -> R.drawable.flag_dealer_large2x
                    else -> R.drawable.flag_blau_hell
                }
            }
            tr_track_access.setImageBitmap(
                BitmapHelper.getBitmap(
                    context,
                    iconRes,
                    track.trackstatus
                )
            )
            tr_camera.visibility =
                if (track.picturecount.toInt() == 0) ImageView.GONE else ImageView.VISIBLE
            tr_calendar.visibility =
                if (track.eventcount.toInt() == 0) ImageView.GONE else ImageView.VISIBLE
            val country = track.country.lowercase(Locale.getDefault()) + "2x"
            val id = context.resources.getDrawableIdentifier(country, context.packageName)
            tr_country.setImageResource(id)
            tr_ratingBar.rating = track.rating.toFloat()
            tr_mo.setDayLayout(track.openmondays == 1L)
            tr_tu.setDayLayout(track.opentuesdays == 1L)
            tr_we.setDayLayout(track.openwednesday == 1L)
            tr_th.setDayLayout(track.openthursday == 1L)
            tr_fr.setDayLayout(track.openfriday == 1L)
            tr_sa.setDayLayout(track.opensaturday == 1L)
            tr_so.setDayLayout(track.opensunday == 1L)
            tr_mo.text = shortWeekdays[2]
            tr_tu.text = shortWeekdays[3]
            tr_we.text = shortWeekdays[4]
            tr_th.text = shortWeekdays[5]
            tr_fr.text = shortWeekdays[6]
            tr_sa.text = shortWeekdays[7]
            tr_so.text = shortWeekdays[1]
            tr_distance.text = getFormatDistance(
                showKm,
                distance.toFloat().roundToInt()
            )
            if (track.distance2location != 0L) {
                tr_distance.setTextColor(Color.WHITE)
            } else {
                tr_distance.setTextColor(ContextCompat.getColor(context, R.color.distance_font))
            }
        }
        return convertView!!
    }

}
