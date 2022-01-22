package info.mx.tracks.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import info.mx.tracks.room.DBMigrationUtil

@Entity(
    tableName = DBMigrationUtil.TABLE_TRACK,
    inheritSuperIndices = true,
    indices = [Index(value = ["id"], name = "TrackIdIndex")]
)
open class Track(
    @ColumnInfo(name = "trackname") var trackname: String = "",
    @ColumnInfo(name = "longitude") var longitude: Double = 0.0,
    @ColumnInfo(name = "latitude") var latitude: Double = 0.0,
    @ColumnInfo(name = "approved") var approved: Int = 0,
    @ColumnInfo(name = "country") var country: String = "",
    @ColumnInfo(name = "url") var url: String = "",
    @ColumnInfo(name = "fees") var fees: String = "",
    @ColumnInfo(name = "phone") var phone: String = "",
    @ColumnInfo(name = "contact") var contact: String = "",
    @ColumnInfo(name = "metatext") var metatext: String = "",
    @ColumnInfo(name = "licence") var licence: String = "",
    @ColumnInfo(name = "kidstrack") var kidstrack: Int = -1,
    @ColumnInfo(name = "openmondays") var openmondays: Int = 1,
    @ColumnInfo(name = "opentuesdays") var opentuesdays: Int = 1,
    @ColumnInfo(name = "openwednesday") var openwednesday: Int = 1,
    @ColumnInfo(name = "openthursday") var openthursday: Int = 1,
    @ColumnInfo(name = "openfriday") var openfriday: Int = 1,
    @ColumnInfo(name = "opensaturday") var opensaturday: Int = 1,
    @ColumnInfo(name = "opensunday") var opensunday: Int = 1,
    @ColumnInfo(name = "hoursmonday") var hoursmonday: String = "",
    @ColumnInfo(name = "hourstuesday") var hourstuesday: String = "",
    @ColumnInfo(name = "hourswednesday") var hourswednesday: String = "",
    @ColumnInfo(name = "hoursthursday") var hoursthursday: String = "",
    @ColumnInfo(name = "hoursfriday") var hoursfriday: String = "",
    @ColumnInfo(name = "hourssaturday") var hourssaturday: String = "",
    @ColumnInfo(name = "hourssunday") var hourssunday: String = "",
    @ColumnInfo(name = "tracklength") var tracklength: Int = 0,
    @ColumnInfo(name = "soiltype") var soiltype: Int = 1,
    @ColumnInfo(name = "camping") var camping: Int = -1,
    @ColumnInfo(name = "shower") var shower: Int = -1,
    @ColumnInfo(name = "cleaning") var cleaning: Int = -1,
    @ColumnInfo(name = "electricity") var electricity: Int = -1,
    @ColumnInfo(name = "distance2location") var distance2location: Int = 0,
    @ColumnInfo(name = "supercross") var supercross: Int = -1,
    @ColumnInfo(name = "showroom") var showroom: Int = -1,
    @ColumnInfo(name = "workshop") var workshop: Int = -1,
    @ColumnInfo(name = "validuntil") var validuntil: Int = 0,
    @ColumnInfo(name = "singletracks") var singletracks: Int = -1,
    @ColumnInfo(name = "campingrvrvhookup") var campingrvrvhookup: Int = -1,
    @ColumnInfo(name = "mxtrack") var mxtrack: Int = -1,
    @ColumnInfo(name = "a4x4") var a4x4: Int = -1,
    @ColumnInfo(name = "enduro") var enduro: Int = -1,
    @ColumnInfo(name = "utv") var utv: Int = -1,
    @ColumnInfo(name = "quad") var quad: Int = -1,
    @ColumnInfo(name = "areatype") var areatype: String = "",
    @ColumnInfo(name = "schwierigkeit") var schwierigkeit: Int = 0,
    @ColumnInfo(name = "indoor") var indoor: Int = 0,
    @ColumnInfo(name = "lastAsked") var lastAsked: Int = 0,
    @ColumnInfo(name = "trackaccess") var trackaccess: String = "N", //"R" "M" "D"
    @ColumnInfo(name = "logoURL") var logoURL: String = "",
    @ColumnInfo(name = "brands") var brands: String = "",
    @ColumnInfo(name = "facebook") var facebook: String = "",
    @ColumnInfo(name = "adress") var adress: String = "",
    @ColumnInfo(name = "feescamping") var feescamping: String = "",
    @ColumnInfo(name = "daysopen") var daysopen: String = "",
    @ColumnInfo(name = "noiselimit") var noiselimit: String = "",
    @ColumnInfo(name = "trackstatus") var trackstatus: String = "",
    @ColumnInfo(name = "andoridid") var andoridid: String = "",
    @ColumnInfo(name = "notes") var notes: String = ""
) : BaseEntity() {

    override fun toString(): String {
        return id.toString() + ":" + changed.toString()
    }
}
