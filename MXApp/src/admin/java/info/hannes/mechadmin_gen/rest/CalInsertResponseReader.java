package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class CalInsertResponseReader extends JsonEntityReader<CalInsertResponse> {

	public CalInsertResponseReader(JsonEntityReaderProvider provider) {
		super(provider);
	}

	public void read(JsonReader reader, CalInsertResponse entity) throws IOException {
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
			else if(name.equals("trackRestId")) {
				entity.setTrackRestId(reader.nextInt());
			}
			else if(name.equals("changed")) {
				entity.setChanged(reader.nextLong());
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

	public void readList(JsonReader reader, List<CalInsertResponse> entities) throws IOException {
		reader.beginArray();

		while(reader.hasNext()) {
			CalInsertResponse item = new CalInsertResponse();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
