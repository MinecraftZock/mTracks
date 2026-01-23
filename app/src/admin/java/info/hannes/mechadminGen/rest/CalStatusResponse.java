package info.hannes.mechadminGen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class CalStatusResponse {
    
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_MESSAGE = "message";

	private int changed;
	private String message;
	
	public int getChanged(){
		return changed;
	}
	public void setChanged(int value){
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
	    
            ContentValuesUtil.putMapped(KEY_CHANGED, map, values, changed);
            ContentValuesUtil.putMapped(KEY_MESSAGE, map, values, message);

        return values;
	}
}
