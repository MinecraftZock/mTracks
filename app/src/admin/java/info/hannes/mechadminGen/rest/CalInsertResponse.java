package info.hannes.mechadminGen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class CalInsertResponse {
    
        public static final String KEY_ID = "id";
        public static final String KEY_TRACKRESTID = "trackRestId";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_MESSAGE = "message";

	private int id;
	private int trackRestId;
	private long changed;
	private String message;
	
	public int getId(){
		return id;
	}
	public void setId(int value){
		this.id = value;
	}
	public int getTrackRestId(){
		return trackRestId;
	}
	public void setTrackRestId(int value){
		this.trackRestId = value;
	}
	public long getChanged(){
		return changed;
	}
	public void setChanged(long value){
		this.changed = value;
	}
	public String getMessage(){
		return message;
	}
	public void setMessage(String value){
		this.message = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_TRACKRESTID, map, values, trackRestId);
            ContentValuesUtil.putMapped(KEY_CHANGED, map, values, changed);
            ContentValuesUtil.putMapped(KEY_MESSAGE, map, values, message);

        return values;
	}
}
