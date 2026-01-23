package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class CalBaseResponseWriter extends JsonEntityWriter<CalBaseResponse> {

	public CalBaseResponseWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, CalBaseResponse entity) throws IOException {
		writer.beginObject();

		writer.name("id");
		writer.value(entity.getId());

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<CalBaseResponse> entities) throws IOException {
		writer.beginArray();
		
		for(CalBaseResponse item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
