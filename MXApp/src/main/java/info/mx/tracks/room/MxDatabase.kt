package info.mx.tracks.room

import androidx.room.Database
import androidx.room.RoomDatabase
import info.mx.tracks.room.entity.*
import info.mx.tracks.room.memory.NetworkDao

@Database(entities = [CapturedLatLng::class, Comment::class, Country::class, Event::class,
    Favorit::class, Network::class, Picture::class, Route::class, Series::class, Track::class,
    TrackStage::class, Weather::class], version = 2)
abstract class MxDatabase : RoomDatabase() {

    abstract fun capturedLatLngDao(): CapturedLatLngDao

    abstract fun commentDao(): CommentDao

    abstract fun eventDao(): EventDao

    abstract fun networkDao(): NetworkDao

    companion object {

        const val ROOM_DATABASE_NAME = "mx_native.db"
    }
}
