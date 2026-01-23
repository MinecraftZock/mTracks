package info.hannes.mechadminGen.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetSearchRequest extends ServiceRequest {
	
	private static final String PATH = "/api/v1/series/search.js";
	
	public GetSearchRequest(){
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + PATH).buildUpon();
			
		return uriBuilder.toString();			
	}
}
