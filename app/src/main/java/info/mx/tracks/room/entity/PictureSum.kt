package info.mx.tracks.room.entity

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "PictureSum",
    value = "select id as id, " +
            "trackId as trackId, " +
            "count(*) as picturecount " +
            "from Picture " +
            "where approved > -1 " +
            "group by trackId"
)
data class PictureSum(
    val id: Long,
    val trackId: Long,
    val picturecount: Int
)

