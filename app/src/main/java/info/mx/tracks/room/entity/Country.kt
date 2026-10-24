package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_COUNTRY)
data class Country(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "country") var country: String = "",
    @ColumnInfo(name = "show") var show: Int = 0
) {
    @Ignore
    constructor() : this(null, "", 0)
}
