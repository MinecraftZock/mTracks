package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class WeatherReader extends JsonEntityReader<Weather> {			
	
	public WeatherReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, Weather entity) throws IOException {
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
			else if(name.equals("main")) {
				entity.setMain(reader.nextString());
			}
			else if(name.equals("description")) {
				entity.setDescription(reader.nextString());
			}
			else if(name.equals("icon")) {
				entity.setIcon(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<Weather> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			Weather item = new Weather();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
