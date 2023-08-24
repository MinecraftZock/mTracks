package info.mx.tracks.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TracksDistance::class], version = 1)
abstract class MxMemDatabase : RoomDatabase() {

    abstract fun tracksDistanceDao(): TracksDistanceDao
}
