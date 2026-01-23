package info.hannes.mechadminGen.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.Parser;
import com.robotoworks.mechanoid.net.Response;
import com.robotoworks.mechanoid.net.ServiceClient;
import com.robotoworks.mechanoid.net.ServiceException;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractMxCal extends ServiceClient {
	private static final String LOG_TAG = "MxCal";
	
	protected static final String DEFAULT_BASE_URL = "http://mxcal.com";
	
	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}
	
	@Override
	protected JsonEntityWriterProvider createWriterProvider() {
		return new DefaultMxCalWriterProvider();
	}
	
	@Override
	protected JsonEntityReaderProvider createReaderProvider() {
		return new DefaultMxCalReaderProvider();
	}
	
	public AbstractMxCal(String baseUrl, boolean debug){
		super(baseUrl, debug);
		
	}
	
	public Response<GetSearchResult> getSearch()
	  throws ServiceException {
	  	return getSearch(new GetSearchRequest());
	}

	public Response<GetSearchResult> getSearch(GetSearchRequest request)
	  throws ServiceException {

		Parser<GetSearchResult> parser = new Parser<GetSearchResult>() {
			public GetSearchResult parse(InputStream inStream) throws IOException {
				return new GetSearchResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetSeriesResult> getSeries(GetSeriesRequest request)
	  throws ServiceException {

		Parser<GetSeriesResult> parser = new Parser<GetSeriesResult>() {
			public GetSeriesResult parse(InputStream inStream) throws IOException {
				return new GetSeriesResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalSerieLastIdResult> getMXcalSerieLastId()
	  throws ServiceException {
	  	return getMXcalSerieLastId(new GetMXcalSerieLastIdRequest());
	}

	public Response<GetMXcalSerieLastIdResult> getMXcalSerieLastId(GetMXcalSerieLastIdRequest request)
	  throws ServiceException {

		Parser<GetMXcalSerieLastIdResult> parser = new Parser<GetMXcalSerieLastIdResult>() {
			public GetMXcalSerieLastIdResult parse(InputStream inStream) throws IOException {
				return new GetMXcalSerieLastIdResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalSeriestrackLastIdResult> getMXcalSeriestrackLastId()
	  throws ServiceException {
	  	return getMXcalSeriestrackLastId(new GetMXcalSeriestrackLastIdRequest());
	}

	public Response<GetMXcalSeriestrackLastIdResult> getMXcalSeriestrackLastId(GetMXcalSeriestrackLastIdRequest request)
	  throws ServiceException {

		Parser<GetMXcalSeriestrackLastIdResult> parser = new Parser<GetMXcalSeriestrackLastIdResult>() {
			public GetMXcalSeriestrackLastIdResult parse(InputStream inStream) throws IOException {
				return new GetMXcalSeriestrackLastIdResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalTrackLastIdResult> getMXcalTrackLastId()
	  throws ServiceException {
	  	return getMXcalTrackLastId(new GetMXcalTrackLastIdRequest());
	}

	public Response<GetMXcalTrackLastIdResult> getMXcalTrackLastId(GetMXcalTrackLastIdRequest request)
	  throws ServiceException {

		Parser<GetMXcalTrackLastIdResult> parser = new Parser<GetMXcalTrackLastIdResult>() {
			public GetMXcalTrackLastIdResult parse(InputStream inStream) throws IOException {
				return new GetMXcalTrackLastIdResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalQuellfileLastIdResult> getMXcalQuellfileLastId()
	  throws ServiceException {
	  	return getMXcalQuellfileLastId(new GetMXcalQuellfileLastIdRequest());
	}

	public Response<GetMXcalQuellfileLastIdResult> getMXcalQuellfileLastId(GetMXcalQuellfileLastIdRequest request)
	  throws ServiceException {

		Parser<GetMXcalQuellfileLastIdResult> parser = new Parser<GetMXcalQuellfileLastIdResult>() {
			public GetMXcalQuellfileLastIdResult parse(InputStream inStream) throws IOException {
				return new GetMXcalQuellfileLastIdResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<PostMXcalSerieResult> postMXcalSerie(PostMXcalSerieRequest request)
	  throws ServiceException {

		Parser<PostMXcalSerieResult> parser = new Parser<PostMXcalSerieResult>() {
			public PostMXcalSerieResult parse(InputStream inStream) throws IOException {
				return new PostMXcalSerieResult(getReaderProvider(), inStream);
			}
		};

		return post(request, parser);
	}

	public Response<PutMXcalSerieResult> putMXcalSerie(PutMXcalSerieRequest request)
	  throws ServiceException {

		Parser<PutMXcalSerieResult> parser = new Parser<PutMXcalSerieResult>() {
			public PutMXcalSerieResult parse(InputStream inStream) throws IOException {
				return new PutMXcalSerieResult(getReaderProvider(), inStream);
			}
		};

		return put(request, parser);
	}

	public Response<GetMXcalSerieFromResult> getMXcalSerieFrom(GetMXcalSerieFromRequest request)
	  throws ServiceException {

		Parser<GetMXcalSerieFromResult> parser = new Parser<GetMXcalSerieFromResult>() {
			public GetMXcalSerieFromResult parse(InputStream inStream) throws IOException {
				return new GetMXcalSerieFromResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<PostMXcalSeriestrackResult> postMXcalSeriestrack(PostMXcalSeriestrackRequest request)
	  throws ServiceException {

		Parser<PostMXcalSeriestrackResult> parser = new Parser<PostMXcalSeriestrackResult>() {
			public PostMXcalSeriestrackResult parse(InputStream inStream) throws IOException {
				return new PostMXcalSeriestrackResult(getReaderProvider(), inStream);
			}
		};

		return post(request, parser);
	}

	public Response<GetMXcalSeriestrackFromResult> getMXcalSeriestrackFrom(GetMXcalSeriestrackFromRequest request)
	  throws ServiceException {

		Parser<GetMXcalSeriestrackFromResult> parser = new Parser<GetMXcalSeriestrackFromResult>() {
			public GetMXcalSeriestrackFromResult parse(InputStream inStream) throws IOException {
				return new GetMXcalSeriestrackFromResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<PostMXcalTrackResult> postMXcalTrack(PostMXcalTrackRequest request)
	  throws ServiceException {

		Parser<PostMXcalTrackResult> parser = new Parser<PostMXcalTrackResult>() {
			public PostMXcalTrackResult parse(InputStream inStream) throws IOException {
				return new PostMXcalTrackResult(getReaderProvider(), inStream);
			}
		};

		return post(request, parser);
	}

	public Response<GetMXcalTrackFromResult> getMXcalTrackFrom(GetMXcalTrackFromRequest request)
	  throws ServiceException {

		Parser<GetMXcalTrackFromResult> parser = new Parser<GetMXcalTrackFromResult>() {
			public GetMXcalTrackFromResult parse(InputStream inStream) throws IOException {
				return new GetMXcalTrackFromResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<PostMXcalQuellfileResult> postMXcalQuellfile(PostMXcalQuellfileRequest request)
	  throws ServiceException {

		Parser<PostMXcalQuellfileResult> parser = new Parser<PostMXcalQuellfileResult>() {
			public PostMXcalQuellfileResult parse(InputStream inStream) throws IOException {
				return new PostMXcalQuellfileResult(getReaderProvider(), inStream);
			}
		};

		return post(request, parser);
	}

	public Response<GetMXcalQuellfileFromResult> getMXcalQuellfileFrom(GetMXcalQuellfileFromRequest request)
	  throws ServiceException {

		Parser<GetMXcalQuellfileFromResult> parser = new Parser<GetMXcalQuellfileFromResult>() {
			public GetMXcalQuellfileFromResult parse(InputStream inStream) throws IOException {
				return new GetMXcalQuellfileFromResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalSerieLastChangedResult> getMXcalSerieLastChanged()
	  throws ServiceException {
	  	return getMXcalSerieLastChanged(new GetMXcalSerieLastChangedRequest());
	}

	public Response<GetMXcalSerieLastChangedResult> getMXcalSerieLastChanged(GetMXcalSerieLastChangedRequest request)
	  throws ServiceException {

		Parser<GetMXcalSerieLastChangedResult> parser = new Parser<GetMXcalSerieLastChangedResult>() {
			public GetMXcalSerieLastChangedResult parse(InputStream inStream) throws IOException {
				return new GetMXcalSerieLastChangedResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalSeriestrackLastChangedResult> getMXcalSeriestrackLastChanged()
	  throws ServiceException {
	  	return getMXcalSeriestrackLastChanged(new GetMXcalSeriestrackLastChangedRequest());
	}

	public Response<GetMXcalSeriestrackLastChangedResult> getMXcalSeriestrackLastChanged(GetMXcalSeriestrackLastChangedRequest request)
	  throws ServiceException {

		Parser<GetMXcalSeriestrackLastChangedResult> parser = new Parser<GetMXcalSeriestrackLastChangedResult>() {
			public GetMXcalSeriestrackLastChangedResult parse(InputStream inStream) throws IOException {
				return new GetMXcalSeriestrackLastChangedResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalTrackLastChangedResult> getMXcalTrackLastChanged()
	  throws ServiceException {
	  	return getMXcalTrackLastChanged(new GetMXcalTrackLastChangedRequest());
	}

	public Response<GetMXcalTrackLastChangedResult> getMXcalTrackLastChanged(GetMXcalTrackLastChangedRequest request)
	  throws ServiceException {

		Parser<GetMXcalTrackLastChangedResult> parser = new Parser<GetMXcalTrackLastChangedResult>() {
			public GetMXcalTrackLastChangedResult parse(InputStream inStream) throws IOException {
				return new GetMXcalTrackLastChangedResult(getReaderProvider(), inStream);
			}
		};

		return get(request, parser);
	}

	public Response<GetMXcalQuellfileLastChangedResult> getMXcalQuellfileLastChanged()
	  throws ServiceException {
	  	return getMXcalQuellfileLastChanged(new GetMXcalQuellfileLastChangedRequest());
	}
	
	public Response<GetMXcalQuellfileLastChangedResult> getMXcalQuellfileLastChanged(GetMXcalQuellfileLastChangedRequest request)
	  throws ServiceException {
		
		Parser<GetMXcalQuellfileLastChangedResult> parser = new Parser<GetMXcalQuellfileLastChangedResult>() {
			public GetMXcalQuellfileLastChangedResult parse(InputStream inStream) throws IOException {
				return new GetMXcalQuellfileLastChangedResult(getReaderProvider(), inStream);
			}
		};
		
		return get(request, parser);
	}
	
}
