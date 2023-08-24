package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class CloudsWriter extends JsonEntityWriter<Clouds> {

	public CloudsWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, Clouds entity) throws IOException {
		writer.beginObject();
		
		writer.name("all");
		writer.value(entity.getAll());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<Clouds> entities) throws IOException {
		writer.beginArray();
		
		for(Clouds item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
