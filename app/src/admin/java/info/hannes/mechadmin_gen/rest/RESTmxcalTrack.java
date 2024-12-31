package info.hannes.mechadmin_gen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class RESTmxcalTrack {
    
        public static final String KEY_ID = "id";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_WEBID = "webid";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_CITY = "city";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_NAME = "name";
        public static final String KEY_PHONE = "phone";
        public static final String KEY_STATECODE = "stateCode";
        public static final String KEY_WEBSITE = "website";
        public static final String KEY_ZIP = "zip";
        public static final String KEY_QUELLFILEID = "quellfileId";

	private int id;
	private int changed;
	private int webid;
	private String address;
	private String city;
	private String email;
	private String name;
	private String phone;
	private String stateCode;
	private String website;
	private int zip;
	private int quellfileId;
	
	public int getId(){
		return id;
	}
	public void setId(int value){
		this.id = value;
	}
	public int getChanged(){
		return changed;
	}
	public void setChanged(int value){
		this.changed = value;
	}
	public int getWebid(){
		return webid;
	}
	public void setWebid(int value){
		this.webid = value;
	}
	public String getAddress(){
		return address;
	}
	public void setAddress(String value){
		this.address = value;
	}
	public String getCity(){
		return city;
	}
	public void setCity(String value){
		this.city = value;
	}
	public String getEmail(){
		return email;
	}
	public void setEmail(String value){
		this.email = value;
	}
	public String getName(){
		return name;
	}
	public void setName(String value){
		this.name = value;
	}
	public String getPhone(){
		return phone;
	}
	public void setPhone(String value){
		this.phone = value;
	}
	public String getStateCode(){
		return stateCode;
	}
	public void setStateCode(String value){
		this.stateCode = value;
	}
	public String getWebsite(){
		return website;
	}
	public void setWebsite(String value){
		this.website = value;
	}
	public int getZip(){
		return zip;
	}
	public void setZip(int value){
		this.zip = value;
	}
	public int getQuellfileId(){
		return quellfileId;
	}
	public void setQuellfileId(int value){
		this.quellfileId = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_CHANGED, map, values, changed);
            ContentValuesUtil.putMapped(KEY_WEBID, map, values, webid);
            ContentValuesUtil.putMapped(KEY_ADDRESS, map, values, address);
            ContentValuesUtil.putMapped(KEY_CITY, map, values, city);
            ContentValuesUtil.putMapped(KEY_EMAIL, map, values, email);
            ContentValuesUtil.putMapped(KEY_NAME, map, values, name);
            ContentValuesUtil.putMapped(KEY_PHONE, map, values, phone);
            ContentValuesUtil.putMapped(KEY_STATECODE, map, values, stateCode);
            ContentValuesUtil.putMapped(KEY_WEBSITE, map, values, website);
            ContentValuesUtil.putMapped(KEY_ZIP, map, values, zip);
            ContentValuesUtil.putMapped(KEY_QUELLFILEID, map, values, quellfileId);

        return values;
	}
}
