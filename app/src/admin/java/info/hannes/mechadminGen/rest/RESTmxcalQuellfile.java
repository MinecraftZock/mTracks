package info.hannes.mechadminGen.rest;

import android.content.ContentValues;

import com.robotoworks.mechanoid.db.ContentValuesUtil;

import java.util.Map;

public class RESTmxcalQuellfile {
    
        public static final String KEY_ID = "id";
        public static final String KEY_CHANGED = "changed";
        public static final String KEY_UPDATEDCOUNT = "updatedcount";
        public static final String KEY_CONTENT = "content";
        public static final String KEY_LOG = "log";
        public static final String KEY_URL = "url";

	private int id;
	private int changed;
	private int updatedcount;
	private String content;
	private String log;
	private String url;
	
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
	public int getUpdatedcount(){
		return updatedcount;
	}
	public void setUpdatedcount(int value){
		this.updatedcount = value;
	}
	public String getContent(){
		return content;
	}
	public void setContent(String value){
		this.content = value;
	}
	public String getLog(){
		return log;
	}
	public void setLog(String value){
		this.log = value;
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String value){
		this.url = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_CHANGED, map, values, changed);
            ContentValuesUtil.putMapped(KEY_UPDATEDCOUNT, map, values, updatedcount);
            ContentValuesUtil.putMapped(KEY_CONTENT, map, values, content);
            ContentValuesUtil.putMapped(KEY_LOG, map, values, log);
            ContentValuesUtil.putMapped(KEY_URL, map, values, url);

        return values;
	}
}
