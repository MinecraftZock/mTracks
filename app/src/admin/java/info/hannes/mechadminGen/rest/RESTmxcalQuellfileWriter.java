package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class RESTmxcalQuellfileWriter extends JsonEntityWriter<RESTmxcalQuellfile> {

	public RESTmxcalQuellfileWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, RESTmxcalQuellfile entity) throws IOException {
		writer.beginObject();

		writer.name("id");
		writer.value(entity.getId());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("updatedcount");
		writer.value(entity.getUpdatedcount());
		writer.name("content");
		writer.value(entity.getContent());
		writer.name("log");
		writer.value(entity.getLog());
		writer.name("url");
		writer.value(entity.getUrl());

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<RESTmxcalQuellfile> entities) throws IOException {
		writer.beginArray();
		
		for(RESTmxcalQuellfile item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
