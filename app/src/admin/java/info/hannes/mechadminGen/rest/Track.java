package info.hannes.mechadminGen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class Track {
    
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_CITY = "city";
        public static final String KEY_CREATED_AT = "created_at";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "name";
        public static final String KEY_PHONE = "phone";
        public static final String KEY_STATE_CODE = "state_code";
        public static final String KEY_UPDATED_AT = "updated_at";
        public static final String KEY_WEBSITE = "website";
        public static final String KEY_ZIP = "zip";

	private String address;
	private String city;
	private String createdAt;
	private String email;
	private int id;
	private String name;
	private String phone;
	private String stateCode;
	private String updatedAt;
	private String website;
	private int zip;
	
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
	public String getCreatedAt(){
		return createdAt;
	}
	public void setCreatedAt(String value){
		this.createdAt = value;
	}
	public String getEmail(){
		return email;
	}
	public void setEmail(String value){
		this.email = value;
	}
	public int getId(){
		return id;
	}
	public void setId(int value){
		this.id = value;
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
	public String getUpdatedAt(){
		return updatedAt;
	}
	public void setUpdatedAt(String value){
		this.updatedAt = value;
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
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ADDRESS, map, values, address);
            ContentValuesUtil.putMapped(KEY_CITY, map, values, city);
            ContentValuesUtil.putMapped(KEY_CREATED_AT, map, values, createdAt);
            ContentValuesUtil.putMapped(KEY_EMAIL, map, values, email);
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_NAME, map, values, name);
            ContentValuesUtil.putMapped(KEY_PHONE, map, values, phone);
            ContentValuesUtil.putMapped(KEY_STATE_CODE, map, values, stateCode);
            ContentValuesUtil.putMapped(KEY_UPDATED_AT, map, values, updatedAt);
            ContentValuesUtil.putMapped(KEY_WEBSITE, map, values, website);
            ContentValuesUtil.putMapped(KEY_ZIP, map, values, zip);

        return values;
	}
}
