package info.mx.core_generated.rest;


import com.robotoworks.mechanoid.net.Parser;
import java.io.IOException;
import com.robotoworks.mechanoid.net.Response;
import com.robotoworks.mechanoid.net.ServiceException;
import java.io.InputStream;
import com.robotoworks.mechanoid.net.ServiceClient;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

public abstract class AbstractGoogle extends ServiceClient {
	private static final String LOG_TAG = "Google";
	
	protected static final String DEFAULT_BASE_URL = "https://maps.googleapis.com";
	
	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}
	
	@Override
	protected JsonEntityWriterProvider createWriterProvider() {
		return new DefaultGoogleWriterProvider();
	}
	
	@Override
	protected JsonEntityReaderProvider createReaderProvider() {
		return new DefaultGoogleReaderProvider();
	}
	
	public AbstractGoogle(String baseUrl, boolean debug){
		super(baseUrl, debug);
		
	}
	
	public Response<GetRouteResult> getRoute()
	  throws ServiceException {
	  	return getRoute(new GetRouteRequest());
	}
	
	public Response<GetRouteResult> getRoute(GetRouteRequest request)
	  throws ServiceException {
		
		Parser<GetRouteResult> parser = new Parser<GetRouteResult>() {
			public GetRouteResult parse(InputStream inStream) throws IOException {
				return new GetRouteResult(getReaderProvider(), inStream);
			}
		};
		
		return get(request, parser);
	}
	
}
