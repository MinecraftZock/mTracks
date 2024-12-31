package info.hannes.mechadmin_gen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetMXcalSerieLastChangedRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcalserie/lastChanged";
	
	public GetMXcalSerieLastChangedRequest(){
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + PATH).buildUpon();
			
		return uriBuilder.toString();			
	}
}
