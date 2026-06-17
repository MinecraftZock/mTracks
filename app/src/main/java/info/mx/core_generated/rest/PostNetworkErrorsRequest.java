package info.mx.core_generated.rest;


import android.net.Uri;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.EntityEnclosedServiceRequest;
import java.io.OutputStream;
import java.util.List;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.nio.charset.Charset;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class PostNetworkErrorsRequest extends EntityEnclosedServiceRequest {
	
	private static final String PATH = "/MXServer/rest/network/multi";
	
	private final List<RESTnetworkError> rESTnetworkErrors;

    public PostNetworkErrorsRequest(List<RESTnetworkError> rESTnetworkErrors){
		this.rESTnetworkErrors = rESTnetworkErrors;
	}
	
	@Override
	public void writeBody(JsonEntityWriterProvider provider, OutputStream stream) throws IOException {
		JsonWriter writer = null;
		try {
			if(stream != null) {
				writer = new JsonWriter(new OutputStreamWriter(stream, Charset.defaultCharset()));
				
			provider.get(RESTnetworkError.class).writeList(writer, rESTnetworkErrors);
				
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
