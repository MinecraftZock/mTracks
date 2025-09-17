package info.hannes.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class Videos(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "restId")
    val restId: Long,
    
    @ColumnInfo(name = "trackId")
    val trackId: Long,
    
    @ColumnInfo(name = "changed")
    val changed: Long,
    
    @ColumnInfo(name = "www")
    val www: String = ""
)