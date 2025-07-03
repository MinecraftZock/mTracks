package info.hannes.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures")
data class PictureStage(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "restId")
    val restId: Long,
    
    @ColumnInfo(name = "trackId")
    val trackId: Long,
    
    @ColumnInfo(name = "trackRestId")
    val trackRestId: Long,
    
    @ColumnInfo(name = "changed")
    val changed: Long,
    
    @ColumnInfo(name = "www")
    val www: String = "",
    
    @ColumnInfo(name = "LocalFile")
    val localFile: String? = null,
    
    @ColumnInfo(name = "uninteressant")
    val uninteressant: Int = 0
)