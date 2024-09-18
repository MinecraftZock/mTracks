package info.mx.tracks.rest;


import android.net.Uri;

import com.robotoworks.mechanoid.net.ServiceRequest;

public class GetRouteRequest extends ServiceRequest {

    private static final String PATH = "/maps/api/directions/json";

    private String originParam;
    private boolean originParamSet;
    private String destinationParam;
    private boolean destinationParamSet;
    private String sensorParam;
    private boolean sensorParamSet;

    public GetRouteRequest setOriginParam(String value) {
        this.originParam = value;
        this.originParamSet = true;
        return this;
    }

    public boolean isOriginParamSet() {
        return originParamSet;
    }

    public GetRouteRequest setDestinationParam(String value) {
        this.destinationParam = value;
        this.destinationParamSet = true;
        return this;
    }

    public boolean isDestinationParamSet() {
        return destinationParamSet;
    }

    public GetRouteRequest setSensorParam(String value) {
        this.sensorParam = value;
        this.sensorParamSet = true;
        return this;
    }

    public boolean isSensorParamSet() {
        return sensorParamSet;
    }

    public GetRouteRequest() {
    }

    @Override
    public String createUrl(String baseUrl) {
        Uri.Builder uriBuilder = Uri.parse(baseUrl + PATH).buildUpon();

        if (originParamSet) {
            uriBuilder.appendQueryParameter("origin", originParam);
        }
        if (destinationParamSet) {
            uriBuilder.appendQueryParameter("destination", destinationParam);
        }
        if (sensorParamSet) {
            uriBuilder.appendQueryParameter("sensor", sensorParam);
        }

        return uriBuilder.toString();
    }
}
