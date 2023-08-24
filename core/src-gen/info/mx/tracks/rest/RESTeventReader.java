package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class RESTeventReader extends JsonEntityReader<RESTevent> {			
	
	public RESTeventReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, RESTevent entity) throws IOException {
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
			else if(name.equals("serieId")) {
				entity.setSerieId(reader.nextInt());
			}
			else if(name.equals("comment")) {
				entity.setComment(reader.nextString());
			}
			else if(name.equals("approved")) {
				entity.setApproved(reader.nextInt());
			}
			else if(name.equals("eventdate")) {
				entity.setEventdate(reader.nextInt());
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
	
	public void readList(JsonReader reader, List<RESTevent> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			RESTevent item = new RESTevent();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
