package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country")
data class Country(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "country")
    val country: String,
    
    @ColumnInfo(name = "show")
    val show: Long = 0
)