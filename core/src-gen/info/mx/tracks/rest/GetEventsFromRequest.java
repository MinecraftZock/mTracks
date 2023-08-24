package info.mx.tracks.rest;


import android.net.Uri;
import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetEventsFromRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/events/changed/%s";
	
	private final long fromSegment;
	
	public GetEventsFromRequest(long fromSegment){
		this.fromSegment = fromSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, fromSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
