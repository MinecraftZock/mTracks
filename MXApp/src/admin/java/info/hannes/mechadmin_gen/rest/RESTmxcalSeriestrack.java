package info.hannes.mechadmin_gen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class RESTmxcalSeriestrack {
    
        public static final String KEY_ID = "id";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_WEBID = "webid";
        public static final String KEY_WEBSERIESID = "webSeriesId";
        public static final String KEY_WEBTRACKID = "webTrackId";
        public static final String KEY_EVENTDATE = "eventDate";
        public static final String KEY_NOTES = "notes";
        public static final String KEY_QUELLFILEID = "quellfileId";

	private int id;
	private int changed;
	private int webid;
	private int webSeriesId;
	private int webTrackId;
	private int eventDate;
	private String notes;
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
	public int getWebSeriesId(){
		return webSeriesId;
	}
	public void setWebSeriesId(int value){
		this.webSeriesId = value;
	}
	public int getWebTrackId(){
		return webTrackId;
	}
	public void setWebTrackId(int value){
		this.webTrackId = value;
	}
	public int getEventDate(){
		return eventDate;
	}
	public void setEventDate(int value){
		this.eventDate = value;
	}
	public String getNotes(){
		return notes;
	}
	public void setNotes(String value){
		this.notes = value;
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
            ContentValuesUtil.putMapped(KEY_WEBSERIESID, map, values, webSeriesId);
            ContentValuesUtil.putMapped(KEY_WEBTRACKID, map, values, webTrackId);
            ContentValuesUtil.putMapped(KEY_EVENTDATE, map, values, eventDate);
            ContentValuesUtil.putMapped(KEY_NOTES, map, values, notes);
            ContentValuesUtil.putMapped(KEY_QUELLFILEID, map, values, quellfileId);

        return values;
	}
}
