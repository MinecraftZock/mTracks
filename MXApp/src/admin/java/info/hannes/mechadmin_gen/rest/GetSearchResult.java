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
import java.util.ArrayList;
import java.util.List;

public class GetSearchResult extends ServiceResult {
	private List<Serie> series;
	public List<Serie> getSeries(){
		return this.series;
	}
	
	public GetSearchResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.series = new ArrayList<Serie>();
			provider.get(Serie.class).readList(reader, this.series);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
