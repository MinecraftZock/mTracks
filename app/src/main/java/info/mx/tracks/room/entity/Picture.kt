package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_PICTURE, indices = arrayOf(Index(value = ["trackId"], name = "PictureTrackIdIndex")))
data class Picture(
    @ColumnInfo(name = "trackId") var trackId: Long = 0,
    @ColumnInfo(name = "localfile") var localfile: String = "",
    @ColumnInfo(name = "localthumb") var localthumb: String = "",
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "deleted") var deleted: Int = 0, // TODO boolean
    @ColumnInfo(name = "approved") var approved: Int = 0, // TODO boolean
    @ColumnInfo(name = "androidid") var androidid: String = ""
) : BaseEntity() {
    @Ignore
    constructor() : this(0, "", "", "", 0, 0, "")

    override fun toString(): String {
        return id.toString() + ":" + changed.toString()
    }
}
