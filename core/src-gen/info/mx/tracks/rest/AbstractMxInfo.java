package info.mx.tracks.rest;


import com.robotoworks.mechanoid.net.Parser;
import java.io.IOException;
import com.robotoworks.mechanoid.net.Response;
import com.robotoworks.mechanoid.net.ServiceException;
import java.io.InputStream;
import com.robotoworks.mechanoid.net.ServiceClient;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

public abstract class AbstractMxInfo extends ServiceClient {
	private static final String LOG_TAG = "MxInfo";
	
	protected static final String DEFAULT_BASE_URL = "http://192.168.178.23:8080";
	
	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}
	
	@Override
	protected JsonEntityWriterProvider createWriterProvider() {
		return new DefaultMxInfoWriterProvider();
	}
	
	@Override
	protected JsonEntityReaderProvider createReaderProvider() {
		return new DefaultMxInfoReaderProvider();
	}
	
	public AbstractMxInfo(String baseUrl, boolean debug){
		super(baseUrl, debug);
		
		setHeader("Content-Type","application/json; charset=utf-8");
	}
	
	public Response<PostLatLngResult> postLatLng(PostLatLngRequest request)
	  throws ServiceException {
		
		Parser<PostLatLngResult> parser = new Parser<PostLatLngResult>() {
			public PostLatLngResult parse(InputStream inStream) throws IOException {
				return new PostLatLngResult(getReaderProvider(), inStream);
			}
		};
		
		return post(request, parser);
	}
	
	public Response<PutTrackstageResult> putTrackstage(PutTrackstageRequest request)
	  throws ServiceException {
		
		Parser<PutTrackstageResult> parser = new Parser<PutTrackstageResult>() {
			public PutTrackstageResult parse(InputStream inStream) throws IOException {
				return new PutTrackstageResult(getReaderProvider(), inStream);
			}
		};
		
		return put(request, parser);
	}
	
	public Response<PostTrackstageIDResult> postTrackstageID(PostTrackstageIDRequest request)
	  throws ServiceException {
		
		Parser<PostTrackstageIDResult> parser = new Parser<PostTrackstageIDResult>() {
			public PostTrackstageIDResult parse(InputStream inStream) throws IOException {
				return new PostTrackstageIDResult(getReaderProvider(), inStream);
			}
		};
		
		return post(request, parser);
	}
	
	public Response<DelTrackstageResult> delTrackstage(DelTrackstageRequest request)
	  throws ServiceException {
		
		Parser<DelTrackstageResult> parser = new Parser<DelTrackstageResult>() {
			public DelTrackstageResult parse(InputStream inStream) throws IOException {
				return new DelTrackstageResult(getReaderProvider(), inStream);
			}
		};
		
		return delete(request, parser);
	}
	
	public Response<GetTracksStageFromResult> getTracksStageFrom(GetTracksStageFromRequest request)
	  throws ServiceException {
		
		Parser<GetTracksStageFromResult> parser = new Parser<GetTracksStageFromResult>() {
			public GetTracksStageFromResult parse(InputStream inStream) throws IOException {
				return new GetTracksStageFromResult(getReaderProvider(), inStream);
			}
		};
		
		return get(request, parser);
	}
	
	public Response<PostEventResult> postEvent(PostEventRequest request)
	  throws ServiceException {
		
		Parser<PostEventResult> parser = new Parser<PostEventResult>() {
			public PostEventResult parse(InputStream inStream) throws IOException {
				return new PostEventResult(getReaderProvider(), inStream);
			}
		};
		
		return post(request, parser);
	}

	public Response<PostWeatherResult> postWeather(PostWeatherRequest request)
	  throws ServiceException {
		
		Parser<PostWeatherResult> parser = new Parser<PostWeatherResult>() {
			public PostWeatherResult parse(InputStream inStream) throws IOException {
				return new PostWeatherResult(getReaderProvider(), inStream);
			}
		};
		
		return post(request, parser);
	}
	
	public Response<PostNetworkErrorsResult> postNetworkErrors(PostNetworkErrorsRequest request)
	  throws ServiceException {
		
		Parser<PostNetworkErrorsResult> parser = new Parser<PostNetworkErrorsResult>() {
			public PostNetworkErrorsResult parse(InputStream inStream) throws IOException {
				return new PostNetworkErrorsResult(getReaderProvider(), inStream);
			}
		};
		
		return post(request, parser);
	}
	
}
