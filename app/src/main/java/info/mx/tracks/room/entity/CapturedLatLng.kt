package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_CAPTUREDLATLNG)
data class CapturedLatLng(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "lat") var lat: Double = 0.0,
        @ColumnInfo(name = "lon ") var lon: Double = 0.0,
        @ColumnInfo(name = "time") var time: Long = 0,
        @ColumnInfo(name = "distToNearest") var distToNearest: Int = 0,
        @ColumnInfo(name = "distToLast") var distanceToLast: Int = 0,
        @ColumnInfo(name = "aktion") var action: String = "",
        @ColumnInfo(name = "extra") var extra: String = "",
        @ColumnInfo(name = "trackname") var trackname: String = ""

) {
    @Ignore
    constructor() : this(null, 0.0, 0.0, System.currentTimeMillis(), 0, 0, "", "", "")
}
