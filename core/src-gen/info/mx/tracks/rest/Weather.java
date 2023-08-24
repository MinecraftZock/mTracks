package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class Weather {
    
        public static final String KEY_ID = "id";
        public static final String KEY_MAIN = "main";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_ICON = "icon";

	private int id;
	private String main;
	private String description;
	private String icon;
	
	public int getId(){
		return id;
	}
	public void setId(int value){
		this.id = value;
	}
	public String getMain(){
		return main;
	}
	public void setMain(String value){
		this.main = value;
	}
	public String getDescription(){
		return description;
	}
	public void setDescription(String value){
		this.description = value;
	}
	public String getIcon(){
		return icon;
	}
	public void setIcon(String value){
		this.icon = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_MAIN, map, values, main);
            ContentValuesUtil.putMapped(KEY_DESCRIPTION, map, values, description);
            ContentValuesUtil.putMapped(KEY_ICON, map, values, icon);

        return values;
	}
}
