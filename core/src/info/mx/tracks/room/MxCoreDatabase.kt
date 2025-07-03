package info.mx.tracks.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import info.mx.tracks.room.dao.TrackDao
import info.mx.tracks.room.dao.PictureDao
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.entity.Picture

@Database(
    entities = [
        CapturedLatLng::class,
        Track::class,
        Picture::class
    ],
    version = 2,
    exportSchema = true
)
abstract class MxCoreDatabase : RoomDatabase() {
    
    abstract fun capturedLatLngDao(): CapturedLatLngDao
    abstract fun trackDao(): TrackDao
    abstract fun pictureDao(): PictureDao
    
    companion object {
        const val DATABASE_NAME = "mx_core.db"
        
        @Volatile
        private var INSTANCE: MxCoreDatabase? = null
        
        fun getDatabase(context: Context): MxCoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MxCoreDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create tracks table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tracks (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        restId INTEGER NOT NULL,
                        changed INTEGER NOT NULL,
                        trackname TEXT NOT NULL,
                        longitude REAL NOT NULL,
                        latitude REAL NOT NULL,
                        approved INTEGER NOT NULL,
                        country TEXT NOT NULL,
                        url TEXT,
                        fees TEXT,
                        phone TEXT,
                        notes TEXT,
                        contact TEXT,
                        notes_en TEXT,
                        metatext TEXT,
                        licence TEXT,
                        kidstrack INTEGER NOT NULL DEFAULT 0,
                        openmondays INTEGER NOT NULL DEFAULT 0,
                        opentuesdays INTEGER NOT NULL DEFAULT 0,
                        openwednesday INTEGER NOT NULL DEFAULT 0,
                        openthursday INTEGER NOT NULL DEFAULT 0,
                        openfriday INTEGER NOT NULL DEFAULT 0,
                        opensaturday INTEGER NOT NULL DEFAULT 0,
                        opensunday INTEGER NOT NULL DEFAULT 0,
                        hoursmonday TEXT,
                        hourstuesday TEXT,
                        hourswednesday TEXT,
                        hoursthursday TEXT,
                        hoursfriday TEXT,
                        hourssaturday TEXT,
                        hourssunday TEXT,
                        tracklength INTEGER NOT NULL DEFAULT 0,
                        soiltype TEXT,
                        camping INTEGER NOT NULL DEFAULT 0,
                        shower INTEGER NOT NULL DEFAULT 0,
                        cleaning INTEGER NOT NULL DEFAULT 0,
                        electricity INTEGER NOT NULL DEFAULT 0,
                        distance2location INTEGER NOT NULL DEFAULT 0,
                        supercross INTEGER NOT NULL DEFAULT 0,
                        trackaccess INTEGER NOT NULL DEFAULT 0,
                        logo_u_r_l TEXT,
                        showroom INTEGER NOT NULL DEFAULT 0,
                        workshop INTEGER NOT NULL DEFAULT 0,
                        validuntil TEXT,
                        brands TEXT,
                        nu_events INTEGER NOT NULL DEFAULT 0,
                        facebook TEXT,
                        trackstage INTEGER NOT NULL DEFAULT 0,
                        region TEXT,
                        difficulty INTEGER NOT NULL DEFAULT 0,
                        owner TEXT,
                        racingonly INTEGER NOT NULL DEFAULT 0,
                        club TEXT,
                        ratingsum INTEGER NOT NULL DEFAULT 0,
                        ratingcount INTEGER NOT NULL DEFAULT 0,
                        ratingavrg REAL NOT NULL DEFAULT 0.0,
                        favorite INTEGER NOT NULL DEFAULT 0,
                        votesum INTEGER NOT NULL DEFAULT 0,
                        votecount INTEGER NOT NULL DEFAULT 0,
                        votelast INTEGER NOT NULL DEFAULT 0,
                        sync INTEGER NOT NULL DEFAULT 0,
                        locallat REAL NOT NULL DEFAULT 0.0,
                        locallng REAL NOT NULL DEFAULT 0.0,
                        localdistance INTEGER NOT NULL DEFAULT 0,
                        ownpicture INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                // Create pictures table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS pictures (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        restId INTEGER NOT NULL,
                        changed INTEGER NOT NULL,
                        username TEXT,
                        comment TEXT,
                        trackRestId INTEGER NOT NULL,
                        approved INTEGER NOT NULL DEFAULT 0,
                        deleted INTEGER NOT NULL DEFAULT 0,
                        localfile TEXT,
                        localthumb TEXT
                    )
                """)
            }
        }
    }
}