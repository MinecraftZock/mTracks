package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class RESTmxcalQuellfileReader extends JsonEntityReader<RESTmxcalQuellfile> {

	public RESTmxcalQuellfileReader(JsonEntityReaderProvider provider) {
		super(provider);
	}

	public void read(JsonReader reader, RESTmxcalQuellfile entity) throws IOException {
		reader.beginObject();

		while(reader.hasNext()) {
			String name = reader.nextName();

			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}

			if(name.equals("id")) {
				entity.setId(reader.nextInt());
			}
			else if(name.equals("changed")) {
				entity.setChanged(reader.nextInt());
			}
			else if(name.equals("updatedcount")) {
				entity.setUpdatedcount(reader.nextInt());
			}
			else if(name.equals("content")) {
				entity.setContent(reader.nextString());
			}
			else if(name.equals("log")) {
				entity.setLog(reader.nextString());
			}
			else if(name.equals("url")) {
				entity.setUrl(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}

		reader.endObject();
	}

	public void readList(JsonReader reader, List<RESTmxcalQuellfile> entities) throws IOException {
		reader.beginArray();

		while(reader.hasNext()) {
			RESTmxcalQuellfile item = new RESTmxcalQuellfile();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
