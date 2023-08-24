package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class TempReader extends JsonEntityReader<Temp> {			
	
	public TempReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, Temp entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("day")) {
				entity.setDay(reader.nextDouble());
			}
			else if(name.equals("min")) {
				entity.setMin(reader.nextDouble());
			}
			else if(name.equals("max")) {
				entity.setMax(reader.nextDouble());
			}
			else if(name.equals("night")) {
				entity.setNight(reader.nextDouble());
			}
			else if(name.equals("eve")) {
				entity.setEve(reader.nextDouble());
			}
			else if(name.equals("morn")) {
				entity.setMorn(reader.nextDouble());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<Temp> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			Temp item = new Temp();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
