package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import java.util.HashMap;

public class DefaultOpenWeatherWriterProvider implements JsonEntityWriterProvider {

	private HashMap<Class<?>, JsonEntityWriter<?>> mMap = new HashMap<Class<?>, JsonEntityWriter<?>>();
	
	public DefaultOpenWeatherWriterProvider(){
		registerWriters(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityWriter<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerWriters(HashMap<Class<?>, JsonEntityWriter<?>> map) {
		map.put(City.class, new CityWriter(this));
		map.put(Coord.class, new CoordWriter(this));
		map.put(TimeDay.class, new TimeDayWriter(this));
		map.put(TimeHour.class, new TimeHourWriter(this));
		map.put(Temp.class, new TempWriter(this));
		map.put(Main.class, new MainWriter(this));
		map.put(Weather.class, new WeatherWriter(this));
		map.put(Clouds.class, new CloudsWriter(this));
		map.put(Wind.class, new WindWriter(this));
	}
}
