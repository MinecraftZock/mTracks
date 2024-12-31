package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.util.HashMap;

public class DefaultMxAdminWriterProvider implements JsonEntityWriterProvider {

	private HashMap<Class<?>, JsonEntityWriter<?>> mMap = new HashMap<Class<?>, JsonEntityWriter<?>>();
	
	public DefaultMxAdminWriterProvider(){
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
