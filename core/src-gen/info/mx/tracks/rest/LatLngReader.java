package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class LatLngReader extends JsonEntityReader<LatLng> {			
	
	public LatLngReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, LatLng entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("trackname")) {
				entity.setTrackname(reader.nextString());
			}
			else if(name.equals("lat")) {
				entity.setLat(reader.nextDouble());
			}
			else if(name.equals("lng")) {
				entity.setLng(reader.nextDouble());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<LatLng> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			LatLng item = new LatLng();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
