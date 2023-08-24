package info.mx.tracks.rest;

import android.content.ContentValues;
import com.robotoworks.mechanoid.db.ContentValuesUtil;
import java.util.Map;

import java.util.List;
public class TimeHour {
    
        public static final String KEY_DT = "dt";
        public static final String KEY_MAIN = "main";
        public static final String KEY_WEATHER = "weather";
        public static final String KEY_CLOUDS = "clouds";
        public static final String KEY_WIND = "wind";
        public static final String KEY_T_TXT = "t_txt";

	private long dt;
	private Main main;
	private List<Weather> weather;
	private Clouds clouds;
	private Wind wind;
	private String tTxt;
	
	public long getDt(){
		return dt;
	}
	public void setDt(long value){
		this.dt = value;
	}
	public Main getMain(){
		return main;
	}
	public void setMain(Main value){
		this.main = value;
	}
	public List<Weather> getWeather(){
		return weather;
	}
	public void setWeather(List<Weather> value){
		this.weather = value;
	}
	public Clouds getClouds(){
		return clouds;
	}
	public void setClouds(Clouds value){
		this.clouds = value;
	}
	public Wind getWind(){
		return wind;
	}
	public void setWind(Wind value){
		this.wind = value;
	}
	public String getTTxt(){
		return tTxt;
	}
	public void setTTxt(String value){
		this.tTxt = value;
	}
	
	public ContentValues toContentValues() {
	    return toContentValues(null);
	}
	
	public ContentValues toContentValues(Map<String, String> map) {
	    ContentValues values = new ContentValues();
	    
            ContentValuesUtil.putMapped(KEY_DT, map, values, dt);
            ContentValuesUtil.putMapped(KEY_MAIN, map, values, main);
            ContentValuesUtil.putMapped(KEY_WEATHER, map, values, weather);
            ContentValuesUtil.putMapped(KEY_CLOUDS, map, values, clouds);
            ContentValuesUtil.putMapped(KEY_WIND, map, values, wind);
            ContentValuesUtil.putMapped(KEY_T_TXT, map, values, tTxt);

        return values;
	}
}
