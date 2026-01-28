package info.mx.tracks.ops

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.net.Response
import com.robotoworks.mechanoid.net.ServiceException
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.commonlib.NetworkHelper.isOnline
import info.mx.tracks.MxCoreApplication.Companion.isAdminOrDebug
import info.mx.tracks.MxCoreApplication.Companion.mxInfo
import info.mx.tracks.MxCoreApplication.Companion.showWeather
import info.mx.tracks.common.SecHelper
import info.mx.tracks.data.DataManagerCore
import info.mx.tracks.koin.CoreKoinComponent
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.rest.*
import info.mx.tracks.rest.model.GetWeatherDaily2Result
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.sqlite.WeatherRecord
import info.mx.tracks.util.checkResponseCodeOk
import org.koin.core.component.inject
import timber.log.Timber
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt

internal class OpWeatherCachedOperation : AbstractOpGetWeatherCachedOperation(), CoreKoinComponent {

    private val dataManagerCore: DataManagerCore by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        val gson = Gson()
        var weatherCached: String?
        if (!isOnline(context.applicationContext)) {
            return OperationResult.ok(bundle)
        }
        val trackRec = TracksRecord.get(args.trackClientId)
        val del = SQuery.newQuery()
            .expr(
                MxInfoDBContract.Weather.DT,
                SQuery.Op.LT,
                (System.currentTimeMillis() / 1000).toFloat().roundToInt()
            )
            .delete(MxInfoDBContract.Weather.CONTENT_URI, false)
        Timber.d("deleted :%s", del)
        val weatherClient = OpenWeather()
        try {
            val daysAvail = SQuery.newQuery()
                .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "D")
                .expr(MxInfoDBContract.Weather.TRACK_CLIENT_ID, SQuery.Op.EQ, args.trackClientId)
                .count(MxInfoDBContract.Weather.CONTENT_URI)
            if (daysAvail <= DAYS_DOWNLOAD - 2 && trackRec != null) {
                //first, go to MXServer
                weatherCached = getWeatherFromMX(args.trackClientId)
                val requestNewFromInternet =
                    weatherCached!!.startsWith(" ") // blank means new data request
                weatherCached = weatherCached.trim { it <= ' ' }
                if (weatherCached == EMPTY_RESULT_FROM_MX) {
                    requestNewDataFromInternet(context, args, gson, trackRec, weatherClient)
                } else {
                    try {
                        val resp = gson.fromJson(weatherCached, GetWeatherDailyResult::class.java)
                        storeResult2DB(context, args.trackClientId, gson, resp)
                        if (requestNewFromInternet && showWeather) {
                            requestNewDataFromInternet(context, args, gson, trackRec, weatherClient)
                        }
                    } catch (_: Exception) {
                        val resp2 = gson.fromJson(weatherCached, GetWeatherDaily2Result::class.java)
                        storeResult2DBv2(context, args.trackClientId, gson, resp2)
                        if (requestNewFromInternet && showWeather) {
                            requestNewDataFromInternet(context, args, gson, trackRec, weatherClient)
                        }
                    }
                }
            } else {
                Timber.d("weather ok days:%s", daysAvail)
            }
            SQuery.newQuery()
                .expr(
                    MxInfoDBContract.Weather.DT,
                    SQuery.Op.LT,
                    (System.currentTimeMillis() / 1000).toFloat().roundToInt()
                )
                .delete(MxInfoDBContract.Weather.CONTENT_URI)
        } catch (e: ServiceException) {
            if (isAdminOrDebug) {
                Toast.makeText(context.applicationContext, "${this.javaClass.name} ${e.message}", Toast.LENGTH_LONG).show()
            }
            val track = trackRec!!.trackname
            Timber.w("OpWeatherCachedOperation/ServiceException '$track' ${Log.getStackTraceString(e)}")
            return OperationResult.error(e)
        } catch (_: JsonSyntaxException) {
            return OperationResult.ok(bundle)
        } catch (e: Exception) {
            if (isAdminOrDebug) {
                Toast.makeText(context.applicationContext, "${this.javaClass.name} ${e.message}", Toast.LENGTH_LONG).show()
            }
            val track = if (trackRec != null) trackRec.trackname else trackRec?.restId.toString() + ""
            Timber.w("OpWeatherCachedOperation/Exception '$track' ${Log.getStackTraceString(e)}")
            return OperationResult.error(e)
        }
        return OperationResult.ok(bundle)
    }

    @Throws(Exception::class)
    private fun requestNewDataFromInternet(
        context: OperationContext,
        args: Args,
        gson: Gson,
        trackRec: TracksRecord,
        weatherClient: OpenWeather
    ) { //require new data and send it to server
        Timber.d("read weather from Internet %s", args.trackClientId)
        val request = GetWeatherDailyRequest(WEATHER_SERVER_VERSION)
        val requestAnonym = GetWeatherDailyAnonymRequest(WEATHER_SERVER_VERSION)
        request.setCntParam(DAYS_DOWNLOAD)
        request.setAPPIDParam(WEATHER_ID)
        request.setLangParam(Locale.getDefault().language)
        requestAnonym.setCntParam(DAYS_DOWNLOAD)
        requestAnonym.setLangParam(Locale.getDefault().language)
        if (MxPreferences.getInstance().unitsKm) {
            request.setUnitsParam(METRIC)
            requestAnonym.setUnitsParam(METRIC)
        } else {
            request.setUnitsParam(IMPERIAL)
            requestAnonym.setUnitsParam(IMPERIAL)
        }
        request.setLatParam(SecHelper.entcryptXtude(trackRec.latitude))
        request.setLonParam(SecHelper.entcryptXtude(trackRec.longitude))
        requestAnonym.setLatParam(SecHelper.entcryptXtude(trackRec.latitude))
        requestAnonym.setLonParam(SecHelper.entcryptXtude(trackRec.longitude))
        val response = getWeatherFromInternetWithID(weatherClient, request)
        var resAll: String? = null
        var anonym = true
        // first with ID
        if (response != null) {
            try {
                resAll = response.readAsText()
                val resp = response.parse()
                storeResult2DB(context, args.trackClientId, gson, resp)
                anonym = false
            } catch (e: ServiceException) {
                Timber.e(e)
            }
        }
        try {
            if (anonym && ANONYM_ENABLED) {
                // then anonymous
                val resultAnonymous = getWeatherFromInternetAnonym(weatherClient, requestAnonym)
                resAll = resultAnonymous!!.readAsText()
                val anonymousParse = resultAnonymous.parse()
                storeResult2DBAnonym(context, args.trackClientId, gson, anonymousParse)
            }
            postWeatherToMX(args.trackClientId, resAll)
        } catch (e: ServiceException) {
            Timber.w("$resAll ${e.message}")
        }
    }

    private fun postWeatherToMX(trackClientId: Long, content: String?) {
        val trackRecord = TracksRecord.get(trackClientId)
        if (trackRecord == null) {
            Timber.w("empty track")
            return
        }
        if (content.isNullOrEmpty()) {
            Timber.e("No weather content for $trackClientId")
            return
        }
        try {
            val webClient = mxInfo
            val requestBody = PostWeatherRequest(
                trackRecord.restId,
                if (MxPreferences.getInstance().unitsKm) "metric" else "imperial",
                Locale.getDefault().toString(),
                content
            )
            val result = webClient.postWeather(requestBody)
            val res = result.parse()
            Timber.d(if (res == null) "null" else "%s", res!!.value)
        } catch (e: ServiceException) {
            Timber.e(e)
        }
    }

    private fun storeResult2DB(
        context: OperationContext,
        trackClientId: Long,
        gson: Gson,
        resp: GetWeatherDailyResult?
    ) {
        if (resp != null && resp.list != null) {
            for (weatherDay in resp.list) {
                var weatherRec = SQuery.newQuery()
                    .expr(MxInfoDBContract.Weather.DT, SQuery.Op.EQ, weatherDay.dt)
                    .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "D")
                    .expr(MxInfoDBContract.Weather.TRACK_CLIENT_ID, SQuery.Op.EQ, trackClientId)
                    .selectFirst<WeatherRecord>(MxInfoDBContract.Weather.CONTENT_URI)
                if (weatherRec == null) {
                    weatherRec = WeatherRecord()
                }
                weatherRec.dt = weatherDay.dt
                weatherRec.type = "D"
                weatherRec.content = gson.toJson(weatherDay)
                weatherRec.trackClientId = trackClientId
                weatherRec.save(false)
            }
        }
        context.applicationContext.contentResolver.notifyChange(
            MxInfoDBContract.Weather.CONTENT_URI,
            null
        )
    }

    private fun storeResult2DBv2(
        context: OperationContext,
        trackClientId: Long,
        gson: Gson,
        resp: GetWeatherDaily2Result?
    ) {
        if (resp != null && resp.list != null) {
            for (weatherDay in resp.list) {
                var weatherRec = SQuery.newQuery()
                    .expr(MxInfoDBContract.Weather.DT, SQuery.Op.EQ, weatherDay.dt)
                    .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "D")
                    .expr(MxInfoDBContract.Weather.TRACK_CLIENT_ID, SQuery.Op.EQ, trackClientId)
                    .selectFirst<WeatherRecord>(MxInfoDBContract.Weather.CONTENT_URI)
                if (weatherRec == null) {
                    weatherRec = WeatherRecord()
                }
                weatherRec.dt = weatherDay.dt.toLong()
                weatherRec.type = "D"
                weatherRec.content = gson.toJson(weatherDay)
                weatherRec.trackClientId = trackClientId
                weatherRec.save(false)
            }
        }
        context.applicationContext.contentResolver.notifyChange(
            MxInfoDBContract.Weather.CONTENT_URI,
            null
        )
    }

    private fun storeResult2DBAnonym(
        context: OperationContext,
        trackClientId: Long,
        gson: Gson,
        resp: GetWeatherDailyAnonymResult
    ) {
        for (weatherDay in resp.list) {
            var weatherRec = SQuery.newQuery()
                .expr(MxInfoDBContract.Weather.DT, SQuery.Op.EQ, weatherDay.dt)
                .expr(MxInfoDBContract.Weather.TYPE, SQuery.Op.EQ, "D")
                .expr(MxInfoDBContract.Weather.TRACK_CLIENT_ID, SQuery.Op.EQ, trackClientId)
                .selectFirst<WeatherRecord>(MxInfoDBContract.Weather.CONTENT_URI)
            if (weatherRec == null) {
                weatherRec = WeatherRecord()
            }
            weatherRec.dt = weatherDay.dt
            weatherRec.type = "D"
            weatherRec.content = gson.toJson(weatherDay)
            weatherRec.trackClientId = trackClientId
            weatherRec.save(false)
        }
        context.applicationContext.contentResolver.notifyChange(
            MxInfoDBContract.Weather.CONTENT_URI,
            null
        )
    }

    private fun getWeatherFromMX(trackClientId: Long): String? {
        Timber.d("read weather from MX %s", trackClientId)
        val trackRecord = TracksRecord.get(trackClientId)
            ?: return EMPTY_RESULT_FROM_MX
        return try {
            val units = if (MxPreferences.getInstance().unitsKm) "metric" else "imperial"
            val picturesResponse = dataManagerCore
                .getWeather4TrackSync(trackRecord.restId, units, Locale.getDefault().toString())
            picturesResponse.checkResponseCodeOk()
            var jsonString = picturesResponse.body()
            if (jsonString == null) {
                jsonString = EMPTY_RESULT_FROM_MX
            }
            jsonString
        } catch (e: IOException) {
            Timber.e("$trackClientId ${e.message}")
            EMPTY_RESULT_FROM_MX
        }
    }

    /**
     * Versuch es X-Mal
     *
     * @param weatherClient WebClient
     * @param request       Request
     * @return a
     */
    @Throws(Exception::class)
    private fun getWeatherFromInternetWithID(
        weatherClient: OpenWeather,
        request: GetWeatherDailyRequest
    ): Response<GetWeatherDailyResult>? {
        var response: Response<GetWeatherDailyResult>? = null
        for (i in 0..MAX_LOOP) {
            try {
                response = weatherClient.getWeatherDaily(request)
                response.checkResponseCodeOk()
                break
            } catch (e: ServiceException) {
                var resp = ""
                if (response != null) {
                    resp = response.readAsText()
                }
                Timber.w("${e.message} $resp")
                //                if (i == MAX_LOOP) {
                //                    throw new ServiceException(e);
                //                }
            } catch (e: Exception) {
                var resp = ""
                if (response != null) {
                    resp = response.readAsText()
                }
                if (response!!.responseCode == Response.HTTP_UNAUTHORIZED) {
                    if (!alreadySent401) {
                        Timber.e("${e.message} $resp $WEATHER_ID")
                        alreadySent401 = true
                    }
                } else {
                    Timber.w("${e.message} $resp")
                }
            }
        }
        return response
    }

    /**
     * Versuch es X-Mal
     *
     * @param weatherClient WebClient
     * @param request       Request
     * @return a
     */
    @Throws(Exception::class)
    private fun getWeatherFromInternetAnonym(
        weatherClient: OpenWeather,
        request: GetWeatherDailyAnonymRequest
    ): Response<GetWeatherDailyAnonymResult>? {
        var response: Response<GetWeatherDailyAnonymResult>? = null
        for (i in 0..MAX_LOOP) {
            try {
                response = weatherClient.getWeatherDailyAnonym(request)
                response.checkResponseCodeOk()
                break
            } catch (e: ServiceException) {
                Timber.e(e)
                if (i == MAX_LOOP) {
                    throw ServiceException(e)
                }
            } catch (e: Exception) {
                if (i == MAX_LOOP) {
                    if (response!!.responseCode == Response.HTTP_UNAUTHORIZED) {
                        if (!alreadySent401) {
                            alreadySent401 = true
                            throw Exception("$e count:$i")
                        }
                    } else {
                        throw Exception("$e count:$i")
                    }
                }
            }
        }
        return response
    }

    companion object {
        private const val WEATHER_SERVER_VERSION = 2.5
        private const val WEATHER_ID = "c5fa1c913f5bfc950f1638d84d076d3f"
        private const val METRIC = "metric"
        private const val IMPERIAL = "imperial"
        private const val DAYS_DOWNLOAD = 16
        private const val MAX_LOOP = 2
        private const val EMPTY_RESULT_FROM_MX = "{}"
        private const val ANONYM_ENABLED = false
        private var alreadySent401 = false
    }
}
