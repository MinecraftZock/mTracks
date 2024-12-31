package info.hannes.mechadmin_gen.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import com.robotoworks.mechanoid.net.ServiceResult;
import com.robotoworks.mechanoid.util.Closeables;

import java.io.IOException;
import java.io.InputStream;

public class PostPictureApprovedResult extends ServiceResult {
	
	public PostPictureApprovedResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
	Closeables.closeSilently(inStream);
	}
}
