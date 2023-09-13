package info.mx.tracks.rest;


import android.net.Uri;
import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetWeatherHourRequest extends ServiceRequest {
	
	private static final String PATH = "/data/%s/forecast";
	
	private final double apiVersionSegment;
	
	private String aPPIDParam;
	private boolean aPPIDParamSet;
	private double latParam;
	private boolean latParamSet;
	private double lonParam;
	private boolean lonParamSet;
	private int cntParam;
	private boolean cntParamSet;
	private String modeParam;
	private boolean modeParamSet;
	private String unitsParam;
	private boolean unitsParamSet;
	private String langParam;
	private boolean langParamSet;
		
	public GetWeatherHourRequest setAPPIDParam(String value) {
		this.aPPIDParam = value;
		this.aPPIDParamSet = true;
		return this;
	}
	
	public boolean isAPPIDParamSet() {
		return aPPIDParamSet;
	}
	public GetWeatherHourRequest setLatParam(double value) {
		this.latParam = value;
		this.latParamSet = true;
		return this;
	}
	
	public boolean isLatParamSet() {
		return latParamSet;
	}
	public GetWeatherHourRequest setLonParam(double value) {
		this.lonParam = value;
		this.lonParamSet = true;
		return this;
	}
	
	public boolean isLonParamSet() {
		return lonParamSet;
	}
	public GetWeatherHourRequest setCntParam(int value) {
		this.cntParam = value;
		this.cntParamSet = true;
		return this;
	}
	
	public boolean isCntParamSet() {
		return cntParamSet;
	}
	public GetWeatherHourRequest setModeParam(String value) {
		this.modeParam = value;
		this.modeParamSet = true;
		return this;
	}
	
	public boolean isModeParamSet() {
		return modeParamSet;
	}
	public GetWeatherHourRequest setUnitsParam(String value) {
		this.unitsParam = value;
		this.unitsParamSet = true;
		return this;
	}
	
	public boolean isUnitsParamSet() {
		return unitsParamSet;
	}
	public GetWeatherHourRequest setLangParam(String value) {
		this.langParam = value;
		this.langParamSet = true;
		return this;
	}
	
	public boolean isLangParamSet() {
		return langParamSet;
	}
		
	public GetWeatherHourRequest(double apiVersionSegment){
		this.apiVersionSegment = apiVersionSegment;
	}
	
	@Override
	public String createUrl(String baseUrl){
		Uri.Builder uriBuilder = Uri.parse(baseUrl + String.format(PATH, apiVersionSegment)).buildUpon();
			
		if(aPPIDParamSet){
			uriBuilder.appendQueryParameter("APPID", aPPIDParam);
		}
		if(latParamSet){
			uriBuilder.appendQueryParameter("lat", String.valueOf(latParam));
		}
		if(lonParamSet){
			uriBuilder.appendQueryParameter("lon", String.valueOf(lonParam));
		}
		if(cntParamSet){
			uriBuilder.appendQueryParameter("cnt", String.valueOf(cntParam));
		}
		if(modeParamSet){
			uriBuilder.appendQueryParameter("mode", modeParam);
		}
		if(unitsParamSet){
			uriBuilder.appendQueryParameter("units", unitsParam);
		}
		if(langParamSet){
			uriBuilder.appendQueryParameter("lang", langParam);
		}
		
		return uriBuilder.toString();			
	}
}
