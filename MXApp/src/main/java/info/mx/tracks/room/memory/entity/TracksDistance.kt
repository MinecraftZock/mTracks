package info.mx.tracks.room.memory.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TracksDistance(
        @PrimaryKey() var id: Long?,
        @ColumnInfo(name = "lat") var lat: Double = 0.0,
        @ColumnInfo(name = "lon ") var lon: Double = 0.0,
        @ColumnInfo(name = "distance") var distance: Long = 0

) {
//    @Ignore
//    constructor() : this(null, 0.0, 0.0, System.currentTimeMillis(), 0, 0, "", "", "")
}
