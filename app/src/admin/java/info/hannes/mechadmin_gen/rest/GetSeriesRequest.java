package info.hannes.mechadmin_gen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetSeriesRequest extends ServiceRequest {
	
	private static final String PATH = "/api/v1/series/%s";
	
	private final String resourceNameSegment;
	
	public GetSeriesRequest(String resourceNameSegment){
		this.resourceNameSegment = resourceNameSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, resourceNameSegment)).buildUpon();
			
		return uriBuilder.toString();			
	}
}
