package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class TimeHourWriter extends JsonEntityWriter<TimeHour> {

	public TimeHourWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, TimeHour entity) throws IOException {
		writer.beginObject();
		
		writer.name("dt");
		writer.value(entity.getDt());
		if(entity.getMain() != null) {
			writer.name("main");
			getProvider().get(Main.class).write(writer, entity.getMain());
		}
		if(entity.getWeather() != null) {
			writer.name("weather");
			getProvider().get(Weather.class).writeList(writer, entity.getWeather());
		}
		if(entity.getClouds() != null) {
			writer.name("clouds");
			getProvider().get(Clouds.class).write(writer, entity.getClouds());
		}
		if(entity.getWind() != null) {
			writer.name("wind");
			getProvider().get(Wind.class).write(writer, entity.getWind());
		}
		writer.name("t_txt");
		writer.value(entity.getTTxt());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<TimeHour> entities) throws IOException {
		writer.beginArray();
		
		for(TimeHour item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
