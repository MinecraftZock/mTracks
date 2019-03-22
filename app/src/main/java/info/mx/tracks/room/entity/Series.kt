package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_SERIES)
data class Series(
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "seriesUrl") var seriesUrl: String = ""
) : BaseEntity() {
    @Ignore
    constructor() : this("", "")

    override fun toString(): String {
        return id.toString() + ":" + changed.toString()
    }
}
