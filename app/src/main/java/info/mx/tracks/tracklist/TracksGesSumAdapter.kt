package info.mx.tracks.tracklist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.hannes.commonlib.DateHelper
import info.mx.commonlib.LocationHelper
import info.mx.core_generated.prefs.MxPreferences
import info.mx.tracks.R
import info.mx.tracks.common.BitmapHelper
import info.mx.tracks.common.SecHelper
import info.mx.tracks.common.setDayLayout
import info.mx.tracks.room.entity.TracksGesSum
import info.mx.tracks.util.getDrawableIdentifier
import timber.log.Timber
import java.util.Locale
import kotlin.math.roundToInt

class TracksGesSumAdapter(
    private val context: Context,
    private val onItemClick: (Long, Int) -> Unit,
    private val onItemLongClick: (Long) -> Boolean
) : ListAdapter<TracksGesSum, TracksGesSumAdapter.TrackViewHolder>(TrackDiffCallback()) {

    private var myLoc: Location? = null
    private val shortWeekdays: Array<String> = DateHelper.shortWeekdays
    private val showKm: Boolean = MxPreferences.instance.unitsKm

    fun setMyLocation(location: Location?) {
        myLoc = location
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track, position, context, myLoc, shortWeekdays, showKm)
    }

    fun getTrackId(position: Int): Long {
        Timber.d("getTrackId position=$position of ${this.itemCount}")
        return getItem(position).id
    }

    class TrackViewHolder(
        itemView: View,
        private val onItemClick: (Long, Int) -> Unit,
        private val onItemLongClick: (Long) -> Boolean
    ) : RecyclerView.ViewHolder(itemView) {

        private val trackNameView: TextView = itemView.findViewById(R.id.tr_name)
        private val trackAccessView: ImageView = itemView.findViewById(R.id.tr_track_access)
        private val countryView: ImageView = itemView.findViewById(R.id.tr_country)
        private val cameraView: ImageView = itemView.findViewById(R.id.tr_camera)
        private val calendarView: ImageView = itemView.findViewById(R.id.tr_calendar)
        private val ratingBarView: RatingBar = itemView.findViewById(R.id.tr_ratingBar)
        private val distanceTextView: TextView = itemView.findViewById(R.id.tr_detail_distance)
        
        private val mondayView: TextView = itemView.findViewById(R.id.tr_mo)
        private val tuesdayView: TextView = itemView.findViewById(R.id.tr_tu)
        private val wednesdayView: TextView = itemView.findViewById(R.id.tr_we)
        private val thursdayView: TextView = itemView.findViewById(R.id.tr_th)
        private val fridayView: TextView = itemView.findViewById(R.id.tr_fr)
        private val saturdayView: TextView = itemView.findViewById(R.id.tr_sa)
        private val sundayView: TextView = itemView.findViewById(R.id.tr_so)

        @SuppressLint("DiscouragedApi")
        fun bind(
            track: TracksGesSum,
            position: Int,
            context: Context,
            myLoc: Location?,
            shortWeekdays: Array<String>,
            showKm: Boolean
        ) {
            trackNameView.text = track.trackname ?: ""
            Timber.d(trackNameView.text.toString())
            
            // Track access icon
            val iconRes = when (track.trackaccess) {
                "R" -> R.drawable.flag_race_hell
                "M" -> R.drawable.flag_member_hell
                "D" -> R.drawable.flag_dealer_large2x
                else -> R.drawable.flag_blau_hell
            }
            trackAccessView.setImageBitmap(
                BitmapHelper.getBitmap(context, iconRes, track.trackstatus)
            )
            
            // Country flag
            val countryIconName = track.country.lowercase(Locale.getDefault()) + "2x"
            val countryIconId = context.resources.getDrawableIdentifier(countryIconName, context.packageName)
            countryView.setImageResource(countryIconId)
            
            // Picture and event indicators
            cameraView.visibility = if (track.picturecount == 0) View.GONE else View.VISIBLE
            calendarView.visibility = if (track.eventcount == 0) View.GONE else View.VISIBLE
            
            // Rating
            ratingBarView.rating = track.rating.toFloat()
            
            // Opening days
            mondayView.text = shortWeekdays[2]
            mondayView.setDayLayout(track.openmondays == 1)
            
            tuesdayView.text = shortWeekdays[3]
            tuesdayView.setDayLayout(track.opentuesdays == 1)
            
            wednesdayView.text = shortWeekdays[4]
            wednesdayView.setDayLayout(track.openwednesday == 1)
            
            thursdayView.text = shortWeekdays[5]
            thursdayView.setDayLayout(track.openthursday == 1)
            
            fridayView.text = shortWeekdays[6]
            fridayView.setDayLayout(track.openfriday == 1)
            
            saturdayView.text = shortWeekdays[7]
            saturdayView.setDayLayout(track.opensaturday == 1)
            
            sundayView.text = shortWeekdays[1]
            sundayView.setDayLayout(track.opensunday == 1)
            
            // Distance
            var distNew = track.distance2location
            if (myLoc != null) {
                val trackLoc = Location("track")
                trackLoc.latitude = SecHelper.entcryptXtude(track.latitude)
                trackLoc.longitude = SecHelper.entcryptXtude(track.longitude)
                distNew = myLoc.distanceTo(trackLoc).roundToInt()
            }
            distanceTextView.text = LocationHelper.getFormatDistance(showKm, distNew)
            
            // Click listeners
            itemView.setOnClickListener {
                onItemClick(track.id, position)
            }
            
            itemView.setOnLongClickListener {
                onItemLongClick(track.id)
            }
        }
    }

    private class TrackDiffCallback : DiffUtil.ItemCallback<TracksGesSum>() {
        override fun areItemsTheSame(oldItem: TracksGesSum, newItem: TracksGesSum): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TracksGesSum, newItem: TracksGesSum): Boolean {
            return oldItem == newItem
        }
    }
}




