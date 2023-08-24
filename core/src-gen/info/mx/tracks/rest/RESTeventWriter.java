package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class RESTeventWriter extends JsonEntityWriter<RESTevent> {

	public RESTeventWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, RESTevent entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("trackId");
		writer.value(entity.getTrackId());
		writer.name("serieId");
		writer.value(entity.getSerieId());
		writer.name("comment");
		writer.value(entity.getComment());
		writer.name("approved");
		writer.value(entity.getApproved());
		writer.name("eventdate");
		writer.value(entity.getEventdate());
		writer.name("androidid");
		writer.value(entity.getAndroidid());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<RESTevent> entities) throws IOException {
		writer.beginArray();
		
		for(RESTevent item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
