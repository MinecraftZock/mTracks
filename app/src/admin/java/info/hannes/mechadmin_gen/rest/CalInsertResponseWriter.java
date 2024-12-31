package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class CalInsertResponseWriter extends JsonEntityWriter<CalInsertResponse> {

	public CalInsertResponseWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, CalInsertResponse entity) throws IOException {
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
	
	public void writeList(JsonWriter writer, List<CalInsertResponse> entities) throws IOException {
		writer.beginArray();
		
		for(CalInsertResponse item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
