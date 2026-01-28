package info.mx.core_generated.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class Wind {
    
        public static final String KEY_SPEED = "speed";
        public static final String KEY_DEG = "deg";

	private double speed;
	private double deg;
	
	public double getSpeed(){
		return speed;
	}
	public void setSpeed(double value){
		this.speed = value;
	}
	public double getDeg(){
		return deg;
	}
	public void setDeg(double value){
		this.deg = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_SPEED, map, values, speed);
            ContentValuesUtil.putMapped(KEY_DEG, map, values, deg);

        return values;
	}
}
