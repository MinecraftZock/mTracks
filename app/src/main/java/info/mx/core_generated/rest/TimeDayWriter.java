package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class TimeDayWriter extends JsonEntityWriter<TimeDay> {

	public TimeDayWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, TimeDay entity) throws IOException {
		writer.beginObject();
		
		writer.name("dt");
		writer.value(entity.getDt());
		if(entity.getTemp() != null) {
			writer.name("temp");
			getProvider().get(Temp.class).write(writer, entity.getTemp());
		}
		writer.name("pressure");
		writer.value(entity.getPressure());
		writer.name("humidity");
		writer.value(entity.getHumidity());
		if(entity.getWeather() != null) {
			writer.name("weather");
			getProvider().get(Weather.class).writeList(writer, entity.getWeather());
		}
		writer.name("speed");
		writer.value(entity.getSpeed());
		writer.name("deg");
		writer.value(entity.getDeg());
		writer.name("clouds");
		writer.value(entity.getClouds());
		writer.name("snow");
		writer.value(entity.getSnow());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<TimeDay> entities) throws IOException {
		writer.beginArray();
		
		for(TimeDay item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
