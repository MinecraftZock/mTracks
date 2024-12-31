package info.hannes.mechadmin_gen.ops.mxcal

import android.os.Bundle
import android.os.Environment
import android.util.Xml
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadmin_gen.ops.ImportDataHelper
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.TrackssmallRid
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.TrackstageRid
import info.hannes.mechadmin_gen.sqlite.MxCalContract.QuellFile
import info.hannes.mechadmin_gen.sqlite.MxCalContract.QuellFileSmall
import info.hannes.mechadmin_gen.sqlite.QuellFileRecord
import info.hannes.mechadmin_gen.sqlite.QuellFileSmallRecord
import info.hannes.mechadmin_gen.sqlite.TrackssmallRidRecord
import info.hannes.mechadmin_gen.sqlite.TrackstageRidRecord
import info.hannes.retrofit.ApiAdminClient
import info.hannes.retrofit.service.model.PictureResponse
import info.mx.comlib.retrofit.CommApiClient
import info.mx.comlib.retrofit.service.model.RiderTrack
import info.mx.comlib.util.RetroFileHelper
import info.mx.tracks.MxAccessApplication.Companion.aadhresUBase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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
            LoggingHelperAdmin.setMessage(e1.message)
            return OperationResult.error(e1)
        } catch (e1: IOException) {
            Timber.e(e1)
        } catch (e: XmlPullParserException) {
            LoggingHelperAdmin.setMessage(e.message)
            return OperationResult.error(e)
        }
        return OperationResult.ok(bundle)
    }

    // public List<TrackstageRecord> parseXmlTracks(String content) throws XmlPullParserException, IOException {
    // InputStream in = null;
    // try {
    // in = new ByteArrayInputStream(content.getBytes());
    // XmlPullParser parser = Xml.newPullParser();
    // parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
    // parser.setInput(in, null);
    // parser.nextTag();
    // // return readTrackstagePlain(parser);
    // return readTrackstage(parser);
    // } finally {
    // if (in != null) {
    // in.close();
    // }
    // }
    // }
    // private List<TrackstageRecord> readTrackstage(XmlPullParser parser) throws XmlPullParserException, IOException {
    // List<TrackstageRecord> entries = new ArrayList<TrackstageRecord>();
    //
    // // parser.require(XmlPullParser.START_TAG, ns, "tracks");
    // while (parser.next() != XmlPullParser.END_TAG) {
    // if (parser.getEventType() != XmlPullParser.START_TAG) {
    // Log.d(TAG, "readTrackstage " + parser.getEventType() + "");
    // continue;
    // }
    // String name = parser.getName();
    // Log.d(TAG, "readTrackstage " + parser.getEventType() + " " + name);
    // // String val = readText(parser);
    // // Starts by looking for the track tag
    // if (name.equals("track")) {
    // entries.add(readTrackEntry(parser));
    // } else {
    // skip(parser);
    // }
    // }
    // return entries;
    // }
    // // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // // to their respective "read" methods for processing. Otherwise, skips the tag.
    // private TrackstageRecord readTrackEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
    // // parser.require(XmlPullParser.START_TAG, ns, "track");
    // TrackstageRecord record = new TrackstageRecord();
    // while (parser.next() != XmlPullParser.END_TAG) {
    // // if (parser.getEventType() != XmlPullParser.START_TAG) {
    // // continue;
    // // }
    // String name = parser.getName();
    // Log.d(TAG, "readTrackEntry " + parser.getEventType() + " " + name);
    // if (name.equals("trackname")) {
    // record.setTrackname(readElement(parser, "trackname"));
    // } else if (name.equals("count")) {
    // long anz = readElementL(parser, "count");
    // if (anz > countMax) {
    // countMax = anz;
    // Log.i(TAG, record.getTrackname() + " hat maximal erreicht " + countMax);
    // }
    // } else if (name.equals("camping")) {
    // record.setCamping(readElementL(parser, "camping"));
    // } else if (name.equals("campingRVhookups")) {
    // record.setCampingRVhookups(readElementL(parser, "campingRVhookups"));
    // } else if (name.equals("singleTrack")) {
    // record.setSingleTrack(readElementL(parser, "singleTrack"));
    // } else if (name.equals("MXTrack")) {
    // record.setMXTrack(readElementL(parser, "MXTrack"));
    // } else if (name.equals("a4X4")) {
    // record.setA4X4(readElementL(parser, "a4X4"));
    // } else if (name.equals("UTV")) {
    // record.setUTV(readElementL(parser, "UTV"));
    // } else if (name.equals("Quad")) {
    // record.setQuad(readElementL(parser, "Quad"));
    // } else if (name.equals("rating")) {
    // record.setRating(readElementL(parser, "rating"));
    // } else if (name.equals("trackstatus")) {
    // record.setTrackstatus(getTrackStatus(readElement(parser, "trackstatus")));
    // } else if (name.equals("areatype")) {
    // record.setAreatype(readElementL(parser, "areatype"));
    // } else if (name.equals("schwierigkeit")) {
    // record.setSchwierigkeit(readElementL(parser, "schwierigkeit"));
    // } else if (name.equals("shower")) {
    // record.setShower(readElementL(parser, "shower"));
    // } else if (name.equals("detail")) {
    // record.setUrlDetailXml(readElement(parser, "detail"));
    // } else if (name.equals("mapinfo")) {
    // record.setUrlMapPointsJson(readElement(parser, "mapinfo"));
    // } else {
    // skip(parser);
    // }
    // }
    // Log.d(TAG, "readTrackEntry " + parser.getEventType() + " END");
    // record.save();
    // return record;
    // }
    //
    // private long getTrackStatus(String readElement) {
    // long res = 0;
    // if (readElement.equals("Open")) {
    // res = 1;
    // } else if (readElement.equals("Closed")) {
    // res = 2;
    // } else if (readElement.equals("Closed For Season")) {
    // res = 3;
    // } else {
    // }
    // return res;
    // }
    //
    // private long readElementL(XmlPullParser parser, String element) throws NumberFormatException, IOException, XmlPullParserException {
    // String val = readElement(parser, element);
    // return Long.parseLong(val.equals("") ? "0" : val);
    // }
    //
    // private String readElement(XmlPullParser parser, String element) throws IOException, XmlPullParserException {
    // // parser.require(XmlPullParser.START_TAG, ns, element);
    // String title = readText(parser);
    // // parser.require(XmlPullParser.END_TAG, ns, element);
    // return title;
    // }
    @Throws(
        IllegalStateException::class,
        IOException::class,
        XmlPullParserException::class,
        JSONException::class
    )
    private fun extractAllStateUrl(downLoadId: Long): String {
        var serverUrl = SQuery.newQuery()
            .expr(QuellFile.DOWNLOAD_SITE_ID, SQuery.Op.LIKE, downLoadId)
            .firstString(QuellFile.CONTENT_URI, QuellFile.URL)
        serverUrl = serverUrl.substring(0, serverUrl.lastIndexOf("/") + 1)
        var filesSmall = SQuery.newQuery() // .expr(QuellFile.DOWNLOAD_SITE_ID, Op.LIKE, downLoadId)
            // .expr(or)
            // .append(QuellFile.REST_ID + " is null")
            .append(QuellFileSmall.CONTENT + " = ''")
            .select<QuellFileSmallRecord>(
                QuellFileSmall.CONTENT_URI,
                QuellFileSmall.CREATEDATE + " desc"
            )

        // Liste der States downloaden (mit VPN)
        for (fileRecord in filesSmall) {
            val xmlCont = RetroFileHelper.getFileContent(commApiClient, fileRecord.url)
            val qfile = QuellFileRecord.get(fileRecord.id)
            qfile.content = xmlCont
            qfile.downloadSiteId = downLoadId
            qfile.save()
        }

        // States auf Server pushen (ohne VPN)
        filesSmall = SQuery.newQuery()
            .append(QuellFileSmall.CONTENT + " <> ''")
            .select(QuellFileSmall.CONTENT_URI, QuellFileSmall.CREATEDATE + " desc")
        // Liste der States downloaden und posten
        for (fileRecord in filesSmall) {
            val filename = ImportDataHelper.getFileToken(fileRecord.url)
            LoggingHelperAdmin.setMessage("po $filename")
            val qfile = QuellFileRecord.get(fileRecord.id)
            val res = doPostStateXML(qfile.content, filename)
            LoggingHelperAdmin.setMessage("p1 $filename")
            if (res!!.size == 0) {
                Timber.e(fileRecord.url + " keine Sätze")
            } else {
                Timber.d(fileRecord.url + " " + res.size + " Sätze")
            }

            // urls zum Server pushen
            for (track in res) {
                val trackStageRec = TrackstageRidRecord()
                trackStageRec.urlDetailXml = serverUrl + track.urldetail
                trackStageRec.urlMapPointsJson = serverUrl + track.urlmappoint
                trackStageRec.restId = track.id.toLong()
                trackStageRec.save()
            }
            // damit es nicht 2x übertragen wird
            if (res.size > 0) {
                qfile.updatedCount = res.size.toLong()
                qfile.save()
            }
            LoggingHelperAdmin.setMessage("pf $filename")
        }

        // jetzt Details downloaden (mit VPN)
        val tracks = SQuery.newQuery()
            .append(TrackstageRid.REST_ID + " is null")
            .append(TrackstageRid.URL_DETAIL_XML + " <> ''")
            .append(TrackstageRid.CONTENT_DETAIL_XML + " is null")
            .append(TrackstageRid.CONTENT_MAP_POINTS_JSON + " is null")
            .select<TrackstageRidRecord>(TrackstageRid.CONTENT_URI, TrackstageRid._ID)
        // Liste der States downloaden und posten
        var i = 0
        for (track in tracks) {
            i++
            LoggingHelperAdmin.setMessage(i.toString() + "/" + tracks.size + " Detail download")
            val jsonMap = getMapJSON(track.urlMapPointsJson)
            track.contentMapPointsJson = jsonMap
            track.contentDetailXml = RetroFileHelper.getFileContent(commApiClient, track.urlDetailXml)
            track.save()
        }

        // jetzt Details zum Server pushen (ohne VPN)
        // "REST_ID is null" nur durch einen Bug beim Saugen vergessen wurde die REST:D zu setzen. dh kann man weglassen
        val orQuery = SQuery.newQuery().append(TrackssmallRid.REST_ID + " is null").or()
            .expr(TrackssmallRid.REST_ID, SQuery.Op.LT, 0)
        val trackssmall = SQuery.newQuery()
            .expr(orQuery)
            .append(TrackssmallRid.LEN_X_M_L + " > 0")
            .append(TrackssmallRid.LEN_J_S_O_N + " > 0")
            .select<TrackssmallRidRecord>(TrackssmallRid.CONTENT_URI, TrackssmallRid._ID)
        // Tracks posten
        i = 0
        var insert = 0
        var manuell = 0
        var update = 0
        var multi = 0
        var error = 0
        for (trackS in trackssmall) {
            i++
            LoggingHelperAdmin.setMessage(trackS.id.toString() + " push " + i + "/" + trackssmall.size)
            val track = TrackstageRidRecord.get(trackS.id)
            if (trackS.restId > 0 || track.urlMapPointsJson != null) {
                val resPost = doPostTrackZusatz(
                    track.contentDetailXml, track.contentMapPointsJson,
                    track.restId,
                    track.urlMapPointsJson
                )
                if (resPost != null && resPost.message == "") {
                    // TODO
                    track.restId = resPost.id.toLong()
                    track.save()
                    if (resPost.aktion == "insert") {
                        insert++
                    } else if (resPost.aktion == "manuell") {
                        manuell++
                    } else if (resPost.aktion == "update") {
                        update++
                    } else if (resPost.aktion!!.startsWith("multiple")) {
                        multi++
                        Timber.d(resPost.aktion)
                    }
                } else {
                    Timber.e(if (resPost == null) "null" else resPost.message)
                    error++
                }
            } else {
                Timber.w(trackS.id.toString() + " ausgelassen")
            }
        }
        LoggingHelperAdmin.setMessage("")
        Timber.d("insert:$insert")
        Timber.d("manuell:$manuell")
        Timber.d("update:$update")
        Timber.d("multi:$multi")
        Timber.d("finish")
        return """
             insert:$insert
             manuell:$manuell
             multi:$multi
             error:$error
             update:$update
             """.trimIndent()
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

    @Throws(IllegalStateException::class, IOException::class, JSONException::class)
    private fun getMapJSON(urlmappoint: String): String {
        var content = RetroFileHelper.getFileContent(commApiClient, urlmappoint)
        content = content.substring(content.indexOf(MAPJSON) + MAPJSON.length - 3)
        content = content.substring(0, content.indexOf(MAPJSON_END))

        // das *.js herunterladen
        val newUrl = urlmappoint.substring(0, urlmappoint.lastIndexOf("/") + 1) + content
        content = RetroFileHelper.getFileContent(commApiClient, newUrl)
        content = content.replace("var gobjRAC = ", "")
            .replace("guiRAGUID".toRegex(), "\"guiRAGUID\"")
            .replace("intRAID".toRegex(), "\"intRAID\"")
            .replace("strName".toRegex(), "\"strName\"")
            .replace("strAreaTypeSummary".toRegex(), "\"strAreaTypeSummary\"")
            .replace("strLandTypeSummary".toRegex(), "\"strLandTypeSummary\"")
            .replace("strNearestCity".toRegex(), "\"strNearestCity\"")
            .replace("strDirectionsOrigin".toRegex(), "\"strDirectionsOrigin\"")
            .replace("strStateCode".toRegex(), "\"strStateCode\"")
            .replace("strStateName".toRegex(), "\"strStateName\"")
            .replace("arrMarkerDefs".toRegex(), "\"arrMarkerDefs\"")
            .replace("dblLat".toRegex(), "\"dblLat\"")
            .replace("dblLng".toRegex(), "\"dblLng\"")
            .replace("dblCenterLat".toRegex(), "\"dblCenterLat\"")
            .replace("dblCenterLng".toRegex(), "\"dblCenterLng\"")
            .replace("intZoomLevel".toRegex(), "\"intZoomLevel\"")
            .replace("strIcon".toRegex(), "\"strIcon\"")
            .replace("\"strIcon\"Path".toRegex(), "\"strIconPath\"")
            .replace("strLabel".toRegex(), "\"strLabel\"")
            .replace("strCategory".toRegex(), "\"strCategory\"")
            .replace("strDescription".toRegex(), "\"strDescription\"")
            .replace("guiGUID".toRegex(), "\"guiGUID\"")
            .replace("objTrailMapDef".toRegex(), "\"objTrailMapDef\"")
            .replace("\"".toRegex(), "_")
            .replace("'".toRegex(), "\"")
        // .replaceAll("\"", "'");
        var array = content.substring(content.indexOf("["))
        array = array.substring(0, array.indexOf("]") + 1)
        val jarray = JSONArray(array.replace("\"north".toRegex(), ""))
        // JSONObject obj = new JSONObject(content.replaceAll("'", "\""));
        return jarray.toString()
    }

    /**
     * @param bodyCleanItems
     * @return
     */
    protected fun handleDatum(bodyCleanItems: MutableList<String>): List<String> {
        val itemsDatum: MutableList<String> = ArrayList()
        for (i in bodyCleanItems.indices) {
            if (i < bodyCleanItems.size - 1 && startDatum(bodyCleanItems[i]) && isZeitZeile(
                    bodyCleanItems[i + 1]
                )
            ) {
                if (i < bodyCleanItems.size - 2 && isZeitZeile(bodyCleanItems[i + 2])) {
                    itemsDatum.add(bodyCleanItems[i] + " " + bodyCleanItems[i + 1] + " " + bodyCleanItems[i + 2])
                    bodyCleanItems[i + 1] = ""
                    bodyCleanItems[i + 2] = ""
                } else {
                    itemsDatum.add(bodyCleanItems[i] + " " + bodyCleanItems[i + 1])
                    bodyCleanItems[i + 1] = ""
                }
            } else {
                itemsDatum.add(bodyCleanItems[i])
            }
        }
        return itemsDatum
    }

    private fun isZeitZeile(zeile: String): Boolean {
        val regExDouble = "^[0-9.;]+$"
        val regExInt = "[0-9]+"
        val temp = zeile.replace(" ".toRegex(), "").trim { it <= ' ' } + " "
        val tGes: Double
        if (temp.trim { it <= ' ' }.matches(regExInt.toRegex())) {
            tGes = temp.trim { it <= ' ' }.toDouble()
            if (tGes > 0 && tGes < 23) {
                return true // nur eine Nummer von 1..23
            }
        }
        return if (temp.length == 0 || !(temp.indexOf("-") > 0 && temp.indexOf("-") < 6)) {
            false
        } else {
            val tok1 =
                temp.substring(0, temp.indexOf("-")).replace(":", ".").replace("(", "")
            var tok2 =
                temp.substring(temp.indexOf("-") + 1).replace(":", ".").trim { it <= ' ' }
            tok2 = tok2.substring(0, if (tok2.length < 2) tok2.length else 2)
            var t1 = 0.0
            var t2 = 0.0
            if (tok1.matches(regExDouble.toRegex())) {
                t1 = tok1.toDouble()
            }
            if (tok2.matches(regExDouble.toRegex())) {
                t2 = tok2.toDouble()
            }
            t1 > 8 && tok2 == "" ||  // 14-
                    t1 > 6 && t1 < 20 && t2 > 9 && t2 < 24 || tok2.substring(
                0,
                if (tok2.length < 2) tok2.length else 2
            ) == "to" ||
                    temp.startsWith("call") ||
                    temp.startsWith("all day")
        }
    }

    private fun startDatum(zeile: String): Boolean {
        return zeile.lowercase(Locale.getDefault()).startsWith("open every day") ||
                zeile.lowercase(Locale.getDefault()).startsWith("weekends open") ||
                zeile.lowercase(Locale.getDefault()).startsWith("every day open") ||
                zeile.lowercase(Locale.getDefault()).startsWith("all day") ||
                zeile.uppercase(Locale.getDefault()).startsWith("MON") ||
                zeile.uppercase(Locale.getDefault()).startsWith("TUE") ||
                zeile.uppercase(Locale.getDefault()).startsWith("WED") ||
                zeile.uppercase(Locale.getDefault()).startsWith("THU") ||
                zeile.uppercase(Locale.getDefault()).startsWith("FRI") ||
                zeile.uppercase(Locale.getDefault()).startsWith("SAT") ||
                zeile.uppercase(Locale.getDefault()).startsWith("FRI") ||
                zeile.startsWith("SUN")
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
        Timber.d("doPostTrackZusatz " + (System.currentTimeMillis() - start) + "ms")
        return null
    }

    // durch einen Bug notwendig: urlMapPoint, kann man später weglassen
    private fun doPostTrackZusatz(
        xml: String,
        json: String,
        id: Long,
        urlMapPoint: String
    ): PictureResponse? {
        val start = System.currentTimeMillis()
        val fileZip =
            Environment.getExternalStorageDirectory().toString() + "/mxinfo/" + id + ".zip"
        val mFolder = File(Environment.getExternalStorageDirectory().toString() + "/mxinfo")
        if (!mFolder.exists()) {
            mFolder.mkdir()
        }
        val fileXml = "$id.xml"
        val fileJson = "$id.json"
        // byte[] buffer = new byte[1024];
        val fos: FileOutputStream
        val postRes: PictureResponse
        try {
            // Zippen
            fos = FileOutputStream(fileZip)
            val bos = BufferedOutputStream(fos)
            val zos = ZipOutputStream(bos)
            try {
                zos.putNextEntry(ZipEntry(fileXml))
                zos.write(xml.toByteArray())
                zos.putNextEntry(ZipEntry(fileJson))
                zos.write(json.toByteArray())
                zos.flush()
                zos.closeEntry()
            } catch (e: IOException) {
                Timber.e(e)
            } finally {
                try {
                    zos.close()
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
        } catch (e1: FileNotFoundException) {
            postRes = PictureResponse()
            postRes.message = e1.message
            return postRes
        }
        val zipFile = File(fileZip)
        val basic = aadhresUBase
        val fileReqBody: RequestBody = zipFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", zipFile.name, fileReqBody)

        val call = apiAdminClient.adminService
            .postZip(filePart, urlMapPoint, id, basic)
        try {
            val result = call.execute()
            zipFile.delete()
            Timber.d("doPostTrackZusatz " + (System.currentTimeMillis() - start) + "ms")
            return result.body()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return null
    }

    private fun doPostStateXML(xml: String, filename: String): List<RiderTrack>? {
        val fileZip = filename.substring(0, filename.lastIndexOf(".")) + ".zip"
        val fos: FileOutputStream
        try {
            // Zippen
            fos = FileOutputStream(fileZip)
            val bos = BufferedOutputStream(fos)
            val zos = ZipOutputStream(bos)
            try {
                zos.putNextEntry(ZipEntry(filename))
                zos.write(xml.toByteArray())
                zos.flush()
                zos.closeEntry()
            } catch (e: IOException) {
                Timber.e(e)
            } finally {
                try {
                    zos.close()
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
        } catch (e1: FileNotFoundException) {
            Timber.e(e1)
        }
        val zipFile = File(fileZip)
        val basic = aadhresUBase
        val fileReqBody: RequestBody = zipFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", zipFile.name, fileReqBody)

        val call = apiAdminClient.adminService
            .postRiderState(filename, filePart, basic)
        try {
            val result = call.execute()
            zipFile.delete()
            return result.body()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return null
    }

    fun doPostStateXMLStream(inputStream: InputStream, filename: String): List<RiderTrack>? {
        val fileZip = filename.substring(0, filename.lastIndexOf(".")) + ".zip"
        val buffer = ByteArray(1024)
        val fos: FileOutputStream
        try {
            // Zippen
            fos = FileOutputStream(fileZip)
            val bos = BufferedOutputStream(fos)
            val zos = ZipOutputStream(bos)
            try {
                zos.putNextEntry(ZipEntry(filename))
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    zos.write(buffer, 0, length)
                }
                zos.flush()
                zos.closeEntry()
            } catch (e: IOException) {
                Timber.e(e)
            } finally {
                try {
                    zos.close()
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
        } catch (e1: FileNotFoundException) {
            Timber.e(e1)
        }
        val zipFile = File(fileZip)
        val basic = aadhresUBase
        val fileReqBody: RequestBody = zipFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", zipFile.name, fileReqBody)

        val call = apiAdminClient.adminService
            .postRiderState(filename, filePart, basic)
        try {
            val result = call.execute()
            zipFile.delete()
            return result.body()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return null
    }

    companion object {
        private const val URLLISTE = "urlliste.xml"
        private val ns: String? = null
        private const val FILE_MAIN = "riderplanet.xml"
        private const val MAPJSON = "<script type=\"text/javascript\" src=\"rac"
        private const val MAPJSON_END = "\"></script>"
        const val RESULT_STR = "RESULTSTR"
        private const val XSLT_FILE = "rider_urls.xsl"
    }
}