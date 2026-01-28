package info.mx.core_generated.rest;


import android.net.Uri;
import com.robotoworks.mechanoid.net.ServiceRequest;

public class DelTrackstageRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/tracksstage/%s";
	
	private final long idSegment;
	
	public DelTrackstageRequest(long idSegment){
		this.idSegment = idSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, idSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
