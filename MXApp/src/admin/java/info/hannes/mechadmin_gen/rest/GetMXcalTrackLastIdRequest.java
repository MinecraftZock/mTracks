package info.hannes.mechadmin_gen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetMXcalTrackLastIdRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcaltrack/last";
	
	public GetMXcalTrackLastIdRequest(){
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + PATH).buildUpon();
			
		return uriBuilder.toString();			
	}
}
