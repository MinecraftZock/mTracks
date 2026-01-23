package info.hannes.retrofit.service.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Tracksarchiv : Serializable, Parcelable {

    @SerializedName("trackstatus")
    @Expose
    var trackstatus: String? = null

    @SerializedName("openwednesday")
    @Expose
    private var openwednesday: Long = 0

    @SerializedName("quad")
    @Expose
    var quad: Long = 0

    @SerializedName("areatype")
    @Expose
    var areatype: String? = null

    @SerializedName("hourssaturday")
    @Expose
    var hourssaturday: String? = null

    @SerializedName("tracklength")
    @Expose
    var tracklength: Long = 0

    @SerializedName("noiselimit")
    @Expose
    var noiselimit: String? = null

    @SerializedName("hourstuesday")
    @Expose
    var hourstuesday: String? = null

    @SerializedName("campingrvrvhookup")
    @Expose
    var campingrvrvhookup: Long = 0

    @SerializedName("logourl")
    @Expose
    var logourl: String? = null

    @SerializedName("singletracks")
    @Expose
    var singletracks: Long = 0

    @SerializedName("adress")
    @Expose
    var adress: String? = null

    @SerializedName("openfriday")
    @Expose
    private var openfriday: Long = 0

    @SerializedName("fees")
    @Expose
    var fees: String? = null

    @SerializedName("hourssunday")
    @Expose
    var hourssunday: String? = null

    @SerializedName("id")
    @Expose
    private var id: Long? = null

    @SerializedName("shower")
    @Expose
    private var shower: Long = 0

    @SerializedName("trackname")
    @Expose
    var trackname: String? = null

    @SerializedName("camping")
    @Expose
    private var camping: Long = 0

    @SerializedName("electricity")
    @Expose
    private var electricity: Long = 0

    @SerializedName("mxtrack")
    @Expose
    var mxtrack: Long = 0

    @SerializedName("indoor")
    @Expose
    private var indoor: Long = 0

    @SerializedName("opensaturday")
    @Expose
    private var opensaturday: Long = 0

    @SerializedName("metatext")
    @Expose
    var metatext: String? = null

    @SerializedName("workshop")
    @Expose
    var workshop: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null

    @SerializedName("showroom")
    @Expose
    var showroom: String? = null

    @SerializedName("trackaccess")
    @Expose
    var trackaccess: String? = null

    @SerializedName("soiltype")
    @Expose
    var soiltype: Long = 0

    @SerializedName("hoursfriday")
    @Expose
    var hoursfriday: String? = null

    @SerializedName("feescamping")
    @Expose
    var feescamping: String? = null

    @SerializedName("validuntil")
    @Expose
    var validuntil: String? = null

    @SerializedName("opensunday")
    @Expose
    private var opensunday: Long = 0

    @SerializedName("a4x4")
    @Expose
    var a4x4: Long = 0

    @SerializedName("enduro")
    @Expose
    var enduro: Long = 0

    @SerializedName("hoursthursday")
    @Expose
    var hoursthursday: String? = null

    @SerializedName("openmondays")
    @Expose
    private var openmondays: Long = 0

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("openthursday")
    @Expose
    private var openthursday: Long = 0

    @SerializedName("facebook")
    @Expose
    var facebook: String? = null

    @SerializedName("hourswednesday")
    @Expose
    var hourswednesday: String? = null

    @SerializedName("licence")
    @Expose
    var licence: String? = null

    @SerializedName("brands")
    @Expose
    var brands: String? = null

    @SerializedName("supercross")
    @Expose
    var supercross: Long = 0

    @SerializedName("approved")
    @Expose
    var approved: Long = 0

    @SerializedName("daysopen")
    @Expose
    var daysopen: String? = null

    @SerializedName("arch_Id")
    @Expose
    private var archId: Long? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("notes")
    @Expose
    var notes: String? = null

    @SerializedName("changed")
    @Expose
    private var changed: Long? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null

    @SerializedName("opentuesdays")
    @Expose
    var opentuesdays: Long = 0

    @SerializedName("hoursmonday")
    @Expose
    var hoursmonday: String? = null

    @SerializedName("contact")
    @Expose
    var contact: String? = null

    @SerializedName("utv")
    @Expose
    var utv: Long = 0

    @SerializedName("schwierigkeit")
    @Expose
    var schwierigkeit: Long = 0

    @SerializedName("arch_Date")
    @Expose
    private var archDate: Long? = null

    @SerializedName("kidstrack")
    @Expose
    var kidstrack: Long = 0

    @SerializedName("cleaning")
    @Expose
    private var cleaning: Long? = null

    @SerializedName("changeuser")
    @Expose
    var changeuser: String? = null

    protected constructor(parcel: Parcel) {
        trackstatus = parcel.readValue(String::class.java.getClassLoader()).toString()
        openwednesday = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        quad = parcel.readValue(String::class.java.getClassLoader()) as Long
        areatype = parcel.readValue(String::class.java.getClassLoader()).toString()
        hourssaturday = parcel.readValue(String::class.java.getClassLoader()).toString()
        tracklength = parcel.readValue(String::class.java.getClassLoader()) as Long
        noiselimit = parcel.readValue(String::class.java.getClassLoader()).toString()
        hourstuesday = parcel.readValue(String::class.java.getClassLoader()).toString()
        campingrvrvhookup = parcel.readValue(String::class.java.getClassLoader()) as Long
        logourl = parcel.readValue(String::class.java.getClassLoader()).toString()
        singletracks = parcel.readValue(String::class.java.getClassLoader()) as Long
        adress = parcel.readValue(String::class.java.getClassLoader()).toString()
        openfriday = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        fees = parcel.readValue(String::class.java.getClassLoader()).toString()
        hourssunday = parcel.readValue(String::class.java.getClassLoader()).toString()
        id = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        shower = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        trackname = parcel.readValue(String::class.java.getClassLoader()).toString()
        camping = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        electricity = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        mxtrack = parcel.readValue(String::class.java.getClassLoader()) as Long
        indoor = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        opensaturday = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        metatext = parcel.readValue(String::class.java.getClassLoader()).toString()
        workshop = parcel.readValue(String::class.java.getClassLoader()).toString()
        latitude = parcel.readValue(Double::class.java.getClassLoader()) as Double
        showroom = parcel.readValue(String::class.java.getClassLoader()).toString()
        trackaccess = parcel.readValue(String::class.java.getClassLoader()).toString()
        soiltype = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        hoursfriday = parcel.readValue(String::class.java.getClassLoader()).toString()
        feescamping = parcel.readValue(String::class.java.getClassLoader()).toString()
        validuntil = parcel.readValue(String::class.java.getClassLoader()).toString()
        opensunday = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        a4x4 = parcel.readValue(String::class.java.getClassLoader()) as Long
        enduro = parcel.readValue(String::class.java.getClassLoader()) as Long
        hoursthursday = parcel.readValue(String::class.java.getClassLoader()).toString()
        openmondays = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        phone = parcel.readValue(String::class.java.getClassLoader()).toString()
        openthursday = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        facebook = parcel.readValue(String::class.java.getClassLoader()).toString()
        hourswednesday = parcel.readValue(String::class.java.getClassLoader()).toString()
        licence = parcel.readValue(String::class.java.getClassLoader()).toString()
        brands = parcel.readValue(String::class.java.getClassLoader()).toString()
        supercross = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        approved = parcel.readValue(String::class.java.getClassLoader()) as Long
        daysopen = parcel.readValue(String::class.java.getClassLoader()).toString()
        archId = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        url = parcel.readValue(String::class.java.getClassLoader()).toString()
        country = parcel.readValue(String::class.java.getClassLoader()).toString()
        notes = parcel.readValue(String::class.java.getClassLoader()).toString()
        changed = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        longitude = parcel.readValue(Double::class.java.getClassLoader()) as Double
        opentuesdays = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        hoursmonday = parcel.readValue(String::class.java.getClassLoader()).toString()
        contact = parcel.readValue(String::class.java.getClassLoader()).toString()
        utv = parcel.readValue(String::class.java.getClassLoader()) as Long
        schwierigkeit = parcel.readValue(String::class.java.getClassLoader()) as Long
        archDate = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        kidstrack = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        cleaning = parcel.readValue(Integer::class.java.getClassLoader()) as Long
        changeuser = parcel.readValue(String::class.java.getClassLoader()).toString()
    }

    /**
     * No args constructor for use in serialization
     */
    constructor() {}

    fun getOpenwednesday(): Long {
        return openwednesday
    }

    fun setOpenwednesday(openwednesday: Long) {
        this.openwednesday = openwednesday
    }

    fun getOpenfriday(): Long {
        return openfriday
    }

    fun setOpenfriday(openfriday: Long) {
        this.openfriday = openfriday
    }

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?) {
        this.id = id
    }

    fun getShower(): Long {
        return shower
    }

    fun setShower(shower: Long) {
        this.shower = shower
    }

    fun getCamping(): Long {
        return camping
    }

    fun setCamping(camping: Long) {
        this.camping = camping
    }

    fun getElectricity(): Long {
        return electricity
    }

    fun setElectricity(electricity: Long) {
        this.electricity = electricity
    }

    fun getIndoor(): Long {
        return indoor
    }

    fun setIndoor(indoor: Long) {
        this.indoor = indoor
    }

    fun getOpensaturday(): Long {
        return opensaturday
    }

    fun setOpensaturday(opensaturday: Long) {
        this.opensaturday = opensaturday
    }

    fun getOpensunday(): Long {
        return opensunday
    }

    fun setOpensunday(opensunday: Long) {
        this.opensunday = opensunday
    }

    fun getOpenmondays(): Long {
        return openmondays
    }

    fun setOpenmondays(openmondays: Long) {
        this.openmondays = openmondays
    }

    fun getOpenthursday(): Long {
        return openthursday
    }

    fun setOpenthursday(openthursday: Long) {
        this.openthursday = openthursday
    }

    fun getArchId(): Long? {
        return archId
    }

    fun setArchId(archId: Long?) {
        this.archId = archId
    }

    fun getChanged(): Long? {
        return changed
    }

    fun setChanged(changed: Long?) {
        this.changed = changed
    }

    fun getArchDate(): Long? {
        return archDate
    }

    fun setArchDate(archDate: Long?) {
        this.archDate = archDate
    }

    fun getCleaning(): Long? {
        return cleaning
    }

    fun setCleaning(cleaning: Long?) {
        this.cleaning = cleaning
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(trackstatus)
        dest.writeValue(openwednesday)
        dest.writeValue(quad)
        dest.writeValue(areatype)
        dest.writeValue(hourssaturday)
        dest.writeValue(tracklength)
        dest.writeValue(noiselimit)
        dest.writeValue(hourstuesday)
        dest.writeValue(campingrvrvhookup)
        dest.writeValue(logourl)
        dest.writeValue(singletracks)
        dest.writeValue(adress)
        dest.writeValue(openfriday)
        dest.writeValue(fees)
        dest.writeValue(hourssunday)
        dest.writeValue(id)
        dest.writeValue(shower)
        dest.writeValue(trackname)
        dest.writeValue(camping)
        dest.writeValue(electricity)
        dest.writeValue(mxtrack)
        dest.writeValue(indoor)
        dest.writeValue(opensaturday)
        dest.writeValue(metatext)
        dest.writeValue(workshop)
        dest.writeValue(latitude)
        dest.writeValue(showroom)
        dest.writeValue(trackaccess)
        dest.writeValue(soiltype)
        dest.writeValue(hoursfriday)
        dest.writeValue(feescamping)
        dest.writeValue(validuntil)
        dest.writeValue(opensunday)
        dest.writeValue(a4x4)
        dest.writeValue(enduro)
        dest.writeValue(hoursthursday)
        dest.writeValue(openmondays)
        dest.writeValue(phone)
        dest.writeValue(openthursday)
        dest.writeValue(facebook)
        dest.writeValue(hourswednesday)
        dest.writeValue(licence)
        dest.writeValue(brands)
        dest.writeValue(supercross)
        dest.writeValue(approved)
        dest.writeValue(daysopen)
        dest.writeValue(archId)
        dest.writeValue(url)
        dest.writeValue(country)
        dest.writeValue(notes)
        dest.writeValue(changed)
        dest.writeValue(longitude)
        dest.writeValue(opentuesdays)
        dest.writeValue(hoursmonday)
        dest.writeValue(contact)
        dest.writeValue(utv)
        dest.writeValue(schwierigkeit)
        dest.writeValue(archDate)
        dest.writeValue(kidstrack)
        dest.writeValue(cleaning)
        dest.writeValue(changeuser)
    }

    override fun describeContents(): Int {
        return 0
    }

//    companion object {
//        val CREATOR: Parcelable.Creator<Tracksarchiv> = object : Creator<Tracksarchiv?>() {
//            @SuppressWarnings(["unchecked"])
//            fun createFromParcel(`in`: Parcel): Tracksarchiv {
//                return Tracksarchiv(`in`)
//            }
//
//            fun newArray(size: Int): Array<Tracksarchiv?> {
//                return arrayOfNulls(size)
//            }
//        }
//        private const val serialVersionUID = -2459797634750889882L
//    }

    companion object CREATOR : Parcelable.Creator<Tracksarchiv> {
        override fun createFromParcel(parcel: Parcel): Tracksarchiv {
            return Tracksarchiv(parcel)
        }

        override fun newArray(size: Int): Array<Tracksarchiv?> {
            return arrayOfNulls(size)
        }
    }
}
