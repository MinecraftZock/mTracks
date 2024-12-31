package info.hannes.mechadmin_gen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetMXcalQuellfileFromRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcalquellfile/changed/%s";
	
	private final long fromSegment;
	
	public GetMXcalQuellfileFromRequest(long fromSegment){
		this.fromSegment = fromSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, fromSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
