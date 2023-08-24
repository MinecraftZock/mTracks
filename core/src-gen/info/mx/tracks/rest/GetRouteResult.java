package info.mx.tracks.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetRouteResult extends ServiceResult {
	private String content;
	public String getContent(){
		return this.content;
	}
	public void setContent(String value){
		this.content = value;
	}
	
	public GetRouteResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
		GetRouteResult subject = this;
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
		
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
						
			if(name.equals("content")) {
				subject.setContent(reader.nextString());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
