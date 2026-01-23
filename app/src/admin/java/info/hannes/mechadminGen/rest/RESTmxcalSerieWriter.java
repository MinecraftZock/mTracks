package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class RESTmxcalSerieWriter extends JsonEntityWriter<RESTmxcalSerie> {

	public RESTmxcalSerieWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, RESTmxcalSerie entity) throws IOException {
		writer.beginObject();

		writer.name("id");
		writer.value(entity.getId());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("webid");
		writer.value(entity.getWebid());
		writer.name("name");
		writer.value(entity.getName());
		writer.name("seriesUrl");
		writer.value(entity.getSeriesUrl());
		writer.name("year");
		writer.value(entity.getYear());
		writer.name("quellfileId");
		writer.value(entity.getQuellfileId());

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<RESTmxcalSerie> entities) throws IOException {
		writer.beginArray();
		
		for(RESTmxcalSerie item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
