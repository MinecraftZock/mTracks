package info.hannes.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import info.hannes.room.dao.*
import info.hannes.room.entity.*

@Database(
    entities = [
        TrackstageBrother::class,
        TrackstageRid::class,
        PictureStage::class,
        Videos::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MxAdminDatabase : RoomDatabase() {
    
    abstract fun trackstageBrotherDao(): TrackstageBrotherDao
    abstract fun trackstageRidDao(): TrackstageRidDao
    abstract fun pictureStageDao(): PictureStageDao
    abstract fun videosDao(): VideosDao
    
    companion object {
        const val DATABASE_NAME = "mx_admin.db"
        
        @Volatile
        private var INSTANCE: MxAdminDatabase? = null
        
        fun getDatabase(context: Context): MxAdminDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MxAdminDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}