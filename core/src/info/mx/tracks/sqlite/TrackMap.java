package info.mx.tracks.sqlite;

import info.mx.tracks.common.SecHelper;

public class TrackMap extends TracksDistanceSmall {

    private String access;
    private String status;

    public TrackMap(long _id, double latCrypt, double lonCrypt, String access, String status) {
        super(_id, latCrypt, lonCrypt);
        this.access = access;
        this.status = status;
    }

    public double getLatDecrypt() {
        return SecHelper.entcryptXtude(getLat());
    }

    public double getLonDecrypt() {
        return SecHelper.entcryptXtude(getLon());
    }

    public String getAccess() {
        return access;
    }

    public String getStatus() {
        return status;
    }
}
