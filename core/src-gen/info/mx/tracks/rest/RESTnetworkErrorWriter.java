package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class RESTnetworkErrorWriter extends JsonEntityWriter<RESTnetworkError> {

	public RESTnetworkErrorWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, RESTnetworkError entity) throws IOException {
		writer.beginObject();
		
		writer.name("androidid");
		writer.value(entity.getAndroidid());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("tracks");
		writer.value(entity.getTracks());
		writer.name("reason");
		writer.value(entity.getReason());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<RESTnetworkError> entities) throws IOException {
		writer.beginArray();
		
		for(RESTnetworkError item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
