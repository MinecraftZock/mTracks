package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import java.util.HashMap;

public class DefaultGoogleWriterProvider implements JsonEntityWriterProvider {

	private HashMap<Class<?>, JsonEntityWriter<?>> mMap = new HashMap<Class<?>, JsonEntityWriter<?>>();
	
	public DefaultGoogleWriterProvider(){
		registerWriters(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityWriter<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerWriters(HashMap<Class<?>, JsonEntityWriter<?>> map) {
	}
}
