package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_EVENT, indices = arrayOf(Index(value = ["trackRestId"], name = "EventTrackIdIndex")))
data class Event(
    @ColumnInfo(name = "trackRestId") var trackRestId: Long = 0,
    @ColumnInfo(name = "seriesRestId") var seriesRestId: Long = 0,
    @ColumnInfo(name = "eventDate") var eventDate: Long = 0,
    @ColumnInfo(name = "comment") var comment: String = "",
    @ColumnInfo(name = "deleted") var deleted: Int = 0, // TODO boolean
    @ColumnInfo(name = "approved") var approved: Int = 0, // TODO boolean
    @ColumnInfo(name = "androidid") var androidid: String = ""
) : BaseEntity() {
    @Ignore
    constructor() : this(0, 0, 0, "", 0, 0, "")

    override fun toString(): String {
        return id.toString() + ":" + changed.toString()
    }
}
