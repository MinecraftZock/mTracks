package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class RESTmxcalSeriestrackWriter extends JsonEntityWriter<RESTmxcalSeriestrack> {

	public RESTmxcalSeriestrackWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, RESTmxcalSeriestrack entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("webid");
		writer.value(entity.getWebid());
		writer.name("webSeriesId");
		writer.value(entity.getWebSeriesId());
		writer.name("webTrackId");
		writer.value(entity.getWebTrackId());
		writer.name("eventDate");
		writer.value(entity.getEventDate());
		writer.name("notes");
		writer.value(entity.getNotes());
		writer.name("quellfileId");
		writer.value(entity.getQuellfileId());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<RESTmxcalSeriestrack> entities) throws IOException {
		writer.beginArray();
		
		for(RESTmxcalSeriestrack item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
