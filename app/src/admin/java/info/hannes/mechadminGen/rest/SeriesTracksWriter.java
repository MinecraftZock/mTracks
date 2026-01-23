package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;

import java.io.IOException;
import java.util.List;


public class SeriesTracksWriter extends JsonEntityWriter<SeriesTracks> {

	public SeriesTracksWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}

	public void write(JsonWriter writer, SeriesTracks entity) throws IOException {
		writer.beginObject();

		writer.name("created_at");
		writer.value(entity.getCreatedAt());
		writer.name("event_date");
		writer.value(entity.getEventDate());
		writer.name("id");
		writer.value(entity.getId());
		writer.name("notes");
		writer.value(entity.getNotes());
		writer.name("series_id");
		writer.value(entity.getSeriesId());
		writer.name("track_id");
		writer.value(entity.getTrackId());
		writer.name("updated_at");
		writer.value(entity.getUpdatedAt());
		if(entity.getTrack() != null) {
			writer.name("track");
			getProvider().get(Track.class).write(writer, entity.getTrack());
		}

		writer.endObject();
	}

	public void writeList(JsonWriter writer, List<SeriesTracks> entities) throws IOException {
		writer.beginArray();
		
		for(SeriesTracks item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
