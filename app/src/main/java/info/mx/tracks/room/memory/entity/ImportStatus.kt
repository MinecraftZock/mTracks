package info.mx.tracks.room.memory.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImportStatus(
    @PrimaryKey() var id: Long?,
    @ColumnInfo(name = "msg") var msg: String = "",
    @ColumnInfo(name = "created") var created: Long = 0
) {
//    @Ignore
//    constructor() : this(null, 0.0, 0.0, System.currentTimeMillis(), 0, 0, "", "", "")
}
