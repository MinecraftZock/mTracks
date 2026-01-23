package info.hannes.mechadminGen.ops.mxcal

import android.os.Bundle
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.net.UnexpectedHttpStatusException
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadmin.WebClient
import info.hannes.mechadminGen.ops.ImportDataHelper
import info.hannes.mechadminGen.ops.ImportDataHelper.ImportMxCalResult
import info.hannes.mechadminGen.rest.GetSeriesRequest
import info.hannes.mechadminGen.sqlite.MxCalContract.MXSerie
import info.hannes.mechadminGen.sqlite.QuellFileRecord
import timber.log.Timber
import java.util.*

internal class OpMxCalLoadSearchOperation : AbstractOpMxCalLoadSearchOperation() {

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val webClient = WebClient.mxCal!!
        val idList: MutableList<Long> = ArrayList()
        val serie = 0
        val seriesTrack = 0
        var quellFile: QuellFileRecord? = null
        var impRes: ImportMxCalResult? = ImportMxCalResult()
        return try {
            LoggingHelperAdmin.setMessage("http://mxcal.com/api/v1/series/search.js")
            val response = webClient.search
            // Throws UnexpectedHttpStatusException if not 200 OK
            response.checkResponseCodeOk()
            val result = response.parse()
            quellFile = QuellFileRecord()
            quellFile.content = response.readAsText()
            quellFile.url = "http://mxcal.com/api/v1/series/search.js"
            quellFile.save()

            // Main
            for (serieWeb in result.series) {
                idList.add(serieWeb.id.toLong())
                impRes = ImportDataHelper.importMXSerieFromHttp(impRes, serieWeb, quellFile)
            }
            if (args.alle) {
                idList.clear()
                val maxID = SQuery.newQuery().firstLong(MXSerie.CONTENT_URI, "max(" + MXSerie.WEB_ID + ")")
                for (i in 1..maxID) {
                    when (i) {
                        20L, 21L, 3L, 2L -> Unit
                        else -> idList.add(i)
                    }
                }
            }
            for (id in idList) {
                LoggingHelperAdmin.setMessage("http://mxcal.com/api/v1/series/$id.js")
                val req = GetSeriesRequest("$id.js")
                val responseS = webClient.getSeries(req)
                val quellFileS = QuellFileRecord()
                quellFileS.content = responseS.readAsText()
                quellFileS.url = "http://mxcal.com/api/v1/series/$id.js"
                quellFileS.save()
                try {
                    // Throws UnexpectedHttpStatusException if not 200 OK
                    responseS.checkResponseCodeOk()
                    val resultS = responseS.parse()
                    val impResS = ImportMxCalResult()
                    ImportDataHelper.importMXSerieFromHttp(impResS, resultS.serie, quellFileS)
                } catch (_: UnexpectedHttpStatusException) {
                    quellFileS.log = "UnexpectedHttpStatusException"
                    quellFileS.save()
                }
            }
            LoggingHelperAdmin.setMessage("")
            val bundle = Bundle()
            OperationResult.ok(bundle)
        } catch (e: Exception) {
            Timber.e(e)
            if (quellFile != null) {
                quellFile.log = """
                Serie:$serie
                SeriesTrack:$seriesTrack
                Track:$seriesTrack
                ${e.message}
                """.trimIndent()
                quellFile.save()
            }
            LoggingHelperAdmin.setMessage("")
            Toast.makeText(context.applicationContext, e.message, Toast.LENGTH_LONG).show()
            OperationResult.error(e)
        }
    }
}
