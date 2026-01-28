package info.mx.core.ops.google

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.net.ServiceException
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.commonlib.NetworkHelper.isOnline
import info.hannes.commonlib.TrackingApplication.Companion.isDebug
import info.mx.core.MxCoreApplication.Companion.isAdminOrDebug
import info.mx.tracks.R
import info.mx.tracks.common.SecHelper
import info.mx.core.ops.ImportHelper.getDistance
import info.mx.core_generated.rest.GetRouteRequest
import info.mx.core.rest.Google
import info.mx.core.rest.google.Routes
import info.mx.core_generated.ops.google.AbstractOpGetRouteOperation
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.core_generated.sqlite.RouteRecord
import info.mx.core_generated.sqlite.TracksRecord
import timber.log.Timber

class OpGetRouteOperation : AbstractOpGetRouteOperation() {
    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        val gson = Gson()
        if (isOnline(context.applicationContext)) {
            deleteOldRoutes()
            val routeClient = Google(isDebug)
            try {
                var distance = DISTANCE_TO_KEEP_ROUTE
                var routeRec = SQuery.newQuery()
                    .expr(MxInfoDBContract.Route.TRACK_CLIENT_ID, SQuery.Op.EQ, args.trackClientId)
                    .selectFirst<RouteRecord>(MxInfoDBContract.Route.CONTENT_URI)
                val locCalcTo = Location("xyz")
                locCalcTo.latitude = args.lat
                locCalcTo.longitude = args.lon
                if (routeRec != null) {
                    distance = getDistance(locCalcTo, routeRec.latitude, routeRec.longitude)
                    Timber.d("New distance $distance trackClientId=${args.trackClientId}")
                }
                val trackRecord = TracksRecord.get(args.trackClientId)
                if (trackRecord != null && distance >= DISTANCE_TO_KEEP_ROUTE) {
                    val request = GetRouteRequest()
                    request.setKeyParam(context.applicationContext.getString(R.string.GOOGLE_MAP_API_KEY))
                    request.setOriginParam(args.lat.toString() + "," + args.lon)
                    request.setDestinationParam(
                        SecHelper.entcryptXtude(trackRecord.latitude).toString() + "," +
                                SecHelper.entcryptXtude(trackRecord.longitude)
                    )
                    request.setSensorParam("false")
                    Timber.d("get new route for trackClientId=${args.trackClientId}")
                    val response = routeClient.getRoute(request)
                    val routeString = response.readAsText()
                    val routes = gson.fromJson(routeString, Routes::class.java)
                    if (routes != null &&
                        routes.status != null &&
                        (routes.status == "OK" || routes.status == "ZERO_RESULTS")
                    ) {
                        if (routeRec == null) {
                            routeRec = RouteRecord()
                        }
                        routeRec.content = routeString
                        routeRec.trackClientId = args.trackClientId
                        routeRec.latitude = args.lat
                        routeRec.longitude = args.lon
                        routeRec.created = System.currentTimeMillis()
                        routeRec.save(true)
                    }
                }
            } catch (e: ServiceException) {
                if (isAdminOrDebug) {
                    Toast.makeText(context.applicationContext, "${this.javaClass.name} ${e.message}", Toast.LENGTH_LONG).show()
                }
                Timber.e(e)
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

    companion object {
        private const val MINUTES_TO_KEEP_ROUTE = 30
        private const val DISTANCE_TO_KEEP_ROUTE: Long = 3500
        fun deleteOldRoutes() {
            // by time
            val deleted = SQuery.newQuery()
                .expr(
                    MxInfoDBContract.Route.CREATED,
                    SQuery.Op.LTEQ,
                    System.currentTimeMillis() - 1000 * 60 * MINUTES_TO_KEEP_ROUTE
                )
                .delete(MxInfoDBContract.Route.CONTENT_URI)
            Timber.d("deleted:%s", deleted)
        }
    }
}
