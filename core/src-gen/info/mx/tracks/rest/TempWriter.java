package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class TempWriter extends JsonEntityWriter<Temp> {

	public TempWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, Temp entity) throws IOException {
		writer.beginObject();
		
		writer.name("day");
		writer.value(entity.getDay());
		writer.name("min");
		writer.value(entity.getMin());
		writer.name("max");
		writer.value(entity.getMax());
		writer.name("night");
		writer.value(entity.getNight());
		writer.name("eve");
		writer.value(entity.getEve());
		writer.name("morn");
		writer.value(entity.getMorn());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<Temp> entities) throws IOException {
		writer.beginArray();
		
		for(Temp item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
