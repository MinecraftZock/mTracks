package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class SeriesTracksReader extends JsonEntityReader<SeriesTracks> {

	public SeriesTracksReader(JsonEntityReaderProvider provider) {
		super(provider);
	}

	public void read(JsonReader reader, SeriesTracks entity) throws IOException {
		reader.beginObject();

		while(reader.hasNext()) {
			String name = reader.nextName();

			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}

			if(name.equals("created_at")) {
				entity.setCreatedAt(reader.nextString());
			}
			else if(name.equals("event_date")) {
				entity.setEventDate(reader.nextString());
			}
			else if(name.equals("id")) {
				entity.setId(reader.nextInt());
			}
			else if(name.equals("notes")) {
				entity.setNotes(reader.nextString());
			}
			else if(name.equals("series_id")) {
				entity.setSeriesId(reader.nextInt());
			}
			else if(name.equals("track_id")) {
				entity.setTrackId(reader.nextInt());
			}
			else if(name.equals("updated_at")) {
				entity.setUpdatedAt(reader.nextString());
			}
			else if(name.equals("track")) {
				Track entityMember = new Track();
				getProvider().get(Track.class).read(reader, entityMember);
				entity.setTrack(entityMember);
			}
			else {
				reader.skipValue();
			}
		}

		reader.endObject();
	}

	public void readList(JsonReader reader, List<SeriesTracks> entities) throws IOException {
		reader.beginArray();

		while(reader.hasNext()) {
			SeriesTracks item = new SeriesTracks();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
