package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import java.util.HashMap;

public class DefaultMxInfoReaderProvider implements JsonEntityReaderProvider {

	private HashMap<Class<?>, JsonEntityReader<?>> mMap = new HashMap<Class<?>, JsonEntityReader<?>>();
	
	public DefaultMxInfoReaderProvider(){
		registerReaders(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityReader<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerReaders(HashMap<Class<?>, JsonEntityReader<?>> map) {
		map.put(RESTnetworkError.class, new RESTnetworkErrorReader(this));
		map.put(LatLng.class, new LatLngReader(this));
		map.put(BaseResponse.class, new BaseResponseReader(this));
		map.put(InsertResponse.class, new InsertResponseReader(this));
		map.put(RESTtrackStage.class, new RESTtrackStageReader(this));
		map.put(RESTrating.class, new RESTratingReader(this));
		map.put(RESTevent.class, new RESTeventReader(this));
	}
}
