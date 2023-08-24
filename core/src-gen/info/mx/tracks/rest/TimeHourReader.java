package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import java.util.ArrayList;

public class TimeHourReader extends JsonEntityReader<TimeHour> {			
	
	public TimeHourReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, TimeHour entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("dt")) {
				entity.setDt(reader.nextLong());
			}
			else if(name.equals("main")) {
				Main entityMember = new Main();
				getProvider().get(Main.class).read(reader, entityMember);
				entity.setMain(entityMember);
			}
			else if(name.equals("weather")) {
				List<Weather> entityMember = new ArrayList<Weather>();
				getProvider().get(Weather.class).readList(reader, entityMember);
				entity.setWeather(entityMember);
			}
			else if(name.equals("clouds")) {
				Clouds entityMember = new Clouds();
				getProvider().get(Clouds.class).read(reader, entityMember);
				entity.setClouds(entityMember);
			}
			else if(name.equals("wind")) {
				Wind entityMember = new Wind();
				getProvider().get(Wind.class).read(reader, entityMember);
				entity.setWind(entityMember);
			}
			else if(name.equals("t_txt")) {
				entity.setTTxt(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<TimeHour> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			TimeHour item = new TimeHour();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
