package info.mx.tracks.room.entity

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "EventSum",
    value = "select id as id, " +
            "trackRestId as trackRestId, " +
            "count(*) as eventcount " +
            "from Event " +
            "where approved > -1 and eventDate > strftime('%s', 'now') " +
            "group by trackRestId"
)
data class EventSum(
    val id: Long,
    val trackRestId: Long,
    val eventcount: Int
)

