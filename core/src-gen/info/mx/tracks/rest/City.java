package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class City {
    
        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "name";
        public static final String KEY_COORD = "coord";

	private int id;
	private String name;
	private Coord coord;
	
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
	public Coord getCoord(){
		return coord;
	}
	public void setCoord(Coord value){
		this.coord = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_ID, map, values, id);
            ContentValuesUtil.putMapped(KEY_NAME, map, values, name);
            ContentValuesUtil.putMapped(KEY_COORD, map, values, coord);

        return values;
	}
}
