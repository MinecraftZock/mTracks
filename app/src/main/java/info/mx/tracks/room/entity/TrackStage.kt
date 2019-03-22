package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_TRACKSTAGE, indices = arrayOf(Index(value = ["trackId"], name = "TrackStageTrackIdIndex")))
data class TrackStage(
    @ColumnInfo(name = "trackId") var trackId: Long = 0,
    @ColumnInfo(name = "updated") var updated: Long = 0
) : Track() {
    override fun toString(): String {
        return id.toString() + ":" + changed.toString()
    }
}
