package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class CalStatusResponseWriter extends JsonEntityWriter<CalStatusResponse> {

	public CalStatusResponseWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, CalStatusResponse entity) throws IOException {
		writer.beginObject();

		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("message");
		writer.value(entity.getMessage());

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<CalStatusResponse> entities) throws IOException {
		writer.beginArray();
		
		for(CalStatusResponse item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
