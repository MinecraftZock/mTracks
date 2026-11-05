package info.mx.tracks.room.entity

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "CountryCount",
    value = "select C.id as id, C.country as country, C.show as show, ifnull(S.count,0) as count " +
            "from Country as C " +
            "left join CountrySum as S on C.country = S.Country " +
            "group by S.Country"
)
data class CountryCount(
    val id: Long,
    val country: String,
    val show: Int,
    val count: Int
)
