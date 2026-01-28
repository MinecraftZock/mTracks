package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import java.util.HashMap;

public class DefaultOpenWeatherReaderProvider implements JsonEntityReaderProvider {

	private HashMap<Class<?>, JsonEntityReader<?>> mMap = new HashMap<Class<?>, JsonEntityReader<?>>();
	
	public DefaultOpenWeatherReaderProvider(){
		registerReaders(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityReader<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerReaders(HashMap<Class<?>, JsonEntityReader<?>> map) {
		map.put(City.class, new CityReader(this));
		map.put(Coord.class, new CoordReader(this));
		map.put(TimeDay.class, new TimeDayReader(this));
		map.put(TimeHour.class, new TimeHourReader(this));
		map.put(Temp.class, new TempReader(this));
		map.put(Main.class, new MainReader(this));
		map.put(Weather.class, new WeatherReader(this));
		map.put(Clouds.class, new CloudsReader(this));
		map.put(Wind.class, new WindReader(this));
	}
}
