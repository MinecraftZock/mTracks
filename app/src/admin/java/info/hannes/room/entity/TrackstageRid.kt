package info.hannes.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trackstageRid")
data class TrackstageRid(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "restId")
    val restId: Long,
    
    @ColumnInfo(name = "created")
    val created: Long = 0,
    
    @ColumnInfo(name = "urlMapPointsJson")
    val urlMapPointsJson: String? = null,
    
    @ColumnInfo(name = "urlDetailXml")
    val urlDetailXml: String? = null,
    
    @ColumnInfo(name = "contentMapPointsJson")
    val contentMapPointsJson: String? = null,
    
    @ColumnInfo(name = "contentDetailXml")
    val contentDetailXml: String? = null
)