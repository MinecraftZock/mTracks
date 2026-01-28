package info.mx.core_generated.rest;


import android.net.Uri;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.EntityEnclosedServiceRequest;
import java.io.OutputStream;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.nio.charset.Charset;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class PostWeatherRequest extends EntityEnclosedServiceRequest {
	
	private static final String PATH = "/MXServer/rest/weather/toTrack";
	
	private final long trackId;
	private final String units;
	private final String lang;
	private final String content;
	
	public long getTrackId() {
		return trackId;
	}
	public String getUnits() {
		return units;
	}
	public String getLang() {
		return lang;
	}
	public String getContent() {
		return content;
	}
	public PostWeatherRequest(long trackId,
	String units,
	String lang,
	String content){
		this.trackId = trackId;
		this.units = units;
		this.lang = lang;
		this.content = content;
	}
	
	@Override
	public void writeBody(JsonEntityWriterProvider provider, OutputStream stream) throws IOException {
		JsonWriter writer = null;
		try {
			if(stream != null) {
				writer = new JsonWriter(new OutputStreamWriter(stream, Charset.defaultCharset()));
				
		
			PostWeatherRequest subject = this;
		
			writer.beginObject();
			
			writer.name("trackId");
			writer.value(subject.getTrackId());
			writer.name("units");
			writer.value(subject.getUnits());
			writer.name("lang");
			writer.value(subject.getLang());
			writer.name("content");
			writer.value(subject.getContent());
			
			writer.endObject();
		
				
			}
		} finally {
			Closeables.closeSilently(writer);
		}
	}

	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + PATH).buildUpon();
			
		return uriBuilder.toString();			
	}
}
