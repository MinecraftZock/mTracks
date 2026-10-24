package info.mx.tracks.room.entity

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "TracksGes",
    value = "select " +
            "trackname as trackname, " +
            "approved as approved, " +
            "distance2location as distance2location, " +
            "openmondays as openmondays, " +
            "opentuesdays as opentuesdays, " +
            "openwednesday as openwednesday, " +
            "openthursday as openthursday, " +
            "openfriday as openfriday, " +
            "opensaturday as opensaturday, " +
            "opensunday as opensunday, " +
            "country as country, " +
            "trackaccess as trackaccess, " +
            "Track.id as id, " +
            "brands as brands, " +
            "metatext as metatext, " +
            "kidstrack as kidstrack, " +
            "supercross as supercross, " +
            "shower as shower, " +
            "cleaning as cleaning, " +
            "electricity as electricity, " +
            "camping as camping, " +
            "latitude as latitude, " +
            "longitude as longitude, " +
            "hoursmonday as hoursmonday, " +
            "hourstuesday as hourstuesday, " +
            "hourswednesday as hourswednesday, " +
            "hoursthursday as hoursthursday, " +
            "hoursfriday as hoursfriday, " +
            "hourssaturday as hourssaturday, " +
            "hourssunday as hourssunday, " +
            "validuntil as validuntil, " +
            "url as url, " +
            "phone as phone, " +
            "contact as contact, " +
            "notes as notes, " +
            "tracklength as tracklength, " +
            "soiltype as soiltype, " +
            "facebook as facebook, " +
            "adress as adress, " +
            "licence as licence, " +
            "fees as fees, " +
            "feescamping as feescamping, " +
            "daysopen as daysopen, " +
            "noiselimit as noiselimit, " +
            "campingrvrvhookup as campingrvrvhookup, " +
            "singletracks as singletracks, " +
            "mxtrack as mxtrack, " +
            "a4x4 as a4x4, " +
            "enduro as enduro, " +
            "utv as utv, " +
            "quad as quad, " +
            "trackstatus as trackstatus, " +
            "areatype as areatype, " +
            "schwierigkeit as schwierigkeit, " +
            "indoor as indoor, " +
            "(cast(ifnull(PictureSum.picturecount, 0) as integer)) as picturecount, " +
            "(cast(ifnull(EventSum.eventcount, 0) as integer)) as eventcount " +
            "from Track " +
            "left join PictureSum on Track.id = PictureSum.trackId " +
            "left join EventSum on Track.id = EventSum.trackRestId"
)
data class TracksGes(
    val trackname: String?,
    val approved: Int,
    val distance2location: Int,
    val openmondays: Int,
    val opentuesdays: Int,
    val openwednesday: Int,
    val openthursday: Int,
    val openfriday: Int,
    val opensaturday: Int,
    val opensunday: Int,
    val country: String,
    val trackaccess: String,
    val id: Long,
    val brands: String?,
    val metatext: String?,
    val kidstrack: Int,
    val supercross: Int,
    val shower: Int,
    val cleaning: Int,
    val electricity: Int,
    val camping: Int,
    val latitude: Double,
    val longitude: Double,
    val hoursmonday: String?,
    val hourstuesday: String?,
    val hourswednesday: String?,
    val hoursthursday: String?,
    val hoursfriday: String?,
    val hourssaturday: String?,
    val hourssunday: String?,
    val validuntil: Int,
    val url: String?,
    val phone: String?,
    val contact: String,
    val notes: String?,
    val tracklength: Int,
    val soiltype: Int,
    val facebook: String?,
    val adress: String?,
    val licence: String?,
    val fees: String?,
    val feescamping: String?,
    val daysopen: String?,
    val noiselimit: String?,
    val campingrvrvhookup: Int,
    val singletracks: Int,
    val mxtrack: Int,
    val a4x4: Int,
    val enduro: Int,
    val utv: Int,
    val quad: Int,
    val trackstatus: String?,
    val areatype: String?,
    val schwierigkeit: Int,
    val indoor: Int,
    val picturecount: Int,
    val eventcount: Int
)

