package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.util.HashMap;

public class DefaultMxCalWriterProvider implements JsonEntityWriterProvider {

	private HashMap<Class<?>, JsonEntityWriter<?>> mMap = new HashMap<Class<?>, JsonEntityWriter<?>>();
	
	public DefaultMxCalWriterProvider(){
		registerWriters(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityWriter<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerWriters(HashMap<Class<?>, JsonEntityWriter<?>> map) {
		map.put(CalBaseResponse.class, new CalBaseResponseWriter(this));
		map.put(CalInsertResponse.class, new CalInsertResponseWriter(this));
		map.put(CalStatusResponse.class, new CalStatusResponseWriter(this));
		map.put(Serie.class, new SerieWriter(this));
		map.put(SeriesTracks.class, new SeriesTracksWriter(this));
		map.put(Track.class, new TrackWriter(this));
		map.put(RESTmxcalSerie.class, new RESTmxcalSerieWriter(this));
		map.put(RESTmxcalSeriestrack.class, new RESTmxcalSeriestrackWriter(this));
		map.put(RESTmxcalTrack.class, new RESTmxcalTrackWriter(this));
		map.put(RESTmxcalQuellfile.class, new RESTmxcalQuellfileWriter(this));
	}
}
