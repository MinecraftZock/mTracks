package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import java.util.HashMap;

public class DefaultMxInfoWriterProvider implements JsonEntityWriterProvider {

	private HashMap<Class<?>, JsonEntityWriter<?>> mMap = new HashMap<Class<?>, JsonEntityWriter<?>>();
	
	public DefaultMxInfoWriterProvider(){
		registerWriters(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityWriter<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerWriters(HashMap<Class<?>, JsonEntityWriter<?>> map) {
		map.put(RESTnetworkError.class, new RESTnetworkErrorWriter(this));
		map.put(LatLng.class, new LatLngWriter(this));
		map.put(BaseResponse.class, new BaseResponseWriter(this));
		map.put(InsertResponse.class, new InsertResponseWriter(this));
		map.put(RESTtrackStage.class, new RESTtrackStageWriter(this));
		map.put(RESTrating.class, new RESTratingWriter(this));
		map.put(RESTevent.class, new RESTeventWriter(this));
	}
}
