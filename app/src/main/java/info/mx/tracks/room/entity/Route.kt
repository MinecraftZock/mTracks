package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_ROUTE, indices = arrayOf(Index(value = ["trackId"], name = "RouteTrackIdIndex")))
data class Route(
        @ColumnInfo(name = "trackId") var trackId: Long = 0,
        @ColumnInfo(name = "content") var content: String = "",
        @ColumnInfo(name = "logitude") var logitude: Double = 0.0,
        @ColumnInfo(name = "langitude") var langitude: Double = 0.0
) : BaseEntity() {
    @Ignore
    constructor() : this(0, "", 0.0, 0.0)
}
