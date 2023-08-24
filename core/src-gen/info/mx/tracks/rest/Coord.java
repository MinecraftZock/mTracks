package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class Coord {
    
        public static final String KEY_LON = "lon";
        public static final String KEY_LAT = "lat";

	private double lon;
	private double lat;
	
	public double getLon(){
		return lon;
	}
	public void setLon(double value){
		this.lon = value;
	}
	public double getLat(){
		return lat;
	}
	public void setLat(double value){
		this.lat = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_LON, map, values, lon);
            ContentValuesUtil.putMapped(KEY_LAT, map, values, lat);

        return values;
	}
}
