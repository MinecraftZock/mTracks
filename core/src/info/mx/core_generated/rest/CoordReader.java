package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class CoordReader extends JsonEntityReader<Coord> {			
	
	public CoordReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, Coord entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("lon")) {
				entity.setLon(reader.nextDouble());
			}
			else if(name.equals("lat")) {
				entity.setLat(reader.nextDouble());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<Coord> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			Coord item = new Coord();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
