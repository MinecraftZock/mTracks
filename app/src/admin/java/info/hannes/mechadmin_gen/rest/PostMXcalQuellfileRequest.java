package info.hannes.mechadmin_gen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.internal.util.JsonWriter;
import com.robotoworks.mechanoid.net.EntityEnclosedServiceRequest;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.util.Closeables;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class PostMXcalQuellfileRequest extends EntityEnclosedServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcalquellfile/createWithID";
	
	private final RESTmxcalQuellfile rESTmxcalQuellfile;

	public RESTmxcalQuellfile getRESTmxcalQuellfile() {
		return rESTmxcalQuellfile;
	}
	public PostMXcalQuellfileRequest(RESTmxcalQuellfile rESTmxcalQuellfile){
		this.rESTmxcalQuellfile = rESTmxcalQuellfile;
	}
	
	@Override
	public void writeBody(JsonEntityWriterProvider provider, OutputStream stream) throws IOException {
		JsonWriter writer = null;
		try {
			if(stream != null) {
				writer = new JsonWriter(new OutputStreamWriter(stream, Charset.defaultCharset()));
				
			provider.get(RESTmxcalQuellfile.class).write(writer, rESTmxcalQuellfile);
				
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
