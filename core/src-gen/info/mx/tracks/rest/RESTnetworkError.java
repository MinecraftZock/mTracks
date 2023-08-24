package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class RESTnetworkError {
    
        public static final String KEY_ANDROIDID = "androidid";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_TRACKS = "tracks";
        public static final String KEY_REASON = "reason";

	private String androidid;
	private long changed;
	private int tracks;
	private String reason;
	
	public String getAndroidid(){
		return androidid;
	}
	public void setAndroidid(String value){
		this.androidid = value;
	}
	public long getChanged(){
		return changed;
	}
	public void setChanged(long value){
		this.changed = value;
	}
	public int getTracks(){
		return tracks;
	}
	public void setTracks(int value){
		this.tracks = value;
	}
	public String getReason(){
		return reason;
	}
	public void setReason(String value){
		this.reason = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ANDROIDID, map, values, androidid);
            ContentValuesUtil.putMapped(KEY_CHANGED, map, values, changed);
            ContentValuesUtil.putMapped(KEY_TRACKS, map, values, tracks);
            ContentValuesUtil.putMapped(KEY_REASON, map, values, reason);

        return values;
	}
}
