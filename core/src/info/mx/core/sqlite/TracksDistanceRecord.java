package info.mx.core.sqlite;

import android.database.Cursor;
import android.location.Location;

import androidx.annotation.NonNull;

import info.hannes.commonlib.LocationHelper;
import info.mx.core_generated.sqlite.TracksRecord;
import info.mx.core_generated.sqlite.TracksgesRecord;
import info.mx.tracks.common.SecHelper;

public class TracksDistanceRecord extends TracksgesRecord implements Comparable<TracksDistanceRecord> {

    private static int distance = 0;

    public static TracksDistanceRecord fromCursor(Cursor c, Location currentPos) {
        final TracksDistanceRecord item = new TracksDistanceRecord();

        item.setPropertiesFromCursor(c);

        item.makeDirty(false);

        if (currentPos != null) {
            final Location trackPos = new Location("track");
            trackPos.setLatitude(SecHelper.entcryptXtude(item.getLatitude()));
            trackPos.setLongitude(SecHelper.entcryptXtude(item.getLongitude()));
            distance = Math.round(trackPos.distanceTo(currentPos));
            item.setDistance2location(distance);
        }

        return item;
    }

    public TracksDistanceRecord() {

    }

    public int storeDistance2DB(Location currLoc) {
        int res = -1;
        if (currLoc != null && currLoc.getLatitude() != 0) {

            final Location locDB = new Location("trackDB");
            locDB.setLatitude(SecHelper.entcryptXtude(this.getLatitude()));
            locDB.setLongitude(SecHelper.entcryptXtude(this.getLongitude()));

            final Location trackThis = new Location("trackThis");
            trackThis.setLatitude(SecHelper.entcryptXtude(getLatitude()));
            trackThis.setLongitude(SecHelper.entcryptXtude(getLongitude()));

            final int distDB = Math.round(currLoc.distanceTo(trackThis));
            if (!LocationHelper.INSTANCE.getFormatDistance(true, distDB).equals(LocationHelper.INSTANCE.getFormatDistance(true, (int) getDistance2location()))) {
                final TracksRecord trackDB = TracksRecord.get(this.getId());
                if (trackDB != null) {
                    trackDB.setDistance2location(distDB);
                    res = distDB;
                    trackDB.save(false);
                }
            }
        }
        return res;
    }

    @Override
    public int compareTo(@NonNull TracksDistanceRecord another) {
        if (this.getDistance2location() == another.getDistance2location()) {
            return 0;
        } else {
            return Long.compare(this.getDistance2location(), another.getDistance2location()); // ascending
        }
    }

}
