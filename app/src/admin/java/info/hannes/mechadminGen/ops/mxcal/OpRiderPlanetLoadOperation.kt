package info.hannes.mechadminGen.ops.mxcal

import android.os.Bundle
import android.util.Xml
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadminGen.ops.ImportDataHelper
import info.hannes.mechadminGen.sqlite.MxCalContract.QuellFile
import info.hannes.mechadminGen.sqlite.QuellFileRecord
import info.hannes.retrofit.ApiAdminClient
import info.hannes.retrofit.service.model.PictureResponse
import info.mx.comlib.retrofit.CommApiClient
import info.mx.tracks.MxAccessApplication.Companion.aadhresUBase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.*
import java.util.*

internal class OpRiderPlanetLoadOperation : AbstractOpRiderPlanetLoadOperation(), KoinComponent {

    val commApiClient: CommApiClient by inject()
    private val apiAdminClient: ApiAdminClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        try {
            if (!args.onlyExtract) {
                // Hole die Länderurl's
                val filename = ImportDataHelper.download2xHtml(args.url, FILE_MAIN)
                val newfile = ImportDataHelper.doTransform2UrlList(
                    context.applicationContext,
                    filename,
                    XSLT_FILE,
                    URLLISTE
                )
                ImportDataHelper.doRemoveContent(newfile, "<url>")
                val urls = parseXmlUrl(newfile)
                SQuery.newQuery().expr(QuellFile.DOWNLOAD_SITE_ID, SQuery.Op.LIKE, args.downLoadId)
                    .delete(QuellFile.CONTENT_URI)
                for (url in urls) {
                    val rec = QuellFileRecord()
                    rec.downloadSiteId = args.downLoadId
                    rec.url = url
                    rec.save()
                }
                LoggingHelperAdmin.setMessage("")
            }
            doPostTrackJsonAllZip("/mnt/sdcard/alljson.zip")
            // String res = extractAllStateUrl(context, args.downLoadId);
            // bundle.putString(RESULT_STR, res);
            // LoggingHelperAdmin.setMessage("");
        } catch (en: NullPointerException) {
            Timber.e(en)
            LoggingHelperAdmin.setMessage("NullPointerException")
            return OperationResult.error(en)
        } catch (e1: IllegalStateException) {
            Timber.e(e1)
            LoggingHelperAdmin.setMessage(e1.message.toString())
            return OperationResult.error(e1)
        } catch (e1: IOException) {
            Timber.e(e1)
        } catch (e: XmlPullParserException) {
            LoggingHelperAdmin.setMessage(e.message.toString())
            return OperationResult.error(e)
        }
        return OperationResult.ok(bundle)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseXmlUrl(filename: String): List<String> {
        var `in`: InputStream? = null
        return try {
            `in` = BufferedInputStream(FileInputStream(filename))
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(`in`, null)
            parser.nextTag()
            readUrl(parser)
        } finally {
            `in`?.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readUrl(parser: XmlPullParser): List<String> {
        val entries: MutableList<String> = ArrayList()
        parser.require(XmlPullParser.START_TAG, ns, "top")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            val `val` = readText(parser)
            // Starts by looking for the url tag
            if (name == "url") {
                entries.add(`val`)
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    private fun doPostTrackJsonAllZip(jsonfile: String): PictureResponse? {
        val start = System.currentTimeMillis()
        val zipFile = File(jsonfile)
        val basic = aadhresUBase

        val fileReqBody: RequestBody = zipFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", zipFile.name, fileReqBody)

        val call = apiAdminClient.adminService
            .postJsonZip(filePart, basic)
        try {
            val result = call.execute()
            zipFile.delete()
            return result.body()
        } catch (e: IOException) {
            Timber.e(e)
        }
        Timber.d("doPostTrackZusatz ${System.currentTimeMillis() - start}ms")
        return null
    }

    companion object {
        private const val URLLISTE = "urlliste.xml"
        private val ns: String? = null
        private const val FILE_MAIN = "riderplanet.xml"
        const val RESULT_STR = "RESULTSTR"
        private const val XSLT_FILE = "rider_urls.xsl"
    }
}
