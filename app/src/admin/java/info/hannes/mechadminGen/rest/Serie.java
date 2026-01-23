package info.hannes.mechadminGen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.List;
import java.util.Map;

public class Serie {
    
        public static final String KEY_CREATED_AT = "created_at";
        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "name";
        public static final String KEY_SERIES_URL = "series_url";
        public static final String KEY_UPDATED_AT = "updated_at";
        public static final String KEY_YEAR = "year";
        public static final String KEY_SERIES_TRACKS = "series_tracks";

	private String createdAt;
	private int id;
	private String name;
	private String seriesUrl;
	private String updatedAt;
	private int year;
	private List<SeriesTracks> seriesTracks;

	public String getCreatedAt(){
		return createdAt;
	}
	public void setCreatedAt(String value){
		this.createdAt = value;
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
	public String getSeriesUrl(){
		return seriesUrl;
	}
	public void setSeriesUrl(String value){
		this.seriesUrl = value;
	}
	public String getUpdatedAt(){
		return updatedAt;
	}
	public void setUpdatedAt(String value){
		this.updatedAt = value;
	}
	public int getYear(){
		return year;
	}
	public void setYear(int value){
		this.year = value;
	}
	public List<SeriesTracks> getSeriesTracks(){
		return seriesTracks;
	}
	public void setSeriesTracks(List<SeriesTracks> value){
		this.seriesTracks = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_CREATED_AT, map, values, createdAt);
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_NAME, map, values, name);
            ContentValuesUtil.putMapped(KEY_SERIES_URL, map, values, seriesUrl);
            ContentValuesUtil.putMapped(KEY_UPDATED_AT, map, values, updatedAt);
            ContentValuesUtil.putMapped(KEY_YEAR, map, values, year);
            ContentValuesUtil.putMapped(KEY_SERIES_TRACKS, map, values, seriesTracks);

        return values;
	}
}
