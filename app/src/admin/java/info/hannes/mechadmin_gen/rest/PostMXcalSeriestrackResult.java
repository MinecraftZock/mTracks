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

public class PostMXcalSeriestrackResult extends ServiceResult {
	private CalInsertResponse calInsertResponse;
	public CalInsertResponse getCalInsertResponse(){
		return this.calInsertResponse;
	}

	public PostMXcalSeriestrackResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.calInsertResponse = new CalInsertResponse();
			provider.get(CalInsertResponse.class).read(reader, this.calInsertResponse);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
