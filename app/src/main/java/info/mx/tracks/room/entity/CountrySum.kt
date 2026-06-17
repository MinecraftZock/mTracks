package info.mx.tracks.room.entity

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "CountrySum",
    value = "select country, count(*) as count from Track group by Country"
)
data class CountrySum(
    val country: String,
    val count: Int,
)
