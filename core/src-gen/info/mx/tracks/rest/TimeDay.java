package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

import java.util.List;
public class TimeDay {
    
        public static final String KEY_DT = "dt";
        public static final String KEY_TEMP = "temp";
        public static final String KEY_PRESSURE = "pressure";
        public static final String KEY_HUMIDITY = "humidity";
        public static final String KEY_WEATHER = "weather";
        public static final String KEY_SPEED = "speed";
        public static final String KEY_DEG = "deg";
        public static final String KEY_CLOUDS = "clouds";
        public static final String KEY_SNOW = "snow";

	private long dt;
	private Temp temp;
	private double pressure;
	private int humidity;
	private List<Weather> weather;
	private double speed;
	private int deg;
	private int clouds;
	private double snow;
	
	public long getDt(){
		return dt;
	}
	public void setDt(long value){
		this.dt = value;
	}
	public Temp getTemp(){
		return temp;
	}
	public void setTemp(Temp value){
		this.temp = value;
	}
	public double getPressure(){
		return pressure;
	}
	public void setPressure(double value){
		this.pressure = value;
	}
	public int getHumidity(){
		return humidity;
	}
	public void setHumidity(int value){
		this.humidity = value;
	}
	public List<Weather> getWeather(){
		return weather;
	}
	public void setWeather(List<Weather> value){
		this.weather = value;
	}
	public double getSpeed(){
		return speed;
	}
	public void setSpeed(double value){
		this.speed = value;
	}
	public int getDeg(){
		return deg;
	}
	public void setDeg(int value){
		this.deg = value;
	}
	public int getClouds(){
		return clouds;
	}
	public void setClouds(int value){
		this.clouds = value;
	}
	public double getSnow(){
		return snow;
	}
	public void setSnow(double value){
		this.snow = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_DT, map, values, dt);
            ContentValuesUtil.putMapped(KEY_TEMP, map, values, temp);
            ContentValuesUtil.putMapped(KEY_PRESSURE, map, values, pressure);
            ContentValuesUtil.putMapped(KEY_HUMIDITY, map, values, humidity);
            ContentValuesUtil.putMapped(KEY_WEATHER, map, values, weather);
            ContentValuesUtil.putMapped(KEY_SPEED, map, values, speed);
            ContentValuesUtil.putMapped(KEY_DEG, map, values, deg);
            ContentValuesUtil.putMapped(KEY_CLOUDS, map, values, clouds);
            ContentValuesUtil.putMapped(KEY_SNOW, map, values, snow);

        return values;
	}
}
