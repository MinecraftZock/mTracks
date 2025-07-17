package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorits")
data class Favorit(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "trackRestId")
    val trackRestId: Long
)