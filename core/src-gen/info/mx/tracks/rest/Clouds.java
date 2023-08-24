package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class Clouds {
    
        public static final String KEY_ALL = "all";

	private int all;
	
	public int getAll(){
		return all;
	}
	public void setAll(int value){
		this.all = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ALL, map, values, all);

        return values;
	}
}
