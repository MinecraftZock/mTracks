package info.hannes.mechadminGen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetMXcalSerieLastIdRequest extends ServiceRequest {
	
	private static final String PATH = "/MXServer/rest/mxcalserie/last";
	
	public GetMXcalSerieLastIdRequest(){
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + PATH).buildUpon();
			
		return uriBuilder.toString();			
	}
}
