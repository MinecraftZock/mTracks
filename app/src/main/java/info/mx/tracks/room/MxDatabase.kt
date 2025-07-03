package info.mx.tracks.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

@Database(entities = [CapturedLatLng::class], version = 1, exportSchema = true)
abstract class MxDatabase : RoomDatabase() {

    abstract fun capturedLatLngDao(): CapturedLatLngDao

    companion object {
        const val ROOM_DATABASE_NAME = "mx_native.db"
        
        @Volatile
        private var INSTANCE: MxDatabase? = null
        
        fun getDatabase(context: Context): MxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MxDatabase::class.java,
                    ROOM_DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
