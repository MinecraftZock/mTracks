package info.mx.tracks.room

import android.content.Context
import info.hannes.room.MxAdminDatabase
import info.mx.tracks.room.repository.TrackRepository

/**
 * Database manager for handling Room database instances
 */
object DatabaseManager {
    
    @Volatile
    private var coreDatabase: MxCoreDatabase? = null
    
    @Volatile
    private var adminDatabase: MxAdminDatabase? = null
    
    @Volatile
    private var trackRepository: TrackRepository? = null
    
    fun getCoreDatabase(context: Context): MxCoreDatabase {
        return coreDatabase ?: synchronized(this) {
            val instance = MxCoreDatabase.getDatabase(context)
            coreDatabase = instance
            instance
        }
    }
    
    fun getAdminDatabase(context: Context): MxAdminDatabase {
        return adminDatabase ?: synchronized(this) {
            val instance = MxAdminDatabase.getDatabase(context)
            adminDatabase = instance
            instance
        }
    }
    
    fun getTrackRepository(context: Context): TrackRepository {
        return trackRepository ?: synchronized(this) {
            val instance = TrackRepository(getCoreDatabase(context))
            trackRepository = instance
            instance
        }
    }
    
    fun clearCoreDatabase() {
        coreDatabase?.clearAllTables()
    }
    
    fun clearAdminDatabase() {
        adminDatabase?.clearAllTables()
    }
}