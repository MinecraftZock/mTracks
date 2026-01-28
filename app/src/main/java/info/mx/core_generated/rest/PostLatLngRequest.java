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

public class PostLatLngRequest extends EntityEnclosedServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcaltrack/latlng";
	
	private final LatLng latLng;
	
	public LatLng getLatLng() {
		return latLng;
	}
	public PostLatLngRequest(LatLng latLng){
		this.latLng = latLng;
	}
	
	@Override
	public void writeBody(JsonEntityWriterProvider provider, OutputStream stream) throws IOException {
		JsonWriter writer = null;
		try {
			if(stream != null) {
				writer = new JsonWriter(new OutputStreamWriter(stream, Charset.defaultCharset()));
				
			provider.get(LatLng.class).write(writer, latLng);
				
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
