package info.mx.tracks.room.memory

import androidx.room.Database
import androidx.room.RoomDatabase
import info.mx.tracks.room.memory.entity.TracksDistance

@Database(entities = [TracksDistance::class], version = 1)
abstract class MxMemDatabase : RoomDatabase() {

    abstract fun tracksDistanceDao(): TracksDistanceDao
}
