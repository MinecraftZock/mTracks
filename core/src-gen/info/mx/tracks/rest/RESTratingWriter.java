package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class RESTratingWriter extends JsonEntityWriter<RESTrating> {

	public RESTratingWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, RESTrating entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("trackId");
		writer.value(entity.getTrackId());
		writer.name("rating");
		writer.value(entity.getRating());
		writer.name("username");
		writer.value(entity.getUsername());
		writer.name("note");
		writer.value(entity.getNote());
		writer.name("country");
		writer.value(entity.getCountry());
		writer.name("approved");
		writer.value(entity.getApproved());
		writer.name("deleted");
		writer.value(entity.getDeleted());
		writer.name("androidid");
		writer.value(entity.getAndroidid());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<RESTrating> entities) throws IOException {
		writer.beginArray();
		
		for(RESTrating item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
