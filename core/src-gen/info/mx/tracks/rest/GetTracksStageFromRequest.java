package info.mx.tracks.rest;


import android.net.Uri;
import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetTracksStageFromRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/tracksstage/changed/%s";
	
	private final long lastSegment;
	
	public GetTracksStageFromRequest(long lastSegment){
		this.lastSegment = lastSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, lastSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
