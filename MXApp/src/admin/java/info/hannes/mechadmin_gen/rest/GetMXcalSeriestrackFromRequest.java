package info.hannes.mechadmin_gen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetMXcalSeriestrackFromRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcalseriestrack/changed/%s";
	
	private final long fromSegment;
	
	public GetMXcalSeriestrackFromRequest(long fromSegment){
		this.fromSegment = fromSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, fromSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
