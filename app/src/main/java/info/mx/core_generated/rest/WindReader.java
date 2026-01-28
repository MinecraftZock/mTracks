package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class WindReader extends JsonEntityReader<Wind> {			
	
	public WindReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, Wind entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("speed")) {
				entity.setSpeed(reader.nextDouble());
			}
			else if(name.equals("deg")) {
				entity.setDeg(reader.nextDouble());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<Wind> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			Wind item = new Wind();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
