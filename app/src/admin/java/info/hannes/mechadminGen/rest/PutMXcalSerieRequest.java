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

public class PutMXcalSerieRequest extends EntityEnclosedServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcalserie/%s";
	
	private final long idSegment;
	
	private final RESTmxcalSerie rESTmxcalSerie;

	public RESTmxcalSerie getRESTmxcalSerie() {
		return rESTmxcalSerie;
	}
	public PutMXcalSerieRequest(long idSegment,
	RESTmxcalSerie rESTmxcalSerie){
		this.idSegment = idSegment;
		this.rESTmxcalSerie = rESTmxcalSerie;
	}
	
	@Override
	public void writeBody(JsonEntityWriterProvider provider, OutputStream stream) throws IOException {
		JsonWriter writer = null;
		try {
			if(stream != null) {
				writer = new JsonWriter(new OutputStreamWriter(stream, Charset.defaultCharset()));
				
			provider.get(RESTmxcalSerie.class).write(writer, rESTmxcalSerie);
				
			}
		} finally {
			Closeables.closeSilently(writer);
		}
	}

	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, idSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
