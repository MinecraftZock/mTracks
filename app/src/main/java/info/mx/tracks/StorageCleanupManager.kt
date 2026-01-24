package info.mx.tracks

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
import info.mx.tracks.sqlite.MxInfoDBContract.Pictures
import info.mx.tracks.sqlite.PicturesRecord
import timber.log.Timber
import java.io.File
import java.util.UUID

/**
 * Modern storage manager for cleaning up old cached image files.
 *
 * Replaces the deprecated ACTION_DEVICE_STORAGE_LOW broadcast receiver with
 * proactive storage monitoring using StorageStatsManager (API 26+) or StatFs for older versions.
 *
 * This manager:
 * - Checks available storage space
 * - Automatically cleans up cached images older than 12 hours when storage is low
 * - Uses modern Android storage APIs
 */
object StorageCleanupManager {

    private const val LOW_STORAGE_THRESHOLD_BYTES = 400 * 1024 * 1024L // 400 MB
    private const val CACHE_FILE_MAX_AGE_MS = 24 * 60 * 60 * 1000L // 24 hours

    /**
     * Checks if device storage is low and performs cleanup if needed.
     *
     * @param context Application context
     * @return true if cleanup was performed, false otherwise
     */
    fun checkAndCleanupIfNeeded(context: Context): Boolean {
        return try {
            val availableBytes = getAvailableStorageBytes(context)
            Timber.d("Available storage: ${availableBytes / 1024 / 1024} MB")

            if (availableBytes < LOW_STORAGE_THRESHOLD_BYTES) {
                Timber.w("Low storage detected (${availableBytes / 1024 / 1024} MB). Starting cleanup...")
                cleanupOldCachedImages()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking storage or cleaning up")
            false
        }
    }

    /**
     * Gets available storage bytes using modern APIs (API 26+) or fallback for older versions.
     */
    private fun getAvailableStorageBytes(context: Context): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getAvailableStorageBytesModern(context)
        } else {
            getAvailableStorageBytesLegacy()
        }
    }

    /**
     * Modern approach using StorageStatsManager (API 26+)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAvailableStorageBytesModern(context: Context): Long {
        return try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

            val storageVolume = storageManager.primaryStorageVolume
            val uuidStr = storageVolume.uuid
            val uuid = if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)

            storageStatsManager.getFreeBytes(uuid)
        } catch (e: Exception) {
            Timber.e(e, "Error getting storage stats with modern API, falling back")
            getAvailableStorageBytesLegacy()
        }
    }

    /**
     * Legacy approach using StatFs (for API < 26)
     */
    @Suppress("DEPRECATION")
    private fun getAvailableStorageBytesLegacy(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        return stat.availableBlocksLong * stat.blockSizeLong
    }

    /**
     * Cleans up old cached image files.
     * Deletes local image files that are older than CACHE_FILE_MAX_AGE_MS.
     */
    private fun cleanupOldCachedImages() {
        val cutoffTime = System.currentTimeMillis() - CACHE_FILE_MAX_AGE_MS
        var deletedCount = 0
        var deletedBytes = 0L

        try {
            val records = SQuery.newQuery()
                .expr(Pictures.LOCALFILE, Op.NEQ, "")
                .select<PicturesRecord>(Pictures.CONTENT_URI, Pictures._ID)

            for (rec in records) {
                val file = File(rec.localfile)
                if (file.exists() && file.lastModified() < cutoffTime) {
                    val fileSize = file.length()
                    if (file.delete()) {
                        deletedCount++
                        deletedBytes += fileSize
                        Timber.d("Deleted cached file: ${file.name} (${fileSize / 1024} KB)")
                    }
                }
            }

            if (deletedCount > 0) {
                Timber.i("Cleanup complete: Deleted $deletedCount files, freed ${deletedBytes / 1024 / 1024} MB")
            } else {
                Timber.d("No old cached files to delete")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during cache cleanup")
        }
    }

}
