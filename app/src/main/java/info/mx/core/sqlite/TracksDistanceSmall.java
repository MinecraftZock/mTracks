package info.mx.core.sqlite;

import androidx.annotation.NonNull;

public class TracksDistanceSmall implements Comparable<TracksDistanceSmall> {

    private int distance = 0;
    private final long _id;
    private final double lat;
    private final double lon;

    public TracksDistanceSmall(long _id, double lat, double lon) {
        this._id = _id;
        this.lat = lat;
        this.lon = lon;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull TracksDistanceSmall another) {
        if (getDistance() == another.getDistance()) {
            return 0;
        } else {
            return Long.compare(getDistance(), another.getDistance()); // ascending
        }
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long get_id() {
        return _id;
    }
}
