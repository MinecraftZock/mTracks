package info.mx.tracks.room.memory;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import info.mx.tracks.room.memory.entity.TracksDistance;

@Database(entities = {TracksDistance.class}, version = 1)
public abstract class MxMemDatabase extends RoomDatabase {

    public abstract TracksDistanceDao tracksDistanceDao();
}
