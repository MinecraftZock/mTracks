package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class CloudsReader extends JsonEntityReader<Clouds> {			
	
	public CloudsReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, Clouds entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("all")) {
				entity.setAll(reader.nextInt());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<Clouds> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			Clouds item = new Clouds();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
