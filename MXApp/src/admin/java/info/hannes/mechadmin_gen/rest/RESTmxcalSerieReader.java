package info.hannes.mechadmin_gen.rest;

import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

import java.io.IOException;
import java.util.List;

public class RESTmxcalSerieReader extends JsonEntityReader<RESTmxcalSerie> {

	public RESTmxcalSerieReader(JsonEntityReaderProvider provider) {
		super(provider);
	}

	public void read(JsonReader reader, RESTmxcalSerie entity) throws IOException {
		reader.beginObject();

		while(reader.hasNext()) {
			String name = reader.nextName();

			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}

			if(name.equals("id")) {
				entity.setId(reader.nextLong());
			}
			else if(name.equals("changed")) {
				entity.setChanged(reader.nextInt());
			}
			else if(name.equals("webid")) {
				entity.setWebid(reader.nextInt());
			}
			else if(name.equals("name")) {
				entity.setName(reader.nextString());
			}
			else if(name.equals("seriesUrl")) {
				entity.setSeriesUrl(reader.nextString());
			}
			else if(name.equals("year")) {
				entity.setYear(reader.nextLong());
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

	public void readList(JsonReader reader, List<RESTmxcalSerie> entities) throws IOException {
		reader.beginArray();

		while(reader.hasNext()) {
			RESTmxcalSerie item = new RESTmxcalSerie();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
