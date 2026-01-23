package info.hannes.mechadminGen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class SeriesTracks {
    
        public static final String KEY_CREATED_AT = "created_at";
        public static final String KEY_EVENT_DATE = "event_date";
        public static final String KEY_ID = "id";
        public static final String KEY_NOTES = "notes";
        public static final String KEY_SERIES_ID = "series_id";
        public static final String KEY_TRACK_ID = "track_id";
        public static final String KEY_UPDATED_AT = "updated_at";
        public static final String KEY_TRACK = "track";

	private String createdAt;
	private String eventDate;
	private int id;
	private String notes;
	private int seriesId;
	private int trackId;
	private String updatedAt;
	private Track track;

	public String getCreatedAt(){
		return createdAt;
	}
	public void setCreatedAt(String value){
		this.createdAt = value;
	}
	public String getEventDate(){
		return eventDate;
	}
	public void setEventDate(String value){
		this.eventDate = value;
	}
	public int getId(){
		return id;
	}
	public void setId(int value){
		this.id = value;
	}
	public String getNotes(){
		return notes;
	}
	public void setNotes(String value){
		this.notes = value;
	}
	public int getSeriesId(){
		return seriesId;
	}
	public void setSeriesId(int value){
		this.seriesId = value;
	}
	public int getTrackId(){
		return trackId;
	}
	public void setTrackId(int value){
		this.trackId = value;
	}
	public String getUpdatedAt(){
		return updatedAt;
	}
	public void setUpdatedAt(String value){
		this.updatedAt = value;
	}
	public Track getTrack(){
		return track;
	}
	public void setTrack(Track value){
		this.track = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_CREATED_AT, map, values, createdAt);
            ContentValuesUtil.putMapped(KEY_EVENT_DATE, map, values, eventDate);
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_NOTES, map, values, notes);
            ContentValuesUtil.putMapped(KEY_SERIES_ID, map, values, seriesId);
            ContentValuesUtil.putMapped(KEY_TRACK_ID, map, values, trackId);
            ContentValuesUtil.putMapped(KEY_UPDATED_AT, map, values, updatedAt);
            ContentValuesUtil.putMapped(KEY_TRACK, map, values, track);

        return values;
	}
}
