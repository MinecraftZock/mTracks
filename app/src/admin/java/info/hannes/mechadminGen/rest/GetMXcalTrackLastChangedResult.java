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

public class GetMXcalTrackLastChangedResult extends ServiceResult {
	private List<RESTmxcalTrack> rESTmxcalTracks;
	public List<RESTmxcalTrack> getRESTmxcalTracks(){
		return this.rESTmxcalTracks;
	}

	public GetMXcalTrackLastChangedResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.rESTmxcalTracks = new ArrayList<RESTmxcalTrack>();
			provider.get(RESTmxcalTrack.class).readList(reader, this.rESTmxcalTracks);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
