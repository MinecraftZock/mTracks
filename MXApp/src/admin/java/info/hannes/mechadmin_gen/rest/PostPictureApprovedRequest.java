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

public class PostPictureApprovedRequest extends EntityEnclosedServiceRequest {
	
	private static final String PATH = "/MXServer/rest/pictures/approved";
	
	private final long id;
	private final int status;
	
	public long getId() {
		return id;
	}
	public int getStatus() {
		return status;
	}
	public PostPictureApprovedRequest(long id,
	int status){
		this.id = id;
		this.status = status;
	}
	
	@Override
	public void writeBody(JsonEntityWriterProvider provider, OutputStream stream) throws IOException {
		JsonWriter writer = null;
		try {
			if(stream != null) {
				writer = new JsonWriter(new OutputStreamWriter(stream, Charset.defaultCharset()));
				
		
			PostPictureApprovedRequest subject = this;
		
			writer.beginObject();
			
			writer.name("id");
			writer.value(subject.getId());
			writer.name("status");
			writer.value(subject.getStatus());
			
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
