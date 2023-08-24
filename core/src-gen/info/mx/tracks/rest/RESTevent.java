package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class RESTevent {
    
        public static final String KEY_ID = "id";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_TRACKID = "trackId";
        public static final String KEY_SERIEID = "serieId";
        public static final String KEY_COMMENT = "comment";
        public static final String KEY_APPROVED = "approved";
        public static final String KEY_EVENTDATE = "eventdate";
        public static final String KEY_ANDROIDID = "androidid";

	private int id;
	private int changed;
	private int trackId;
	private int serieId;
	private String comment;
	private int approved;
	private int eventdate;
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
	public int getSerieId(){
		return serieId;
	}
	public void setSerieId(int value){
		this.serieId = value;
	}
	public String getComment(){
		return comment;
	}
	public void setComment(String value){
		this.comment = value;
	}
	public int getApproved(){
		return approved;
	}
	public void setApproved(int value){
		this.approved = value;
	}
	public int getEventdate(){
		return eventdate;
	}
	public void setEventdate(int value){
		this.eventdate = value;
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
            ContentValuesUtil.putMapped(KEY_SERIEID, map, values, serieId);
            ContentValuesUtil.putMapped(KEY_COMMENT, map, values, comment);
            ContentValuesUtil.putMapped(KEY_APPROVED, map, values, approved);
            ContentValuesUtil.putMapped(KEY_EVENTDATE, map, values, eventdate);
            ContentValuesUtil.putMapped(KEY_ANDROIDID, map, values, androidid);

        return values;
	}
}
