package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class RESTratingReader extends JsonEntityReader<RESTrating> {			
	
	public RESTratingReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, RESTrating entity) throws IOException {
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
			else if(name.equals("changed")) {
				entity.setChanged(reader.nextInt());
			}
			else if(name.equals("trackId")) {
				entity.setTrackId(reader.nextInt());
			}
			else if(name.equals("rating")) {
				entity.setRating(reader.nextInt());
			}
			else if(name.equals("username")) {
				entity.setUsername(reader.nextString());
			}
			else if(name.equals("note")) {
				entity.setNote(reader.nextString());
			}
			else if(name.equals("country")) {
				entity.setCountry(reader.nextString());
			}
			else if(name.equals("approved")) {
				entity.setApproved(reader.nextInt());
			}
			else if(name.equals("deleted")) {
				entity.setDeleted(reader.nextInt());
			}
			else if(name.equals("androidid")) {
				entity.setAndroidid(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<RESTrating> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			RESTrating item = new RESTrating();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
