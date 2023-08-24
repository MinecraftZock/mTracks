package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class InsertResponseWriter extends JsonEntityWriter<InsertResponse> {

	public InsertResponseWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, InsertResponse entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		writer.name("trackRestId");
		writer.value(entity.getTrackRestId());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("message");
		writer.value(entity.getMessage());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<InsertResponse> entities) throws IOException {
		writer.beginArray();
		
		for(InsertResponse item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
