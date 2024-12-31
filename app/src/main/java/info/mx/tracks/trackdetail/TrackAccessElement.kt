package info.mx.tracks.trackdetail

import info.mx.tracks.R

class TrackAccessElement(val id: String, val text: String) {
    val imageResId: Int
        get() {
            val imageResId: Int = when (id) {
                "M" -> R.drawable.flag_member_hell_klein
                "R" -> R.drawable.flag_race_hell_klein
                "D" -> R.drawable.flag_dealer_large2x
                else -> R.drawable.flag_blau_hell_klein
            }
            return imageResId
        }

    override fun equals(other: Any?): Boolean { // for contains and indexof
        return (other is TrackAccessElement && id == other.id)
    }
}