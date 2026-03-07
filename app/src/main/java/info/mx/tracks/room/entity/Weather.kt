package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_WEATHER, indices = arrayOf(Index(value = ["trackId"], name = "WeatherTrackIdIndex")))
data class Weather(
    @ColumnInfo(name = "trackId") var trackId: Long = 0,
    @ColumnInfo(name = "type") var type: String = "D", //D=day H=hour
    @ColumnInfo(name = "content") var content: String = "",
    @ColumnInfo(name = "dt") var dt: Int = 0
) : BaseEntity() {
    @Ignore
    constructor() : this(0, "D", "", 0)
}
