package info.hannes.mechadminGen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.EntityEnclosedServiceRequest;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.util.Closeables;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class PostMXcalSeriestrackRequest extends EntityEnclosedServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcalseriestrack/createWithID";
	
	private final RESTmxcalSeriestrack rESTmxcalSeriestrack;

	public RESTmxcalSeriestrack getRESTmxcalSeriestrack() {
		return rESTmxcalSeriestrack;
	}
	public PostMXcalSeriestrackRequest(RESTmxcalSeriestrack rESTmxcalSeriestrack){
		this.rESTmxcalSeriestrack = rESTmxcalSeriestrack;
	}
	
	@Override
	public void writeBody(JsonEntityWriterProvider provider, OutputStream stream) throws IOException {
		JsonWriter writer = null;
		try {
			if(stream != null) {
				writer = new JsonWriter(new OutputStreamWriter(stream, Charset.defaultCharset()));
				
			provider.get(RESTmxcalSeriestrack.class).write(writer, rESTmxcalSeriestrack);
				
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
