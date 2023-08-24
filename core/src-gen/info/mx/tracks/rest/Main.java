package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

public class Main {
    
        public static final String KEY_TEMP = "temp";
        public static final String KEY_TEMP_MIN = "temp_min";
        public static final String KEY_TEMP_MAX = "temp_max";
        public static final String KEY_PRESSURE = "pressure";
        public static final String KEY_SEA_LEVEL = "sea_level";
        public static final String KEY_GRND_LEVEL = "grnd_level";
        public static final String KEY_HUMIDITY = "humidity";
        public static final String KEY_TEMP_KF = "temp_kf";

	private double temp;
	private double tempMin;
	private double tempMax;
	private double pressure;
	private double seaLevel;
	private double grndLevel;
	private int humidity;
	private double tempKf;
	
	public double getTemp(){
		return temp;
	}
	public void setTemp(double value){
		this.temp = value;
	}
	public double getTempMin(){
		return tempMin;
	}
	public void setTempMin(double value){
		this.tempMin = value;
	}
	public double getTempMax(){
		return tempMax;
	}
	public void setTempMax(double value){
		this.tempMax = value;
	}
	public double getPressure(){
		return pressure;
	}
	public void setPressure(double value){
		this.pressure = value;
	}
	public double getSeaLevel(){
		return seaLevel;
	}
	public void setSeaLevel(double value){
		this.seaLevel = value;
	}
	public double getGrndLevel(){
		return grndLevel;
	}
	public void setGrndLevel(double value){
		this.grndLevel = value;
	}
	public int getHumidity(){
		return humidity;
	}
	public void setHumidity(int value){
		this.humidity = value;
	}
	public double getTempKf(){
		return tempKf;
	}
	public void setTempKf(double value){
		this.tempKf = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_TEMP, map, values, temp);
            ContentValuesUtil.putMapped(KEY_TEMP_MIN, map, values, tempMin);
            ContentValuesUtil.putMapped(KEY_TEMP_MAX, map, values, tempMax);
            ContentValuesUtil.putMapped(KEY_PRESSURE, map, values, pressure);
            ContentValuesUtil.putMapped(KEY_SEA_LEVEL, map, values, seaLevel);
            ContentValuesUtil.putMapped(KEY_GRND_LEVEL, map, values, grndLevel);
            ContentValuesUtil.putMapped(KEY_HUMIDITY, map, values, humidity);
            ContentValuesUtil.putMapped(KEY_TEMP_KF, map, values, tempKf);

        return values;
	}
}
