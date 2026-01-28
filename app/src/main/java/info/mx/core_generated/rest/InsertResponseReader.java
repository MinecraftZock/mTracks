package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class InsertResponseReader extends JsonEntityReader<InsertResponse> {			
	
	public InsertResponseReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, InsertResponse entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("id")) {
				entity.setId(reader.nextInt());
			}
			else if(name.equals("trackRestId")) {
				entity.setTrackRestId(reader.nextInt());
			}
			else if(name.equals("changed")) {
				entity.setChanged(reader.nextLong());
			}
			else if(name.equals("message")) {
				entity.setMessage(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<InsertResponse> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			InsertResponse item = new InsertResponse();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
