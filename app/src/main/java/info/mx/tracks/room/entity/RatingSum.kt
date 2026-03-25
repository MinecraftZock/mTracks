package info.mx.tracks.room.entity

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "RatingSum",
    value = "select id as id, " +
            "trackId as trackId, " +
            "count(*) as ratingcount, " +
            "round(avg(rating),1) as ratingavg " +
            "from Comment " +
            "where approved > -1 " +
            "group by trackId"
)
data class RatingSum(
    val id: Long,
    val trackId: Long,
    val ratingcount: Int,
    val ratingavg: Double
)

