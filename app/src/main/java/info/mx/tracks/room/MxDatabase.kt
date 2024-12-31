package info.mx.tracks.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CapturedLatLng::class], version = 1)
abstract class MxDatabase : RoomDatabase() {

    abstract fun capturedLatLngDao(): CapturedLatLngDao

    companion object {

        const val ROOM_DATABASE_NAME = "mx_native.db"
    }
}
