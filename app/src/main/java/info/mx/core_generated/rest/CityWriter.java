package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class CityWriter extends JsonEntityWriter<City> {

	public CityWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, City entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		writer.name("name");
		writer.value(entity.getName());
		if(entity.getCoord() != null) {
			writer.name("coord");
			getProvider().get(Coord.class).write(writer, entity.getCoord());
		}
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<City> entities) throws IOException {
		writer.beginArray();
		
		for(City item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
