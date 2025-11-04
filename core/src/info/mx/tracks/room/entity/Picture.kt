package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures")
data class Picture(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "restId")
    val restId: Long,
    
    @ColumnInfo(name = "changed")
    val changed: Long,
    
    @ColumnInfo(name = "username")
    val username: String? = null,
    
    @ColumnInfo(name = "comment")
    val comment: String? = null,
    
    @ColumnInfo(name = "trackRestId")
    val trackRestId: Long,
    
    @ColumnInfo(name = "approved")
    val approved: Int = 0,
    
    @ColumnInfo(name = "deleted")
    val deleted: Int = 0,
    
    @ColumnInfo(name = "localfile")
    val localfile: String? = null,
    
    @ColumnInfo(name = "localthumb")
    val localthumb: String? = null
)