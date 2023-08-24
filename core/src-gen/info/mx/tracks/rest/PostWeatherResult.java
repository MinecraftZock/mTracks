package info.mx.tracks.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.util.Streams;

public class PostWeatherResult extends ServiceResult {
	private String value;	
	public String getValue(){
		return this.value;
	}
	
	public PostWeatherResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		try {
			if(inStream != null) {
			String source = Streams.readAllText(inStream);
			
			this.value = String.valueOf(source);
		}
	} finally {
		Closeables.closeSilently(inStream);
	}
	}
}
