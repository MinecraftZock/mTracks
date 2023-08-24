package info.mx.tracks.rest;


import com.robotoworks.mechanoid.net.Parser;
import java.io.IOException;
import com.robotoworks.mechanoid.net.Response;
import com.robotoworks.mechanoid.net.ServiceException;
import java.io.InputStream;
import com.robotoworks.mechanoid.net.ServiceClient;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;

public abstract class AbstractOpenWeather extends ServiceClient {
	private static final String LOG_TAG = "OpenWeather";
	
	protected static final String DEFAULT_BASE_URL = "http://api.openweathermap.org";
	
	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}
	
	@Override
	protected JsonEntityWriterProvider createWriterProvider() {
		return new DefaultOpenWeatherWriterProvider();
	}
	
	@Override
	protected JsonEntityReaderProvider createReaderProvider() {
		return new DefaultOpenWeatherReaderProvider();
	}
	
	public AbstractOpenWeather(String baseUrl, boolean debug){
		super(baseUrl, debug);
		
	}
	
	public Response<GetWeatherDailyResult> getWeatherDaily(GetWeatherDailyRequest request)
	  throws ServiceException {
		
		Parser<GetWeatherDailyResult> parser = new Parser<GetWeatherDailyResult>() {
			public GetWeatherDailyResult parse(InputStream inStream) throws IOException {
				return new GetWeatherDailyResult(getReaderProvider(), inStream);
			}
		};
		
		return get(request, parser);
	}
	
	public Response<GetWeatherDailyAnonymResult> getWeatherDailyAnonym(GetWeatherDailyAnonymRequest request)
	  throws ServiceException {
		
		Parser<GetWeatherDailyAnonymResult> parser = new Parser<GetWeatherDailyAnonymResult>() {
			public GetWeatherDailyAnonymResult parse(InputStream inStream) throws IOException {
				return new GetWeatherDailyAnonymResult(getReaderProvider(), inStream);
			}
		};
		
		return get(request, parser);
	}
	
	public Response<GetWeatherHourResult> getWeatherHour(GetWeatherHourRequest request)
	  throws ServiceException {
		
		Parser<GetWeatherHourResult> parser = new Parser<GetWeatherHourResult>() {
			public GetWeatherHourResult parse(InputStream inStream) throws IOException {
				return new GetWeatherHourResult(getReaderProvider(), inStream);
			}
		};
		
		return get(request, parser);
	}
	
}
