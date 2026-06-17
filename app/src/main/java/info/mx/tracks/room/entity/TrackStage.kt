package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import info.mx.tracks.room.DBMigrationUtil

@Entity(
    tableName = DBMigrationUtil.TABLE_TRACKSTAGE,
    inheritSuperIndices = true,
    indices = [Index(value = ["trackId"], name = "TrackStageTrackIdIndex")]
)
data class TrackStage(
    @ColumnInfo(name = "trackId") var trackId: Long = 0,
    @ColumnInfo(name = "updated") var updated: Long = 0,
    @ColumnInfo(name = "insLongitude") var insLongitude: Double = 0.0,
    @ColumnInfo(name = "insLatitude") var insLatitude: Double = 0.0,
    @ColumnInfo(name = "insDistance") var insDistance: Long = 0
) : Track() {
    override fun toString(): String {
        return id.toString() + ":" + changed.toString()
    }
}
