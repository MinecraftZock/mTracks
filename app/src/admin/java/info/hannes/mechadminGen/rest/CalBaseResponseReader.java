package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class CalBaseResponseReader extends JsonEntityReader<CalBaseResponse> {

	public CalBaseResponseReader(JsonEntityReaderProvider provider) {
		super(provider);
	}

	public void read(JsonReader reader, CalBaseResponse entity) throws IOException {
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
			else {
				reader.skipValue();
			}
		}

		reader.endObject();
	}

	public void readList(JsonReader reader, List<CalBaseResponse> entities) throws IOException {
		reader.beginArray();

		while(reader.hasNext()) {
			CalBaseResponse item = new CalBaseResponse();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
