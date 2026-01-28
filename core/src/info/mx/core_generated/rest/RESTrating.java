package info.mx.core_generated.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class RESTrating {
    
        public static final String KEY_ID = "id";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_TRACKID = "trackId";
        public static final String KEY_RATING = "rating";
        public static final String KEY_USERNAME = "username";
        public static final String KEY_NOTE = "note";
        public static final String KEY_COUNTRY = "country";
        public static final String KEY_APPROVED = "approved";
        public static final String KEY_DELETED = "deleted";
        public static final String KEY_ANDROIDID = "androidid";

	private int id;
	private int changed;
	private int trackId;
	private int rating;
	private String username;
	private String note;
	private String country;
	private int approved;
	private int deleted;
	private String androidid;
	
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
	public int getTrackId(){
		return trackId;
	}
	public void setTrackId(int value){
		this.trackId = value;
	}
	public int getRating(){
		return rating;
	}
	public void setRating(int value){
		this.rating = value;
	}
	public String getUsername(){
		return username;
	}
	public void setUsername(String value){
		this.username = value;
	}
	public String getNote(){
		return note;
	}
	public void setNote(String value){
		this.note = value;
	}
	public String getCountry(){
		return country;
	}
	public void setCountry(String value){
		this.country = value;
	}
	public int getApproved(){
		return approved;
	}
	public void setApproved(int value){
		this.approved = value;
	}
	public int getDeleted(){
		return deleted;
	}
	public void setDeleted(int value){
		this.deleted = value;
	}
	public String getAndroidid(){
		return androidid;
	}
	public void setAndroidid(String value){
		this.androidid = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_CHANGED, map, values, changed);
            ContentValuesUtil.putMapped(KEY_TRACKID, map, values, trackId);
            ContentValuesUtil.putMapped(KEY_RATING, map, values, rating);
            ContentValuesUtil.putMapped(KEY_USERNAME, map, values, username);
            ContentValuesUtil.putMapped(KEY_NOTE, map, values, note);
            ContentValuesUtil.putMapped(KEY_COUNTRY, map, values, country);
            ContentValuesUtil.putMapped(KEY_APPROVED, map, values, approved);
            ContentValuesUtil.putMapped(KEY_DELETED, map, values, deleted);
            ContentValuesUtil.putMapped(KEY_ANDROIDID, map, values, androidid);

        return values;
	}
}
