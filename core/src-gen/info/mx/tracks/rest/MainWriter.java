package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class MainWriter extends JsonEntityWriter<Main> {

	public MainWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, Main entity) throws IOException {
		writer.beginObject();
		
		writer.name("temp");
		writer.value(entity.getTemp());
		writer.name("temp_min");
		writer.value(entity.getTempMin());
		writer.name("temp_max");
		writer.value(entity.getTempMax());
		writer.name("pressure");
		writer.value(entity.getPressure());
		writer.name("sea_level");
		writer.value(entity.getSeaLevel());
		writer.name("grnd_level");
		writer.value(entity.getGrndLevel());
		writer.name("humidity");
		writer.value(entity.getHumidity());
		writer.name("temp_kf");
		writer.value(entity.getTempKf());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<Main> entities) throws IOException {
		writer.beginArray();
		
		for(Main item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
