package info.mx.core_generated.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class Temp {
    
        public static final String KEY_DAY = "day";
        public static final String KEY_MIN = "min";
        public static final String KEY_MAX = "max";
        public static final String KEY_NIGHT = "night";
        public static final String KEY_EVE = "eve";
        public static final String KEY_MORN = "morn";

	private double day;
	private double min;
	private double max;
	private double night;
	private double eve;
	private double morn;
	
	public double getDay(){
		return day;
	}
	public void setDay(double value){
		this.day = value;
	}
	public double getMin(){
		return min;
	}
	public void setMin(double value){
		this.min = value;
	}
	public double getMax(){
		return max;
	}
	public void setMax(double value){
		this.max = value;
	}
	public double getNight(){
		return night;
	}
	public void setNight(double value){
		this.night = value;
	}
	public double getEve(){
		return eve;
	}
	public void setEve(double value){
		this.eve = value;
	}
	public double getMorn(){
		return morn;
	}
	public void setMorn(double value){
		this.morn = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_DAY, map, values, day);
            ContentValuesUtil.putMapped(KEY_MIN, map, values, min);
            ContentValuesUtil.putMapped(KEY_MAX, map, values, max);
            ContentValuesUtil.putMapped(KEY_NIGHT, map, values, night);
            ContentValuesUtil.putMapped(KEY_EVE, map, values, eve);
            ContentValuesUtil.putMapped(KEY_MORN, map, values, morn);

        return values;
	}
}
