package info.hannes.mechadmin_gen.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.Parser;
import com.robotoworks.mechanoid.net.Response;
import com.robotoworks.mechanoid.net.ServiceClient;
import com.robotoworks.mechanoid.net.ServiceException;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractMxAdmin extends ServiceClient {
	private static final String LOG_TAG = "MxAdmin";
	
	protected static final String DEFAULT_BASE_URL = "http://192.168.178.23:8080";
	
	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}
	
	@Override
	protected JsonEntityWriterProvider createWriterProvider() {
		return new DefaultMxAdminWriterProvider();
	}
	
	@Override
	protected JsonEntityReaderProvider createReaderProvider() {
		return new DefaultMxAdminReaderProvider();
	}
	
	public AbstractMxAdmin(String baseUrl, boolean debug){
		super(baseUrl, debug);
		
		setHeader("Content-Type","application/json; charset=utf-8");
	}
	
	public Response<PostPictureApprovedResult> postPictureApproved(PostPictureApprovedRequest request)
	  throws ServiceException {

		Parser<PostPictureApprovedResult> parser = new Parser<PostPictureApprovedResult>() {
			public PostPictureApprovedResult parse(InputStream inStream) throws IOException {
				return new PostPictureApprovedResult(getReaderProvider(), inStream);
			}
		};
		
		return post(request, parser);
	}
	
}
