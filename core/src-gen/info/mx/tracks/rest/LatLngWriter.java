package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class LatLngWriter extends JsonEntityWriter<LatLng> {

	public LatLngWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, LatLng entity) throws IOException {
		writer.beginObject();
		
		writer.name("trackname");
		writer.value(entity.getTrackname());
		writer.name("lat");
		writer.value(entity.getLat());
		writer.name("lng");
		writer.value(entity.getLng());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<LatLng> entities) throws IOException {
		writer.beginArray();
		
		for(LatLng item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
