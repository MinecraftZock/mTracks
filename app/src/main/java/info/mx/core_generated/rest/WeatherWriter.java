package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class WeatherWriter extends JsonEntityWriter<Weather> {

	public WeatherWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, Weather entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		writer.name("main");
		writer.value(entity.getMain());
		writer.name("description");
		writer.value(entity.getDescription());
		writer.name("icon");
		writer.value(entity.getIcon());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<Weather> entities) throws IOException {
		writer.beginArray();
		
		for(Weather item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
