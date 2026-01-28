package info.mx.core_generated.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PostTrackstageIDResult extends ServiceResult {
	private InsertResponse insertResponse;
	public InsertResponse getInsertResponse(){
		return this.insertResponse;
	}
	
	public PostTrackstageIDResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.insertResponse = new InsertResponse();
			provider.get(InsertResponse.class).read(reader, this.insertResponse);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
