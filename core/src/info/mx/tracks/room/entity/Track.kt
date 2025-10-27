package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    
    @ColumnInfo(name = "restId")
    val restId: Long,
    
    @ColumnInfo(name = "changed")
    val changed: Long,
    
    @ColumnInfo(name = "trackname")
    val trackname: String,
    
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    
    @ColumnInfo(name = "approved")
    val approved: Int,
    
    @ColumnInfo(name = "country")
    val country: String,
    
    @ColumnInfo(name = "url")
    val url: String? = null,
    
    @ColumnInfo(name = "fees")
    val fees: String? = null,
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "contact")
    val contact: String? = null,
    
    @ColumnInfo(name = "notes_en")
    val notesEn: String? = null,
    
    @ColumnInfo(name = "metatext")
    val metatext: String? = null,
    
    @ColumnInfo(name = "licence")
    val licence: String? = null,
    
    @ColumnInfo(name = "kidstrack")
    val kidstrack: Int = 0,
    
    @ColumnInfo(name = "openmondays")
    val openmondays: Int = 0,
    
    @ColumnInfo(name = "opentuesdays")
    val opentuesdays: Int = 0,
    
    @ColumnInfo(name = "openwednesday")
    val openwednesday: Int = 0,
    
    @ColumnInfo(name = "openthursday")
    val openthursday: Int = 0,
    
    @ColumnInfo(name = "openfriday")
    val openfriday: Int = 0,
    
    @ColumnInfo(name = "opensaturday")
    val opensaturday: Int = 0,
    
    @ColumnInfo(name = "opensunday")
    val opensunday: Int = 0,
    
    @ColumnInfo(name = "hoursmonday")
    val hoursmonday: String? = null,
    
    @ColumnInfo(name = "hourstuesday")
    val hourstuesday: String? = null,
    
    @ColumnInfo(name = "hourswednesday")
    val hourswednesday: String? = null,
    
    @ColumnInfo(name = "hoursthursday")
    val hoursthursday: String? = null,
    
    @ColumnInfo(name = "hoursfriday")
    val hoursfriday: String? = null,
    
    @ColumnInfo(name = "hourssaturday")
    val hourssaturday: String? = null,
    
    @ColumnInfo(name = "hourssunday")
    val hourssunday: String? = null,
    
    @ColumnInfo(name = "tracklength")
    val tracklength: Int = 0,
    
    @ColumnInfo(name = "soiltype")
    val soiltype: String? = null,
    
    @ColumnInfo(name = "camping")
    val camping: Int = 0,
    
    @ColumnInfo(name = "shower")
    val shower: Int = 0,
    
    @ColumnInfo(name = "cleaning")
    val cleaning: Int = 0,
    
    @ColumnInfo(name = "electricity")
    val electricity: Int = 0,
    
    @ColumnInfo(name = "distance2location")
    val distance2location: Int = 0,
    
    @ColumnInfo(name = "supercross")
    val supercross: Int = 0,
    
    @ColumnInfo(name = "trackaccess")
    val trackaccess: Int = 0,
    
    @ColumnInfo(name = "logo_u_r_l")
    val logoUrl: String? = null,
    
    @ColumnInfo(name = "showroom")
    val showroom: Int = 0,
    
    @ColumnInfo(name = "workshop")
    val workshop: Int = 0,
    
    @ColumnInfo(name = "validuntil")
    val validuntil: String? = null,
    
    @ColumnInfo(name = "brands")
    val brands: String? = null,
    
    @ColumnInfo(name = "nu_events")
    val nuEvents: Int = 0,
    
    @ColumnInfo(name = "facebook")
    val facebook: String? = null,
    
    @ColumnInfo(name = "trackstage")
    val trackstage: Int = 0,
    
    @ColumnInfo(name = "region")
    val region: String? = null,
    
    @ColumnInfo(name = "difficulty")
    val difficulty: Int = 0,
    
    @ColumnInfo(name = "owner")
    val owner: String? = null,
    
    @ColumnInfo(name = "racingonly")
    val racingonly: Int = 0,
    
    @ColumnInfo(name = "club")
    val club: String? = null,
    
    @ColumnInfo(name = "ratingsum")
    val ratingsum: Int = 0,
    
    @ColumnInfo(name = "ratingcount")
    val ratingcount: Int = 0,
    
    @ColumnInfo(name = "ratingavrg")
    val ratingavrg: Double = 0.0,
    
    @ColumnInfo(name = "favorite")
    val favorite: Int = 0,
    
    @ColumnInfo(name = "votesum")
    val votesum: Int = 0,
    
    @ColumnInfo(name = "votecount")
    val votecount: Int = 0,
    
    @ColumnInfo(name = "votelast")
    val votelast: Int = 0,
    
    @ColumnInfo(name = "sync")
    val sync: Int = 0,
    
    @ColumnInfo(name = "locallat")
    val locallat: Double = 0.0,
    
    @ColumnInfo(name = "locallng")
    val locallng: Double = 0.0,
    
    @ColumnInfo(name = "localdistance")
    val localdistance: Int = 0,
    
    @ColumnInfo(name = "ownpicture")
    val ownpicture: Int = 0
)