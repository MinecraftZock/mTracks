package info.mx.tracks.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;

public class PostLatLngResult extends ServiceResult {
	
	public PostLatLngResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
	Closeables.closeSilently(inStream);
	}
}
