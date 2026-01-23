package info.hannes.mechadminGen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class RESTmxcalSerie {
    
        public static final String KEY_ID = "id";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_WEBID = "webid";
        public static final String KEY_NAME = "name";
        public static final String KEY_SERIESURL = "seriesUrl";
        public static final String KEY_YEAR = "year";
        public static final String KEY_QUELLFILEID = "quellfileId";

	private long id;
	private int changed;
	private int webid;
	private String name;
	private String seriesUrl;
	private long year;
	private int quellfileId;
	
	public long getId(){
		return id;
	}
	public void setId(long value){
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
	public long getYear(){
		return year;
	}
	public void setYear(long value){
		this.year = value;
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
            ContentValuesUtil.putMapped(KEY_NAME, map, values, name);
            ContentValuesUtil.putMapped(KEY_SERIESURL, map, values, seriesUrl);
            ContentValuesUtil.putMapped(KEY_YEAR, map, values, year);
            ContentValuesUtil.putMapped(KEY_QUELLFILEID, map, values, quellfileId);

        return values;
	}
}
