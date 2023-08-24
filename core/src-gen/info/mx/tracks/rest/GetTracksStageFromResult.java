package info.mx.tracks.rest;


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

public class GetTracksStageFromResult extends ServiceResult {
	private List<RESTtrackStage> rESTtrackStages;
	public List<RESTtrackStage> getRESTtrackStages(){
		return this.rESTtrackStages;
	}
	
	public GetTracksStageFromResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.rESTtrackStages = new ArrayList<RESTtrackStage>();
			provider.get(RESTtrackStage.class).readList(reader, this.rESTtrackStages);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
