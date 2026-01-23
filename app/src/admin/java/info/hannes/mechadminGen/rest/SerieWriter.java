package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class SerieWriter extends JsonEntityWriter<Serie> {

	public SerieWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, Serie entity) throws IOException {
		writer.beginObject();

		writer.name("created_at");
		writer.value(entity.getCreatedAt());
		writer.name("id");
		writer.value(entity.getId());
		writer.name("name");
		writer.value(entity.getName());
		writer.name("series_url");
		writer.value(entity.getSeriesUrl());
		writer.name("updated_at");
		writer.value(entity.getUpdatedAt());
		writer.name("year");
		writer.value(entity.getYear());
		if(entity.getSeriesTracks() != null) {
			writer.name("series_tracks");
			getProvider().get(SeriesTracks.class).writeList(writer, entity.getSeriesTracks());
		}

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<Serie> entities) throws IOException {
		writer.beginArray();
		
		for(Serie item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
