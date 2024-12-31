package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class RESTmxcalTrackWriter extends JsonEntityWriter<RESTmxcalTrack> {

	public RESTmxcalTrackWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, RESTmxcalTrack entity) throws IOException {
		writer.beginObject();

		writer.name("id");
		writer.value(entity.getId());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("webid");
		writer.value(entity.getWebid());
		writer.name("address");
		writer.value(entity.getAddress());
		writer.name("city");
		writer.value(entity.getCity());
		writer.name("email");
		writer.value(entity.getEmail());
		writer.name("name");
		writer.value(entity.getName());
		writer.name("phone");
		writer.value(entity.getPhone());
		writer.name("stateCode");
		writer.value(entity.getStateCode());
		writer.name("website");
		writer.value(entity.getWebsite());
		writer.name("zip");
		writer.value(entity.getZip());
		writer.name("quellfileId");
		writer.value(entity.getQuellfileId());

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<RESTmxcalTrack> entities) throws IOException {
		writer.beginArray();
		
		for(RESTmxcalTrack item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
