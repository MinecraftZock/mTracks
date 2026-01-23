package info.hannes.mechadminGen.rest;


import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import com.robotoworks.mechanoid.net.ServiceResult;
import com.robotoworks.mechanoid.util.Closeables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class GetMXcalQuellfileFromResult extends ServiceResult {
	private List<RESTmxcalQuellfile> rESTmxcalQuellfiles;
	public List<RESTmxcalQuellfile> getRESTmxcalQuellfiles(){
		return this.rESTmxcalQuellfiles;
	}

	public GetMXcalQuellfileFromResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.rESTmxcalQuellfiles = new ArrayList<RESTmxcalQuellfile>();
			provider.get(RESTmxcalQuellfile.class).readList(reader, this.rESTmxcalQuellfiles);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
