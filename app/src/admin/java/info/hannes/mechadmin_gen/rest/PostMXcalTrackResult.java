package info.hannes.mechadmin_gen.rest;


import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import com.robotoworks.mechanoid.net.ServiceResult;
import com.robotoworks.mechanoid.util.Closeables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class PostMXcalTrackResult extends ServiceResult {
	private CalBaseResponse calBaseResponse;
	public CalBaseResponse getCalBaseResponse(){
		return this.calBaseResponse;
	}

	public PostMXcalTrackResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.calBaseResponse = new CalBaseResponse();
			provider.get(CalBaseResponse.class).read(reader, this.calBaseResponse);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
