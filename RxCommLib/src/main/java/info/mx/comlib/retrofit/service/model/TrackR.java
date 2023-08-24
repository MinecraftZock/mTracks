package info.mx.comlib.retrofit.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrackR implements Serializable, Parcelable {

    @SerializedName("enduro")
    @Expose
    private Integer enduro = 0;
    @SerializedName("a4x4")
    @Expose
    private Integer a4x4 = 0;
    @SerializedName("approved")
    @Expose
    private Integer approved = 0;
    @SerializedName("areatype")
    @Expose
    private String areatype;
    @SerializedName("camping")
    @Expose
    private Integer camping = 0;
    @SerializedName("campingrvrvhookup")
    @Expose
    private Integer campingrvrvhookup = 0;
    @SerializedName("changed")
    @Expose
    private Integer changed = 0;
    @SerializedName("cleaning")
    @Expose
    private Integer cleaning = 0;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("daysopen")
    @Expose
    private String daysopen;
    @SerializedName("electricity")
    @Expose
    private Integer electricity = 0;
    @SerializedName("feescamping")
    @Expose
    private String feescamping;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("indoor")
    @Expose
    private Integer indoor = 0;
    @SerializedName("kidstrack")
    @Expose
    private Integer kidstrack = 0;
    @SerializedName("latitude")
    @Expose
    private Double latitude = 0d;
    @SerializedName("longitude")
    @Expose
    private Double longitude = 0d;
    @SerializedName("mxtrack")
    @Expose
    private Integer mxtrack = 0;
    @SerializedName("noiselimit")
    @Expose
    private String noiselimit;
    @SerializedName("openfriday")
    @Expose
    private Integer openfriday = 0;
    @SerializedName("openmondays")
    @Expose
    private Integer openmondays = 0;
    @SerializedName("opensaturday")
    @Expose
    private Integer opensaturday = 0;
    @SerializedName("opensunday")
    @Expose
    private Integer opensunday = 0;
    @SerializedName("openthursday")
    @Expose
    private Integer openthursday = 0;
    @SerializedName("opentuesdays")
    @Expose
    private Integer opentuesdays = 0;
    @SerializedName("openwednesday")
    @Expose
    private Integer openwednesday = 0;
    @SerializedName("schwierigkeit")
    @Expose
    private Integer schwierigkeit = 0;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("fees")
    @Expose
    private String fees;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("metatext")
    @Expose
    private String metatext;
    @SerializedName("licence")
    @Expose
    private String licence;
    @SerializedName("hoursmonday")
    @Expose
    private String hoursmonday;
    @SerializedName("hourssunday")
    @Expose
    private String hourssunday;
    @SerializedName("hoursthursday")
    @Expose
    private String hoursthursday;
    @SerializedName("hourstuesday")
    @Expose
    private String hourstuesday;
    @SerializedName("hourswednesday")
    @Expose
    private String hourswednesday;
    @SerializedName("hoursfriday")
    @Expose
    private String hoursfriday;
    @SerializedName("hourssaturday")
    @Expose
    private String hourssaturday;
    @SerializedName("tracklength")
    @Expose
    private Integer tracklength = 0;
    @SerializedName("quad")
    @Expose
    private Integer quad = 0;
    @SerializedName("logourl")
    @Expose
    private String logourl;
    @SerializedName("Adress")
    @Expose
    private String adress;
    @SerializedName("showroom")
    @Expose
    private Integer showroom = 0;
    @SerializedName("workshop")
    @Expose
    private Integer workshop = 0;
    @SerializedName("validuntil")
    @Expose
    private Integer validuntil = 0;
    @SerializedName("brands")
    @Expose
    private String brands;
    @SerializedName("shower")
    @Expose
    private Integer shower = 0;
    @SerializedName("singletracks")
    @Expose
    private Integer singletracks = 0;
    @SerializedName("soiltype")
    @Expose
    private Integer soiltype = 0;
    @SerializedName("supercross")
    @Expose
    private Integer supercross = 0;
    @SerializedName("trackaccess")
    @Expose
    private String trackaccess;
    @SerializedName("trackname")
    @Expose
    private String trackname;
    @SerializedName("trackstatus")
    @Expose
    private String trackstatus;
    @SerializedName("utv")
    @Expose
    private Integer utv = 0;
    public final static Parcelable.Creator<TrackR> CREATOR = new Creator<TrackR>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrackR createFromParcel(Parcel in) {
            TrackR instance = new TrackR();
            instance.enduro = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.a4x4 = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.approved = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.areatype = ((String) in.readValue((String.class.getClassLoader())));
            instance.camping = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.campingrvrvhookup = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.changed = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.cleaning = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.country = ((String) in.readValue((String.class.getClassLoader())));
            instance.daysopen = ((String) in.readValue((String.class.getClassLoader())));
            instance.electricity = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.feescamping = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.indoor = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.kidstrack = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mxtrack = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.noiselimit = ((String) in.readValue((String.class.getClassLoader())));
            instance.openfriday = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.openmondays = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.opensaturday = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.opensunday = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.openthursday = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.opentuesdays = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.openwednesday = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.schwierigkeit = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.notes = ((String) in.readValue((String.class.getClassLoader())));
            instance.contact = ((String) in.readValue((String.class.getClassLoader())));
            instance.phone = ((String) in.readValue((String.class.getClassLoader())));
            instance.fees = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.facebook = ((String) in.readValue((String.class.getClassLoader())));
            instance.metatext = ((String) in.readValue((String.class.getClassLoader())));
            instance.licence = ((String) in.readValue((String.class.getClassLoader())));
            instance.hoursmonday = ((String) in.readValue((String.class.getClassLoader())));
            instance.hourssunday = ((String) in.readValue((String.class.getClassLoader())));
            instance.hoursthursday = ((String) in.readValue((String.class.getClassLoader())));
            instance.hourstuesday = ((String) in.readValue((String.class.getClassLoader())));
            instance.hourswednesday = ((String) in.readValue((String.class.getClassLoader())));
            instance.hoursfriday = ((String) in.readValue((String.class.getClassLoader())));
            instance.hourssaturday = ((String) in.readValue((String.class.getClassLoader())));
            instance.tracklength = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.quad = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.logourl = ((String) in.readValue((String.class.getClassLoader())));
            instance.adress = ((String) in.readValue((String.class.getClassLoader())));
            instance.showroom = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.workshop = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.validuntil = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.brands = ((String) in.readValue((String.class.getClassLoader())));
            instance.shower = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.singletracks = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.soiltype = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.supercross = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.trackaccess = ((String) in.readValue((String.class.getClassLoader())));
            instance.trackname = ((String) in.readValue((String.class.getClassLoader())));
            instance.trackstatus = ((String) in.readValue((String.class.getClassLoader())));
            instance.utv = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public TrackR[] newArray(int size) {
            return (new TrackR[size]);
        }

    };
    private final static long serialVersionUID = -7428976964278943895L;

    public Integer getA4x4() {
        return a4x4;
    }

    public Integer getEnduro() {
        return enduro;
    }

    public void setA4x4(Integer a4x4) {
        this.a4x4 = a4x4;
    }

    public void setEnduro(Integer enduro) {
        this.enduro = enduro;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public String getAreatype() {
        return areatype;
    }

    public void setAreatype(String areatype) {
        this.areatype = areatype;
    }

    public Integer getCamping() {
        return camping;
    }

    public void setCamping(Integer camping) {
        this.camping = camping;
    }

    public Integer getCampingrvrvhookup() {
        return campingrvrvhookup;
    }

    public void setCampingrvrvhookup(Integer campingrvrvhookup) {
        this.campingrvrvhookup = campingrvrvhookup;
    }

    public Integer getChanged() {
        return changed;
    }

    public void setChanged(Integer changed) {
        this.changed = changed;
    }

    public Integer getCleaning() {
        return cleaning;
    }

    public void setCleaning(Integer cleaning) {
        this.cleaning = cleaning;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDaysopen() {
        return daysopen;
    }

    public void setDaysopen(String daysopen) {
        this.daysopen = daysopen;
    }

    public Integer getElectricity() {
        return electricity;
    }

    public void setElectricity(Integer electricity) {
        this.electricity = electricity;
    }

    public String getFeescamping() {
        return feescamping;
    }

    public void setFeescamping(String feescamping) {
        this.feescamping = feescamping;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIndoor() {
        return indoor;
    }

    public void setIndoor(Integer indoor) {
        this.indoor = indoor;
    }

    public Integer getKidstrack() {
        return kidstrack;
    }

    public void setKidstrack(Integer kidstrack) {
        this.kidstrack = kidstrack;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getMxtrack() {
        return mxtrack;
    }

    public void setMxtrack(Integer mxtrack) {
        this.mxtrack = mxtrack;
    }

    public String getNoiselimit() {
        return noiselimit;
    }

    public void setNoiselimit(String noiselimit) {
        this.noiselimit = noiselimit;
    }

    public Integer getOpenfriday() {
        return openfriday;
    }

    public void setOpenfriday(Integer openfriday) {
        this.openfriday = openfriday;
    }

    public Integer getOpenmondays() {
        return openmondays;
    }

    public void setOpenmondays(Integer openmondays) {
        this.openmondays = openmondays;
    }

    public Integer getOpensaturday() {
        return opensaturday;
    }

    public void setOpensaturday(Integer opensaturday) {
        this.opensaturday = opensaturday;
    }

    public Integer getOpensunday() {
        return opensunday;
    }

    public void setOpensunday(Integer opensunday) {
        this.opensunday = opensunday;
    }

    public Integer getOpenthursday() {
        return openthursday;
    }

    public void setOpenthursday(Integer openthursday) {
        this.openthursday = openthursday;
    }

    public Integer getOpentuesdays() {
        return opentuesdays;
    }

    public void setOpentuesdays(Integer opentuesdays) {
        this.opentuesdays = opentuesdays;
    }

    public Integer getOpenwednesday() {
        return openwednesday;
    }

    public void setOpenwednesday(Integer openwednesday) {
        this.openwednesday = openwednesday;
    }

    public Integer getSchwierigkeit() {
        return schwierigkeit;
    }

    public void setSchwierigkeit(Integer schwierigkeit) {
        this.schwierigkeit = schwierigkeit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getMetatext() {
        return metatext;
    }

    public void setMetatext(String metatext) {
        this.metatext = metatext;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getHoursmonday() {
        return hoursmonday;
    }

    public void setHoursmonday(String hoursmonday) {
        this.hoursmonday = hoursmonday;
    }

    public String getHourssunday() {
        return hourssunday;
    }

    public void setHourssunday(String hourssunday) {
        this.hourssunday = hourssunday;
    }

    public String getHoursthursday() {
        return hoursthursday;
    }

    public void setHoursthursday(String hoursthursday) {
        this.hoursthursday = hoursthursday;
    }

    public String getHourstuesday() {
        return hourstuesday;
    }

    public void setHourstuesday(String hourstuesday) {
        this.hourstuesday = hourstuesday;
    }

    public String getHourswednesday() {
        return hourswednesday;
    }

    public void setHourswednesday(String hourswednesday) {
        this.hourswednesday = hourswednesday;
    }

    public String getHoursfriday() {
        return hoursfriday;
    }

    public void setHoursfriday(String hoursfriday) {
        this.hoursfriday = hoursfriday;
    }

    public String getHourssaturday() {
        return hourssaturday;
    }

    public void setHourssaturday(String hourssaturday) {
        this.hourssaturday = hourssaturday;
    }

    public Integer getTracklength() {
        return tracklength;
    }

    public void setTracklength(Integer tracklength) {
        this.tracklength = tracklength;
    }

    public Integer getQuad() {
        return quad;
    }

    public void setQuad(Integer quad) {
        this.quad = quad;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Integer getShowroom() {
        return showroom;
    }

    public void setShowroom(Integer showroom) {
        this.showroom = showroom;
    }

    public Integer getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Integer workshop) {
        this.workshop = workshop;
    }

    public Integer getValiduntil() {
        return validuntil;
    }

    public void setValiduntil(Integer validuntil) {
        this.validuntil = validuntil;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public Integer getShower() {
        return shower;
    }

    public void setShower(Integer shower) {
        this.shower = shower;
    }

    public Integer getSingletracks() {
        return singletracks;
    }

    public void setSingletracks(Integer singletracks) {
        this.singletracks = singletracks;
    }

    public Integer getSoiltype() {
        return soiltype;
    }

    public void setSoiltype(Integer soiltype) {
        this.soiltype = soiltype;
    }

    public Integer getSupercross() {
        return supercross;
    }

    public void setSupercross(Integer supercross) {
        this.supercross = supercross;
    }

    public String getTrackaccess() {
        return trackaccess;
    }

    public void setTrackaccess(String trackaccess) {
        this.trackaccess = trackaccess;
    }

    public String getTrackname() {
        return trackname;
    }

    public void setTrackname(String trackname) {
        this.trackname = trackname;
    }

    public String getTrackstatus() {
        return trackstatus;
    }

    public void setTrackstatus(String trackstatus) {
        this.trackstatus = trackstatus;
    }

    public Integer getUtv() {
        return utv;
    }

    public void setUtv(Integer utv) {
        this.utv = utv;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(enduro);
        dest.writeValue(a4x4);
        dest.writeValue(approved);
        dest.writeValue(areatype);
        dest.writeValue(camping);
        dest.writeValue(campingrvrvhookup);
        dest.writeValue(changed);
        dest.writeValue(cleaning);
        dest.writeValue(country);
        dest.writeValue(daysopen);
        dest.writeValue(electricity);
        dest.writeValue(feescamping);
        dest.writeValue(id);
        dest.writeValue(indoor);
        dest.writeValue(kidstrack);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(mxtrack);
        dest.writeValue(noiselimit);
        dest.writeValue(openfriday);
        dest.writeValue(openmondays);
        dest.writeValue(opensaturday);
        dest.writeValue(opensunday);
        dest.writeValue(openthursday);
        dest.writeValue(opentuesdays);
        dest.writeValue(openwednesday);
        dest.writeValue(schwierigkeit);
        dest.writeValue(notes);
        dest.writeValue(contact);
        dest.writeValue(phone);
        dest.writeValue(fees);
        dest.writeValue(url);
        dest.writeValue(facebook);
        dest.writeValue(metatext);
        dest.writeValue(licence);
        dest.writeValue(hoursmonday);
        dest.writeValue(hourssunday);
        dest.writeValue(hoursthursday);
        dest.writeValue(hourstuesday);
        dest.writeValue(hourswednesday);
        dest.writeValue(hoursfriday);
        dest.writeValue(hourssaturday);
        dest.writeValue(tracklength);
        dest.writeValue(quad);
        dest.writeValue(logourl);
        dest.writeValue(adress);
        dest.writeValue(showroom);
        dest.writeValue(workshop);
        dest.writeValue(validuntil);
        dest.writeValue(brands);
        dest.writeValue(shower);
        dest.writeValue(singletracks);
        dest.writeValue(soiltype);
        dest.writeValue(supercross);
        dest.writeValue(trackaccess);
        dest.writeValue(trackname);
        dest.writeValue(trackstatus);
        dest.writeValue(utv);
    }

    public int describeContents() {
        return 0;
    }

}
