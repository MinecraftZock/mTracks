package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import java.util.ArrayList;

public class TimeDayReader extends JsonEntityReader<TimeDay> {			
	
	public TimeDayReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, TimeDay entity) throws IOException {
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
			else if(name.equals("temp")) {
				Temp entityMember = new Temp();
				getProvider().get(Temp.class).read(reader, entityMember);
				entity.setTemp(entityMember);
			}
			else if(name.equals("pressure")) {
				entity.setPressure(reader.nextDouble());
			}
			else if(name.equals("humidity")) {
				entity.setHumidity(reader.nextInt());
			}
			else if(name.equals("weather")) {
				List<Weather> entityMember = new ArrayList<Weather>();
				getProvider().get(Weather.class).readList(reader, entityMember);
				entity.setWeather(entityMember);
			}
			else if(name.equals("speed")) {
				entity.setSpeed(reader.nextDouble());
			}
			else if(name.equals("deg")) {
				entity.setDeg(reader.nextInt());
			}
			else if(name.equals("clouds")) {
				entity.setClouds(reader.nextInt());
			}
			else if(name.equals("snow")) {
				entity.setSnow(reader.nextDouble());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<TimeDay> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			TimeDay item = new TimeDay();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
