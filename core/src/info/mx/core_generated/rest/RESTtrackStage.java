package info.mx.core_generated.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class RESTtrackStage {
    
        public static final String KEY_ID = "id";
        public static final String KEY_TRACKID = "trackId";
        public static final String KEY_ANDROIDID = "androidid";
        public static final String KEY_APPROVED = "approved";
        public static final String KEY_COUNTRY = "country";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_TRACKNAME = "trackname";
        public static final String KEY_LONGITUDE = "longitude";
        public static final String KEY_LATITUDE = "latitude";
        public static final String KEY_INS_LONGITUDE = "ins_longitude";
        public static final String KEY_INS_LATITUDE = "ins_latitude";
        public static final String KEY_INS_DISTANCE = "ins_distance";
        public static final String KEY_URL = "url";
        public static final String KEY_FEES = "fees";
        public static final String KEY_PHONE = "phone";
        public static final String KEY_NOTES = "notes";
        public static final String KEY_CONTACT = "contact";
        public static final String KEY_LICENCE = "licence";
        public static final String KEY_KIDSTRACK = "kidstrack";
        public static final String KEY_OPENMONDAYS = "openmondays";
        public static final String KEY_OPENTUESDAYS = "opentuesdays";
        public static final String KEY_OPENWEDNESDAY = "openwednesday";
        public static final String KEY_OPENTHURSDAY = "openthursday";
        public static final String KEY_OPENFRIDAY = "openfriday";
        public static final String KEY_OPENSATURDAY = "opensaturday";
        public static final String KEY_OPENSUNDAY = "opensunday";
        public static final String KEY_HOURSMONDAY = "hoursmonday";
        public static final String KEY_HOURSTUESDAY = "hourstuesday";
        public static final String KEY_HOURSWEDNESDAY = "hourswednesday";
        public static final String KEY_HOURSTHURSDAY = "hoursthursday";
        public static final String KEY_HOURSFRIDAY = "hoursfriday";
        public static final String KEY_HOURSSATURDAY = "hourssaturday";
        public static final String KEY_HOURSSUNDAY = "hourssunday";
        public static final String KEY_TRACKLENGTH = "tracklength";
        public static final String KEY_SOILTYPE = "soiltype";
        public static final String KEY_CAMPING = "camping";
        public static final String KEY_SHOWER = "shower";
        public static final String KEY_CLEANING = "cleaning";
        public static final String KEY_ELECTRICITY = "electricity";
        public static final String KEY_SUPERCROSS = "supercross";
        public static final String KEY_TRACKACCESS = "trackaccess";
        public static final String KEY_FACEBOOK = "facebook";
        public static final String KEY_ADRESS = "adress";
        public static final String KEY_FEESCAMPING = "feescamping";
        public static final String KEY_DAYSOPEN = "daysopen";
        public static final String KEY_NOISELIMIT = "noiselimit";
        public static final String KEY_CAMPINGRVHOOKUPS = "campingrvhookups";
        public static final String KEY_SINGLETRACK = "singletrack";
        public static final String KEY_MXTRACK = "mxtrack";
        public static final String KEY_A4X4 = "a4x4";
        public static final String KEY_ENDURO = "enduro";
        public static final String KEY_UTV = "utv";
        public static final String KEY_QUAD = "quad";
        public static final String KEY_TRACKSTATUS = "trackstatus";
        public static final String KEY_AREATYPE = "areatype";
        public static final String KEY_SCHWIERIGKEIT = "schwierigkeit";
        public static final String KEY_INDOOR = "indoor";

	private int id;
	private int trackId;
	private String androidid;
	private int approved;
	private String country;
	private long changed;
	private String trackname;
	private double longitude;
	private double latitude;
	private double insLongitude;
	private double insLatitude;
	private int insDistance;
	private String url;
	private String fees;
	private String phone;
	private String notes;
	private String contact;
	private String licence;
	private int kidstrack;
	private int openmondays;
	private int opentuesdays;
	private int openwednesday;
	private int openthursday;
	private int openfriday;
	private int opensaturday;
	private int opensunday;
	private String hoursmonday;
	private String hourstuesday;
	private String hourswednesday;
	private String hoursthursday;
	private String hoursfriday;
	private String hourssaturday;
	private String hourssunday;
	private int tracklength;
	private int soiltype;
	private int camping;
	private int shower;
	private int cleaning;
	private int electricity;
	private int supercross;
	private String trackaccess;
	private String facebook;
	private String adress;
	private String feescamping;
	private String daysopen;
	private String noiselimit;
	private int campingrvhookups;
	private int singletrack;
	private int mxtrack;
	private int a4x4;
	private int enduro;
	private int utv;
	private int quad;
	private String trackstatus;
	private String areatype;
	private int schwierigkeit;
	private int indoor;
	
	public int getId(){
		return id;
	}
	public void setId(int value){
		this.id = value;
	}
	public int getTrackId(){
		return trackId;
	}
	public void setTrackId(int value){
		this.trackId = value;
	}
	public String getAndroidid(){
		return androidid;
	}
	public void setAndroidid(String value){
		this.androidid = value;
	}
	public int getApproved(){
		return approved;
	}
	public void setApproved(int value){
		this.approved = value;
	}
	public String getCountry(){
		return country;
	}
	public void setCountry(String value){
		this.country = value;
	}
	public long getChanged(){
		return changed;
	}
	public void setChanged(long value){
		this.changed = value;
	}
	public String getTrackname(){
		return trackname;
	}
	public void setTrackname(String value){
		this.trackname = value;
	}
	public double getLongitude(){
		return longitude;
	}
	public void setLongitude(double value){
		this.longitude = value;
	}
	public double getLatitude(){
		return latitude;
	}
	public void setLatitude(double value){
		this.latitude = value;
	}
	public double getInsLongitude(){
		return insLongitude;
	}
	public void setInsLongitude(double value){
		this.insLongitude = value;
	}
	public double getInsLatitude(){
		return insLatitude;
	}
	public void setInsLatitude(double value){
		this.insLatitude = value;
	}
	public int getInsDistance(){
		return insDistance;
	}
	public void setInsDistance(int value){
		this.insDistance = value;
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String value){
		this.url = value;
	}
	public String getFees(){
		return fees;
	}
	public void setFees(String value){
		this.fees = value;
	}
	public String getPhone(){
		return phone;
	}
	public void setPhone(String value){
		this.phone = value;
	}
	public String getNotes(){
		return notes;
	}
	public void setNotes(String value){
		this.notes = value;
	}
	public String getContact(){
		return contact;
	}
	public void setContact(String value){
		this.contact = value;
	}
	public String getLicence(){
		return licence;
	}
	public void setLicence(String value){
		this.licence = value;
	}
	public int getKidstrack(){
		return kidstrack;
	}
	public void setKidstrack(int value){
		this.kidstrack = value;
	}
	public int getOpenmondays(){
		return openmondays;
	}
	public void setOpenmondays(int value){
		this.openmondays = value;
	}
	public int getOpentuesdays(){
		return opentuesdays;
	}
	public void setOpentuesdays(int value){
		this.opentuesdays = value;
	}
	public int getOpenwednesday(){
		return openwednesday;
	}
	public void setOpenwednesday(int value){
		this.openwednesday = value;
	}
	public int getOpenthursday(){
		return openthursday;
	}
	public void setOpenthursday(int value){
		this.openthursday = value;
	}
	public int getOpenfriday(){
		return openfriday;
	}
	public void setOpenfriday(int value){
		this.openfriday = value;
	}
	public int getOpensaturday(){
		return opensaturday;
	}
	public void setOpensaturday(int value){
		this.opensaturday = value;
	}
	public int getOpensunday(){
		return opensunday;
	}
	public void setOpensunday(int value){
		this.opensunday = value;
	}
	public String getHoursmonday(){
		return hoursmonday;
	}
	public void setHoursmonday(String value){
		this.hoursmonday = value;
	}
	public String getHourstuesday(){
		return hourstuesday;
	}
	public void setHourstuesday(String value){
		this.hourstuesday = value;
	}
	public String getHourswednesday(){
		return hourswednesday;
	}
	public void setHourswednesday(String value){
		this.hourswednesday = value;
	}
	public String getHoursthursday(){
		return hoursthursday;
	}
	public void setHoursthursday(String value){
		this.hoursthursday = value;
	}
	public String getHoursfriday(){
		return hoursfriday;
	}
	public void setHoursfriday(String value){
		this.hoursfriday = value;
	}
	public String getHourssaturday(){
		return hourssaturday;
	}
	public void setHourssaturday(String value){
		this.hourssaturday = value;
	}
	public String getHourssunday(){
		return hourssunday;
	}
	public void setHourssunday(String value){
		this.hourssunday = value;
	}
	public int getTracklength(){
		return tracklength;
	}
	public void setTracklength(int value){
		this.tracklength = value;
	}
	public int getSoiltype(){
		return soiltype;
	}
	public void setSoiltype(int value){
		this.soiltype = value;
	}
	public int getCamping(){
		return camping;
	}
	public void setCamping(int value){
		this.camping = value;
	}
	public int getShower(){
		return shower;
	}
	public void setShower(int value){
		this.shower = value;
	}
	public int getCleaning(){
		return cleaning;
	}
	public void setCleaning(int value){
		this.cleaning = value;
	}
	public int getElectricity(){
		return electricity;
	}
	public void setElectricity(int value){
		this.electricity = value;
	}
	public int getSupercross(){
		return supercross;
	}
	public void setSupercross(int value){
		this.supercross = value;
	}
	public String getTrackaccess(){
		return trackaccess;
	}
	public void setTrackaccess(String value){
		this.trackaccess = value;
	}
	public String getFacebook(){
		return facebook;
	}
	public void setFacebook(String value){
		this.facebook = value;
	}
	public String getAdress(){
		return adress;
	}
	public void setAdress(String value){
		this.adress = value;
	}
	public String getFeescamping(){
		return feescamping;
	}
	public void setFeescamping(String value){
		this.feescamping = value;
	}
	public String getDaysopen(){
		return daysopen;
	}
	public void setDaysopen(String value){
		this.daysopen = value;
	}
	public String getNoiselimit(){
		return noiselimit;
	}
	public void setNoiselimit(String value){
		this.noiselimit = value;
	}
	public int getCampingrvhookups(){
		return campingrvhookups;
	}
	public void setCampingrvhookups(int value){
		this.campingrvhookups = value;
	}
	public int getSingletrack(){
		return singletrack;
	}
	public void setSingletrack(int value){
		this.singletrack = value;
	}
	public int getMxtrack(){
		return mxtrack;
	}
	public void setMxtrack(int value){
		this.mxtrack = value;
	}
	public int getA4x4(){
		return a4x4;
	}
	public void setA4x4(int value){
		this.a4x4 = value;
	}
	public int getEnduro(){
		return enduro;
	}
	public void setEnduro(int value){
		this.enduro = value;
	}
	public int getUtv(){
		return utv;
	}
	public void setUtv(int value){
		this.utv = value;
	}
	public int getQuad(){
		return quad;
	}
	public void setQuad(int value){
		this.quad = value;
	}
	public String getTrackstatus(){
		return trackstatus;
	}
	public void setTrackstatus(String value){
		this.trackstatus = value;
	}
	public String getAreatype(){
		return areatype;
	}
	public void setAreatype(String value){
		this.areatype = value;
	}
	public int getSchwierigkeit(){
		return schwierigkeit;
	}
	public void setSchwierigkeit(int value){
		this.schwierigkeit = value;
	}
	public int getIndoor(){
		return indoor;
	}
	public void setIndoor(int value){
		this.indoor = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_TRACKID, map, values, trackId);
            ContentValuesUtil.putMapped(KEY_ANDROIDID, map, values, androidid);
            ContentValuesUtil.putMapped(KEY_APPROVED, map, values, approved);
            ContentValuesUtil.putMapped(KEY_COUNTRY, map, values, country);
            ContentValuesUtil.putMapped(KEY_CHANGED, map, values, changed);
            ContentValuesUtil.putMapped(KEY_TRACKNAME, map, values, trackname);
            ContentValuesUtil.putMapped(KEY_LONGITUDE, map, values, longitude);
            ContentValuesUtil.putMapped(KEY_LATITUDE, map, values, latitude);
            ContentValuesUtil.putMapped(KEY_INS_LONGITUDE, map, values, insLongitude);
            ContentValuesUtil.putMapped(KEY_INS_LATITUDE, map, values, insLatitude);
            ContentValuesUtil.putMapped(KEY_INS_DISTANCE, map, values, insDistance);
            ContentValuesUtil.putMapped(KEY_URL, map, values, url);
            ContentValuesUtil.putMapped(KEY_FEES, map, values, fees);
            ContentValuesUtil.putMapped(KEY_PHONE, map, values, phone);
            ContentValuesUtil.putMapped(KEY_NOTES, map, values, notes);
            ContentValuesUtil.putMapped(KEY_CONTACT, map, values, contact);
            ContentValuesUtil.putMapped(KEY_LICENCE, map, values, licence);
            ContentValuesUtil.putMapped(KEY_KIDSTRACK, map, values, kidstrack);
            ContentValuesUtil.putMapped(KEY_OPENMONDAYS, map, values, openmondays);
            ContentValuesUtil.putMapped(KEY_OPENTUESDAYS, map, values, opentuesdays);
            ContentValuesUtil.putMapped(KEY_OPENWEDNESDAY, map, values, openwednesday);
            ContentValuesUtil.putMapped(KEY_OPENTHURSDAY, map, values, openthursday);
            ContentValuesUtil.putMapped(KEY_OPENFRIDAY, map, values, openfriday);
            ContentValuesUtil.putMapped(KEY_OPENSATURDAY, map, values, opensaturday);
            ContentValuesUtil.putMapped(KEY_OPENSUNDAY, map, values, opensunday);
            ContentValuesUtil.putMapped(KEY_HOURSMONDAY, map, values, hoursmonday);
            ContentValuesUtil.putMapped(KEY_HOURSTUESDAY, map, values, hourstuesday);
            ContentValuesUtil.putMapped(KEY_HOURSWEDNESDAY, map, values, hourswednesday);
            ContentValuesUtil.putMapped(KEY_HOURSTHURSDAY, map, values, hoursthursday);
            ContentValuesUtil.putMapped(KEY_HOURSFRIDAY, map, values, hoursfriday);
            ContentValuesUtil.putMapped(KEY_HOURSSATURDAY, map, values, hourssaturday);
            ContentValuesUtil.putMapped(KEY_HOURSSUNDAY, map, values, hourssunday);
            ContentValuesUtil.putMapped(KEY_TRACKLENGTH, map, values, tracklength);
            ContentValuesUtil.putMapped(KEY_SOILTYPE, map, values, soiltype);
            ContentValuesUtil.putMapped(KEY_CAMPING, map, values, camping);
            ContentValuesUtil.putMapped(KEY_SHOWER, map, values, shower);
            ContentValuesUtil.putMapped(KEY_CLEANING, map, values, cleaning);
            ContentValuesUtil.putMapped(KEY_ELECTRICITY, map, values, electricity);
            ContentValuesUtil.putMapped(KEY_SUPERCROSS, map, values, supercross);
            ContentValuesUtil.putMapped(KEY_TRACKACCESS, map, values, trackaccess);
            ContentValuesUtil.putMapped(KEY_FACEBOOK, map, values, facebook);
            ContentValuesUtil.putMapped(KEY_ADRESS, map, values, adress);
            ContentValuesUtil.putMapped(KEY_FEESCAMPING, map, values, feescamping);
            ContentValuesUtil.putMapped(KEY_DAYSOPEN, map, values, daysopen);
            ContentValuesUtil.putMapped(KEY_NOISELIMIT, map, values, noiselimit);
            ContentValuesUtil.putMapped(KEY_CAMPINGRVHOOKUPS, map, values, campingrvhookups);
            ContentValuesUtil.putMapped(KEY_SINGLETRACK, map, values, singletrack);
            ContentValuesUtil.putMapped(KEY_MXTRACK, map, values, mxtrack);
            ContentValuesUtil.putMapped(KEY_A4X4, map, values, a4x4);
            ContentValuesUtil.putMapped(KEY_ENDURO, map, values, enduro);
            ContentValuesUtil.putMapped(KEY_UTV, map, values, utv);
            ContentValuesUtil.putMapped(KEY_QUAD, map, values, quad);
            ContentValuesUtil.putMapped(KEY_TRACKSTATUS, map, values, trackstatus);
            ContentValuesUtil.putMapped(KEY_AREATYPE, map, values, areatype);
            ContentValuesUtil.putMapped(KEY_SCHWIERIGKEIT, map, values, schwierigkeit);
            ContentValuesUtil.putMapped(KEY_INDOOR, map, values, indoor);

        return values;
	}
}
