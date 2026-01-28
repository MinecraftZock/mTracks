package info.mx.core.ops

import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.net.Response
import com.robotoworks.mechanoid.net.ServiceException
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.commonlib.NetworkHelper.isOnline
import info.hannes.commonlib.TrackingApplication.Companion.isDebug
import info.mx.core.MxCoreApplication.Companion.isAdminOrDebug
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.rest.GetWeatherDailyRequest
import info.mx.core_generated.rest.GetWeatherDailyResult
import info.mx.core_generated.rest.GetWeatherHourRequest
import info.mx.core.rest.OpenWeather
import info.mx.core_generated.ops.AbstractOpGetWeatherOperation
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.core_generated.sqlite.WeatherRecord
import timber.log.Timber
import java.util.*
import kotlin.math.roundToLong

class OpGetWeatherOperation : AbstractOpGetWeatherOperation() {
    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        val gson = Gson()
        if (isOnline(context.applicationContext)) {
            val del = SQuery.newQuery()
                .expr(
                    MxInfoDBContract.Weather.DT,
                    SQuery.Op.LT,
                    (System.currentTimeMillis() / 1000).toFloat().roundToLong()
                )
                .delete(MxInfoDBContract.Weather.CONTENT_URI)
            Timber.d("deleted :%s", del)
            val weatherClient = OpenWeather(isDebug)
            try {
                val daysAvail = SQuery.newQuery()
                    .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "D")
                    .expr(
                        MxInfoDBContract.Weather.TRACK_CLIENT_ID,
                        SQuery.Op.EQ,
                        args.trackClientId
                    )
                    .count(MxInfoDBContract.Weather.CONTENT_URI)
                if (daysAvail <= DAYS_DOWNLOAD - 3) {
                    val request = GetWeatherDailyRequest(WEATHER_SERVER_VERSION)
                    request.setCntParam(DAYS_DOWNLOAD)
                    request.setAPPIDParam(WEATHER_ID)
                    request.setLangParam(Locale.getDefault().language)
                    if (MxPreferences.getInstance().unitsKm) {
                        request.setUnitsParam(METRIC)
                    } else {
                        request.setUnitsParam(IMPERIAL)
                    }
                    request.setLatParam(args.lat)
                    request.setLonParam(args.lon)
                    val response = callOpenWeather(weatherClient, request)
                    val resp = response!!.parse()
                    for (weatherDay in resp.list) {
                        var weatherRec = SQuery.newQuery()
                            .expr(MxInfoDBContract.Weather.DT, SQuery.Op.EQ, weatherDay.dt)
                            .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "D")
                            .selectFirst<WeatherRecord>(MxInfoDBContract.Weather.CONTENT_URI)
                        if (weatherRec == null) {
                            weatherRec = WeatherRecord()
                        }
                        weatherRec.dt = weatherDay.dt
                        weatherRec.type = "D"
                        weatherRec.content = gson.toJson(weatherDay)
                        weatherRec.trackClientId = args.trackClientId
                        weatherRec.save(false)
                    }
                    context.applicationContext.contentResolver.notifyChange(
                        MxInfoDBContract.Weather.CONTENT_URI,
                        null
                    )
                }
                val hourAvail = SQuery.newQuery()
                    .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "H")
                    .expr(
                        MxInfoDBContract.Weather.TRACK_CLIENT_ID,
                        SQuery.Op.EQ,
                        args.trackClientId
                    )
                    .count(MxInfoDBContract.Weather.CONTENT_URI)
                if (hourAvail <= HOUR_DOWNLOAD - 3 && USE_HOUR_FORECAST) {
                    val request = GetWeatherHourRequest(WEATHER_SERVER_VERSION)
                    request.setCntParam(HOUR_DOWNLOAD)
                    request.setAPPIDParam(WEATHER_ID)
                    request.setLangParam(Locale.getDefault().language)
                    if (MxPreferences.getInstance().unitsKm) {
                        request.setUnitsParam(METRIC)
                    } else {
                        request.setUnitsParam(IMPERIAL)
                    }
                    request.setLatParam(args.lat)
                    request.setLonParam(args.lon)
                    val response = weatherClient.getWeatherHour(request)
                    response.checkResponseCodeOk()
                    val resp = response.parse()
                    for (weatherHour in resp.list) {
                        var weatherRec = SQuery.newQuery()
                            .expr(MxInfoDBContract.Weather.DT, SQuery.Op.EQ, weatherHour.dt)
                            .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "H")
                            .selectFirst<WeatherRecord>(MxInfoDBContract.Weather.CONTENT_URI)
                        if (weatherRec == null) {
                            weatherRec = WeatherRecord()
                        }
                        weatherRec.dt = weatherHour.dt
                        weatherRec.type = "H"
                        weatherRec.content = gson.toJson(weatherHour)
                        weatherRec.trackClientId = args.trackClientId
                        weatherRec.save(false)
                    }
                    context.applicationContext.contentResolver.notifyChange(
                        MxInfoDBContract.Weather.CONTENT_URI,
                        null
                    )
                }
            } catch (e: ServiceException) {
                if (isAdminOrDebug) {
                    Toast.makeText(context.applicationContext, "${this.javaClass.name} ${e.message}", Toast.LENGTH_LONG).show()
                }
                return OperationResult.error(e)
            } catch (e: Exception) {
                Timber.e(e)
                if (isAdminOrDebug) {
                    Toast.makeText(context.applicationContext, "${this.javaClass.name} ${e.message}", Toast.LENGTH_LONG).show()
                }
                return OperationResult.error(e)
            }
        }
        return OperationResult.ok(bundle)
    }

    /**
     * try it X-time
     */
    @Throws(Exception::class)
    private fun callOpenWeather(
        weatherClient: OpenWeather,
        request: GetWeatherDailyRequest
    ): Response<GetWeatherDailyResult>? {
        var response: Response<GetWeatherDailyResult>? = null
        for (i in 0..MAX_LOOP) {
            try {
                response = weatherClient.getWeatherDaily(request)
                response.checkResponseCodeOk()
            } catch (e: ServiceException) {
                Timber.e(e)
                if (i == MAX_LOOP) {
                    throw ServiceException(e)
                }
            } catch (e: Exception) {
                Timber.e(e)
                if (i == MAX_LOOP) {
                    throw Exception(e)
                }
            }
        }
        return response
    }

    companion object {
        private const val WEATHER_SERVER_VERSION = 2.5
        private const val WEATHER_ID = "ae0af421e6928f15e64edc3c0833a125"
        private const val METRIC = "metric"
        private const val IMPERIAL = "imperial"
        private const val DAYS_DOWNLOAD = 16
        private const val HOUR_DOWNLOAD = 10
        private const val USE_HOUR_FORECAST = false
        private const val MAX_LOOP = 1
    }
}
