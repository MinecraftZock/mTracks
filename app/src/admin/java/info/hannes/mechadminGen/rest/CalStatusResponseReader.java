package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class CalStatusResponseReader extends JsonEntityReader<CalStatusResponse> {

	public CalStatusResponseReader(JsonEntityReaderProvider provider) {
		super(provider);
	}

	public void read(JsonReader reader, CalStatusResponse entity) throws IOException {
		reader.beginObject();

		while(reader.hasNext()) {
			String name = reader.nextName();

			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}

			if(name.equals("changed")) {
				entity.setChanged(reader.nextInt());
			}
			else if(name.equals("message")) {
				entity.setMessage(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}

		reader.endObject();
	}

	public void readList(JsonReader reader, List<CalStatusResponse> entities) throws IOException {
		reader.beginArray();

		while(reader.hasNext()) {
			CalStatusResponse item = new CalStatusResponse();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
