package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class MainReader extends JsonEntityReader<Main> {			
	
	public MainReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, Main entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("temp")) {
				entity.setTemp(reader.nextDouble());
			}
			else if(name.equals("temp_min")) {
				entity.setTempMin(reader.nextDouble());
			}
			else if(name.equals("temp_max")) {
				entity.setTempMax(reader.nextDouble());
			}
			else if(name.equals("pressure")) {
				entity.setPressure(reader.nextDouble());
			}
			else if(name.equals("sea_level")) {
				entity.setSeaLevel(reader.nextDouble());
			}
			else if(name.equals("grnd_level")) {
				entity.setGrndLevel(reader.nextDouble());
			}
			else if(name.equals("humidity")) {
				entity.setHumidity(reader.nextInt());
			}
			else if(name.equals("temp_kf")) {
				entity.setTempKf(reader.nextDouble());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<Main> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			Main item = new Main();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
