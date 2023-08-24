package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class WindWriter extends JsonEntityWriter<Wind> {

	public WindWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, Wind entity) throws IOException {
		writer.beginObject();
		
		writer.name("speed");
		writer.value(entity.getSpeed());
		writer.name("deg");
		writer.value(entity.getDeg());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<Wind> entities) throws IOException {
		writer.beginArray();
		
		for(Wind item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
