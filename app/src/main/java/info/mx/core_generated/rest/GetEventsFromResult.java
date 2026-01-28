package info.mx.core_generated.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GetEventsFromResult extends ServiceResult {
	private List<RESTevent> rESTevents;
	public List<RESTevent> getRESTevents(){
		return this.rESTevents;
	}
	
	public GetEventsFromResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.rESTevents = new ArrayList<RESTevent>();
			provider.get(RESTevent.class).readList(reader, this.rESTevents);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
