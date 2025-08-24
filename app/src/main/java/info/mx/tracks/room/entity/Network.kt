package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_NETWORK)
data class Network(
        @ColumnInfo(name = "tracks") var tracks: Long = 0,
        @ColumnInfo(name = "reason") var reason: String = ""
) : BaseEntity() {
    @Ignore
    constructor() : this(0, "")
}
