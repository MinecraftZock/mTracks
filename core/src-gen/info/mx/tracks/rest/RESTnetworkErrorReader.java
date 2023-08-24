package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class RESTnetworkErrorReader extends JsonEntityReader<RESTnetworkError> {			
	
	public RESTnetworkErrorReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, RESTnetworkError entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("androidid")) {
				entity.setAndroidid(reader.nextString());
			}
			else if(name.equals("changed")) {
				entity.setChanged(reader.nextLong());
			}
			else if(name.equals("tracks")) {
				entity.setTracks(reader.nextInt());
			}
			else if(name.equals("reason")) {
				entity.setReason(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<RESTnetworkError> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			RESTnetworkError item = new RESTnetworkError();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
