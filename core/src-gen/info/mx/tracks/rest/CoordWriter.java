package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class CoordWriter extends JsonEntityWriter<Coord> {

	public CoordWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, Coord entity) throws IOException {
		writer.beginObject();
		
		writer.name("lon");
		writer.value(entity.getLon());
		writer.name("lat");
		writer.value(entity.getLat());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<Coord> entities) throws IOException {
		writer.beginArray();
		
		for(Coord item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
