package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class TrackWriter extends JsonEntityWriter<Track> {

	public TrackWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, Track entity) throws IOException {
		writer.beginObject();

		writer.name("address");
		writer.value(entity.getAddress());
		writer.name("city");
		writer.value(entity.getCity());
		writer.name("created_at");
		writer.value(entity.getCreatedAt());
		writer.name("email");
		writer.value(entity.getEmail());
		writer.name("id");
		writer.value(entity.getId());
		writer.name("name");
		writer.value(entity.getName());
		writer.name("phone");
		writer.value(entity.getPhone());
		writer.name("state_code");
		writer.value(entity.getStateCode());
		writer.name("updated_at");
		writer.value(entity.getUpdatedAt());
		writer.name("website");
		writer.value(entity.getWebsite());
		writer.name("zip");
		writer.value(entity.getZip());

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<Track> entities) throws IOException {
		writer.beginArray();
		
		for(Track item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
