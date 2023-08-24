package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class RESTmxcalSeriestrackReader extends JsonEntityReader<RESTmxcalSeriestrack> {
	
	public RESTmxcalSeriestrackReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, RESTmxcalSeriestrack entity) throws IOException {
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
			else if(name.equals("webid")) {
				entity.setWebid(reader.nextInt());
			}
			else if(name.equals("webSeriesId")) {
				entity.setWebSeriesId(reader.nextInt());
			}
			else if(name.equals("webTrackId")) {
				entity.setWebTrackId(reader.nextInt());
			}
			else if(name.equals("eventDate")) {
				entity.setEventDate(reader.nextInt());
			}
			else if(name.equals("notes")) {
				entity.setNotes(reader.nextString());
			}
			else if(name.equals("quellfileId")) {
				entity.setQuellfileId(reader.nextInt());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<RESTmxcalSeriestrack> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			RESTmxcalSeriestrack item = new RESTmxcalSeriestrack();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
