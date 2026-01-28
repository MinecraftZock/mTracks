package info.mx.core_generated.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;

public class DelTrackstageResult extends ServiceResult {
	
	public DelTrackstageResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
	Closeables.closeSilently(inStream);
	}
}
