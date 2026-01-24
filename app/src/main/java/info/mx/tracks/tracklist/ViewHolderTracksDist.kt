package info.mx.tracks.tracklist

import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView

@Suppress("PropertyName")
data class ViewHolderTracksDist(
    var tr_list_event: TextView,
    var tr_name: TextView,
    var tr_distance: TextView,
    var tr_ratingBar: RatingBar,
    var tr_mo: TextView,
    var tr_tu: TextView,
    var tr_we: TextView,
    var tr_th: TextView,
    var tr_fr: TextView,
    var tr_sa: TextView,
    var tr_so: TextView,
    var tr_country: ImageView,
    var tr_track_access: ImageView,
    var tr_camera: ImageView,
    var tr_calendar: ImageView
)
