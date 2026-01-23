package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.util.HashMap;

public class DefaultMxCalReaderProvider implements JsonEntityReaderProvider {

	private HashMap<Class<?>, JsonEntityReader<?>> mMap = new HashMap<Class<?>, JsonEntityReader<?>>();
	
	public DefaultMxCalReaderProvider(){
		registerReaders(mMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends JsonEntityReader<T>> R get(Class<T> entityType) {
		return (R) mMap.get(entityType);
	}
	
	protected void registerReaders(HashMap<Class<?>, JsonEntityReader<?>> map) {
		map.put(CalBaseResponse.class, new CalBaseResponseReader(this));
		map.put(CalInsertResponse.class, new CalInsertResponseReader(this));
		map.put(CalStatusResponse.class, new CalStatusResponseReader(this));
		map.put(Serie.class, new SerieReader(this));
		map.put(SeriesTracks.class, new SeriesTracksReader(this));
		map.put(Track.class, new TrackReader(this));
		map.put(RESTmxcalSerie.class, new RESTmxcalSerieReader(this));
		map.put(RESTmxcalSeriestrack.class, new RESTmxcalSeriestrackReader(this));
		map.put(RESTmxcalTrack.class, new RESTmxcalTrackReader(this));
		map.put(RESTmxcalQuellfile.class, new RESTmxcalQuellfileReader(this));
	}
}
