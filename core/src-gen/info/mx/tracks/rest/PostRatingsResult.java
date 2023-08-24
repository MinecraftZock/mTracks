package info.mx.tracks.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PostRatingsResult extends ServiceResult {
	private BaseResponse baseResponse;
	public BaseResponse getBaseResponse(){
		return this.baseResponse;
	}
	
	public PostRatingsResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
			this.baseResponse = new BaseResponse();
			provider.get(BaseResponse.class).read(reader, this.baseResponse);
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
