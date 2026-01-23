package info.hannes.mechadminGen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetMXcalTrackFromRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcaltrack/changed/%s";
	
	private final long fromSegment;
	
	public GetMXcalTrackFromRequest(long fromSegment){
		this.fromSegment = fromSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, fromSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
