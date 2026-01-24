package info.mx.tracks.service

import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

class LocationRecalculateService(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val latitude = inputData.getDouble(KEY_LATITUDE, Double.NaN)
        val longitude = inputData.getDouble(KEY_LONGITUDE, Double.NaN)
        val source = inputData.getString(KEY_SOURCE)

        if (latitude.isNaN() || longitude.isNaN()) {
            return Result.failure()
        }

        val location = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }

        val recalculateDistance = RecalculateDistance(applicationContext)
        recalculateDistance.recalculateTracks(location, "$source Service")

        return Result.success()
    }

    companion object {
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_SOURCE = "source"

        fun enqueueWork(context: Context, location: Location?, source: String?) {
            location?.let {
                val inputData = Data.Builder()
                    .putDouble(KEY_LATITUDE, it.latitude)
                    .putDouble(KEY_LONGITUDE, it.longitude)
                    .putString(KEY_SOURCE, source)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<LocationRecalculateService>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }
}
