package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.util.HashMap;

public class DefaultMxAdminReaderProvider implements JsonEntityReaderProvider {

	private HashMap<Class<?>, JsonEntityReader<?>> mMap = new HashMap<Class<?>, JsonEntityReader<?>>();
	
	public DefaultMxAdminReaderProvider(){
		registerReaders(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityReader<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerReaders(HashMap<Class<?>, JsonEntityReader<?>> map) {
	}
}
