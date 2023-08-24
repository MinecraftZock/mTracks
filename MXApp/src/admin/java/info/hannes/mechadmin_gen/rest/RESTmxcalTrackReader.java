package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class RESTmxcalTrackReader extends JsonEntityReader<RESTmxcalTrack> {

	public RESTmxcalTrackReader(JsonEntityReaderProvider provider) {
		super(provider);
	}

	public void read(JsonReader reader, RESTmxcalTrack entity) throws IOException {
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
			else if(name.equals("webid")) {
				entity.setWebid(reader.nextInt());
			}
			else if(name.equals("address")) {
				entity.setAddress(reader.nextString());
			}
			else if(name.equals("city")) {
				entity.setCity(reader.nextString());
			}
			else if(name.equals("email")) {
				entity.setEmail(reader.nextString());
			}
			else if(name.equals("name")) {
				entity.setName(reader.nextString());
			}
			else if(name.equals("phone")) {
				entity.setPhone(reader.nextString());
			}
			else if(name.equals("stateCode")) {
				entity.setStateCode(reader.nextString());
			}
			else if(name.equals("website")) {
				entity.setWebsite(reader.nextString());
			}
			else if(name.equals("zip")) {
				entity.setZip(reader.nextInt());
			}
			else if(name.equals("quellfileId")) {
				entity.setQuellfileId(reader.nextInt());
			}
			else {
				reader.skipValue();
			}
		}

		reader.endObject();
	}

	public void readList(JsonReader reader, List<RESTmxcalTrack> entities) throws IOException {
		reader.beginArray();

		while(reader.hasNext()) {
			RESTmxcalTrack item = new RESTmxcalTrack();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
