package info.hannes.mechadmin_gen.ops.mxcal

import android.os.Bundle
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadmin.WebClient
import info.hannes.mechadmin_gen.ops.ImportDataHelper
import info.hannes.mechadmin_gen.sqlite.MXSerieRecord
import info.hannes.mechadmin_gen.sqlite.MxCalContract.*
import info.hannes.mechadmin_gen.sqlite.MxSeriesTrackRecord
import info.hannes.mechadmin_gen.sqlite.MxTrackRecord
import info.hannes.mechadmin_gen.sqlite.QuellFileRecord
import info.hannes.retrofit.ApiAdminClient
import info.mx.tracks.util.Wait.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class OpMxCallPush2ServerOperation : AbstractOpMxCallPush2ServerOperation(), KoinComponent {

    private val apiAdminClient: ApiAdminClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val webClient = WebClient.mxCal!!
        return try {
            // DownloadSource
            val dowResp = webClient.mXcalQuellfileLastId
            val dowResult = dowResp.parse()
            dowResp.checkResponseCodeOk()
            if (dowResult.calStatusResponse.message == null || dowResult.calStatusResponse.message == "") {
                val tmp = SQuery.newQuery()
                    .expr(QuellFile.CREATEDATE, SQuery.Op.GT, dowResult.calStatusResponse.changed)
                    .or()
                    .append(QuellFile.REST_ID + " is null")
                val downloads = SQuery.newQuery()
                    .expr(tmp)
                    .expr(QuellFile.UPDATED_COUNT, SQuery.Op.GT, 0)
                    .select<QuellFileRecord>(QuellFile.CONTENT_URI, QuellFile.CREATEDATE)
                for (download in downloads) {
                    ImportDataHelper.SendDownLoadSite2ServerMultipart(
                        apiAdminClient,
                        download,
                        args.basic
                    )
                    delay()
                }
            } else {
                throw Exception("resp:" + dowResult.calStatusResponse.message)
            }

            // Track
            val traResp = webClient.mXcalTrackLastId
            val traResult = traResp.parse()
            traResp.checkResponseCodeOk()
            if (traResult.calStatusResponse.message == null || traResult.calStatusResponse.message == "") {
                val tmp = SQuery.newQuery()
                    .expr(MxTrack.UPDATED_AT, SQuery.Op.GT, traResult.calStatusResponse.changed)
                    .or()
                    .append(MxTrack.REST_ID + " is null")
                val tracks = SQuery.newQuery()
                    .expr(tmp)
                    .select<MxTrackRecord>(MxTrack.CONTENT_URI, MxTrack.CREATED_AT)
                for (track in tracks) {
                    ImportDataHelper.SendMXTrack2Server(webClient, track)
                    delay()
                }
            } else {
                throw Exception("resp:" + traResult.calStatusResponse.message)
            }

            // Serie
            val serResp = webClient.mXcalSerieLastId
            val serResult = serResp.parse()
            serResp.checkResponseCodeOk()
            if (serResult.calStatusResponse.message == null || serResult.calStatusResponse.message == "") {
                val tmp = SQuery.newQuery()
                    .expr(MXSerie.UPDATED_AT, SQuery.Op.GT, serResult.calStatusResponse.changed)
                    .or()
                    .append(MXSerie.REST_ID + " is null")
                val series = SQuery.newQuery()
                    .expr(tmp)
                    .select<MXSerieRecord>(MXSerie.CONTENT_URI, MXSerie.CREATED_AT)
                for (serie in series) {
                    ImportDataHelper.SendMXSerie2Server(webClient, serie)
                    delay()
                }
            } else {
                throw Exception("resp:" + serResult.calStatusResponse.message)
            }

            // SeriesTrack
            val sertraResp = webClient.mXcalSeriestrackLastId
            val sertraResult = sertraResp.parse()
            sertraResp.checkResponseCodeOk()
            if (sertraResult.calStatusResponse.message == null || sertraResult.calStatusResponse.message == "") {
                val tmp = SQuery.newQuery().expr(
                    MxSeriesTrack.UPDATED_AT,
                    SQuery.Op.GT,
                    sertraResult.calStatusResponse.changed
                )
                    .or()
                    .append(MxSeriesTrack.REST_ID + " is null")
                val sertracks = SQuery.newQuery()
                    .expr(tmp)
                    .select<MxSeriesTrackRecord>(MxSeriesTrack.CONTENT_URI, MxSeriesTrack._ID)
                for (sertrack in sertracks) {
                    ImportDataHelper.SendMXSeriesTrack2Server(webClient, sertrack)
                    delay()
                }
            } else {
                throw Exception("resp:" + sertraResult.calStatusResponse.message)
            }
            LoggingHelperAdmin.setMessage("")
            val bundle = Bundle()
            OperationResult.ok(bundle)
        } catch (e: Exception) {
            LoggingHelperAdmin.setMessage("")
            val msg = if (e.message == null) "NullPointerException" else e.message!!
            Toast.makeText(context.applicationContext, msg, Toast.LENGTH_LONG).show()
            OperationResult.error(e)
        }
    }
}