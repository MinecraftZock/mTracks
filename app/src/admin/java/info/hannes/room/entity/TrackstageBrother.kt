package info.hannes.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trackstageBrother")
data class TrackstageBrother(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "restId")
    val restId: Long,
    
    @ColumnInfo(name = "trackRestId")
    val trackRestId: Long,
    
    @ColumnInfo(name = "created")
    val created: Long = 0,
    
    @ColumnInfo(name = "Trackname")
    val trackname: String,
    
    @ColumnInfo(name = "Longitude")
    val longitude: Double,
    
    @ColumnInfo(name = "Latitude")
    val latitude: Double,
    
    @ColumnInfo(name = "Country")
    val country: String = "",
    
    @ColumnInfo(name = "androidid")
    val androidid: String? = null,
    
    @ColumnInfo(name = "urlDetailXml")
    val urlDetailXml: String? = null,
    
    @ColumnInfo(name = "contentDetailXml")
    val contentDetailXml: String = "",
    
    @ColumnInfo(name = "urlPhoto")
    val urlPhoto: String? = null,
    
    @ColumnInfo(name = "contentPhoto")
    val contentPhoto: String = "",
    
    @ColumnInfo(name = "url")
    val url: String? = null,
    
    @ColumnInfo(name = "Phone")
    val phone: String? = null,
    
    @ColumnInfo(name = "Notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "Votes")
    val votes: String? = null,
    
    @ColumnInfo(name = "Openmondays")
    val openmondays: Int = 0,
    
    @ColumnInfo(name = "Opentuesdays")
    val opentuesdays: Int = 0,
    
    @ColumnInfo(name = "Openwednesday")
    val openwednesday: Int = 0,
    
    @ColumnInfo(name = "Openthursday")
    val openthursday: Int = 0,
    
    @ColumnInfo(name = "Openfriday")
    val openfriday: Int = 0,
    
    @ColumnInfo(name = "Opensaturday")
    val opensaturday: Int = 0,
    
    @ColumnInfo(name = "Opensunday")
    val opensunday: Int = 0,
    
    @ColumnInfo(name = "Hoursmonday")
    val hoursmonday: String? = null,
    
    @ColumnInfo(name = "Hourstuesday")
    val hourstuesday: String? = null,
    
    @ColumnInfo(name = "Hourswednesday")
    val hourswednesday: String? = null,
    
    @ColumnInfo(name = "Hoursthursday")
    val hoursthursday: String? = null,
    
    @ColumnInfo(name = "Hoursfriday")
    val hoursfriday: String? = null,
    
    @ColumnInfo(name = "Hourssaturday")
    val hourssaturday: String? = null,
    
    @ColumnInfo(name = "Hourssunday")
    val hourssunday: String? = null
)