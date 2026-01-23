package info.hannes.mechadminGen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SerieReader extends JsonEntityReader<Serie> {
	
	public SerieReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, Serie entity) throws IOException {
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
			else if(name.equals("id")) {
				entity.setId(reader.nextInt());
			}
			else if(name.equals("name")) {
				entity.setName(reader.nextString());
			}
			else if(name.equals("series_url")) {
				entity.setSeriesUrl(reader.nextString());
			}
			else if(name.equals("updated_at")) {
				entity.setUpdatedAt(reader.nextString());
			}
			else if(name.equals("year")) {
				entity.setYear(reader.nextInt());
			}
			else if(name.equals("series_tracks")) {
				List<SeriesTracks> entityMember = new ArrayList<SeriesTracks>();
				getProvider().get(SeriesTracks.class).readList(reader, entityMember);
				entity.setSeriesTracks(entityMember);
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<Serie> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			Serie item = new Serie();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
