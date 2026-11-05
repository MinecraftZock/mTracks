package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_FAVORIT)
data class Favorit(@PrimaryKey(autoGenerate = true) var id: Long?,
                   @ColumnInfo(name = "trackId") var trackId: Long = 0
) {
    @Ignore
    constructor() : this(null, 0)
}
