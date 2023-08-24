package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class LatLng {
    
        public static final String KEY_TRACKNAME = "trackname";
        public static final String KEY_LAT = "lat";
        public static final String KEY_LNG = "lng";

	private String trackname;
	private double lat;
	private double lng;
	
	public String getTrackname(){
		return trackname;
	}
	public void setTrackname(String value){
		this.trackname = value;
	}
	public double getLat(){
		return lat;
	}
	public void setLat(double value){
		this.lat = value;
	}
	public double getLng(){
		return lng;
	}
	public void setLng(double value){
		this.lng = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_TRACKNAME, map, values, trackname);
            ContentValuesUtil.putMapped(KEY_LAT, map, values, lat);
            ContentValuesUtil.putMapped(KEY_LNG, map, values, lng);

        return values;
	}
}
