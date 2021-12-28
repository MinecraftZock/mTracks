package info.hannes.mechadminGen.ops.mxcal

import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.net.Response
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadminGen.sqlite.MxCalContract.QuellFile
import info.hannes.mechadminGen.sqlite.QuellFileRecord
import info.mx.comlib.retrofit.CommApiClient
import info.mx.comlib.util.RetroFileHelper
import info.mx.core.MxCoreApplication.Companion.mxInfo
import info.mx.core.ops.ImportHelper.getCountryFromLatLng
import info.mx.core.util.Wait.delay
import info.mx.core_generated.rest.PostTrackstageIDRequest
import info.mx.core_generated.rest.PostTrackstageIDResult
import info.mx.core_generated.rest.RESTtrackStage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLConnection
import java.net.UnknownHostException
import java.util.Arrays
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.roundToInt

internal open class OpTracksMapLoadOperation : AbstractOpTracksMapLoadOperation(), KoinComponent {

    val commApiClient: CommApiClient by inject()

    private val countries: MutableMap<String, String> = HashMap()

    override fun onExecute(context: OperationContext, args: Args): OperationResult? {
        for (iso in Locale.getISOCountries()) {
            val l = Locale.Builder().setRegion(iso).build()
            countries[l.displayCountry.uppercase(Locale.getDefault())] = iso
        }
        try {
            if (!args.onlyExtract) {
                val sb = StringBuilder()
                val server = args.url.substring(0, args.url.lastIndexOf("/"))
                sb.append(RetroFileHelper.getFileContent(commApiClient, args.url))
                val body = sb.substring(sb.indexOf("<form id"), sb.indexOf("</form>"))
                var action = sb.substring(sb.indexOf("action=") + "action=".length + 1)
                action = action.substringBefore("\">")
                val optionList = body.substring(body.indexOf("<option"), body.indexOf("</select>"))
                    .trim { it <= ' ' }
                val options = optionList.split("</option>").toTypedArray()
                for (opt in options) {
                    val country = opt.substring(opt.indexOf("\"") + 1, opt.lastIndexOf("\""))
                    val url = "$server/$action&track=$country"
                    Timber.d("url $url")
                    LoggingHelperAdmin.setMessage("download $country")
                    onlyDownloadUrl(url, args.downLoadId)
                }
                LoggingHelperAdmin.setMessage("")
            }
            extractAllUrl(args.downLoadId)
        } catch (e: IllegalStateException) {
            Timber.e(e)
        } catch (e: IOException) {
            Timber.e(e)
        }
        LoggingHelperAdmin.setMessage("")
        return null
    }

    private fun getShortCountry(country: String): String? {
        var shortCountry = countries[country]
        if (country == "UK") {
            shortCountry = "GB"
        } else if (country == "DANMARK") {
            shortCountry = "DK"
        } else if (country == "REST_OF_WORLD") {
            shortCountry = ""
        } else if (country.startsWith("USA")) {
            shortCountry = "US"
        }
        return shortCountry
    }

    private fun extractAllUrl(downLoadId: Long) {
        var iNr = 0
        val or = SQuery.newQuery().expr(QuellFile.UPDATED_COUNT, SQuery.Op.EQ, -1).or()
            .append(QuellFile.UPDATED_COUNT + " is null")
        val files = SQuery.newQuery()
            .expr(QuellFile.DOWNLOAD_SITE_ID, SQuery.Op.LIKE, downLoadId)
            .expr(or)
            .append(QuellFile.REST_ID + " is null")
            .select<QuellFileRecord>(QuellFile.CONTENT_URI, QuellFile.CREATEDATE + " desc")
        for (fileRecord in files) {
            val anzahlChange = 0
            val options =
                fileRecord.content.split("map\\.addOverlay\\(marker\\)\\;bounds\\.extend\\(point\\)\\;")
                    .toTypedArray()
            for (opt in options) {
                val optTokBeg = ";var html = \""
                val trackStage = RESTtrackStage()
                val latlng = opt.substring(opt.indexOf("(") + 1, opt.indexOf(")"))
                trackStage.latitude = latlng.substring(0, latlng.indexOf(",")).toDouble()
                trackStage.longitude = latlng.substring(latlng.indexOf(",") + 1).toDouble()
                val name =
                    opt.substring(opt.indexOf("<b>") + 3, opt.indexOf("</b>")).replace("*", "")
                        .trim { it <= ' ' }
                trackStage.trackname = name
                var sx = false
                var kids = false
                var telNr = ""
                var contact = ""
                var trackAccess = "N"
                var trackUrl = ""
                var trackLength = 0
                var soil = 0
                var rating = 0
                val country = fileRecord.url.substring(fileRecord.url.lastIndexOf("=") + 1)
                iNr++
                LoggingHelperAdmin.setMessage(
                    iNr.toString() + " " + name + "[" + getShortCountry(
                        country
                    ) + "]"
                )
                val body = opt.substring(
                    opt.indexOf(optTokBeg) + optTokBeg.length,
                    opt.indexOf("var label = ")
                )
                val bodyItems = body
                    .split("<b>|</b>|<BR>|</DIV><DIV>&nbsp;</DIV><DIV>|&nbsp;|</font>|<font color=\\\\\"#000000\\\\\">|<div>|<DIV>|<br><br>")
                    .toTypedArray()
                val bodyList = Arrays.asList(*bodyItems)
                for (i in bodyList.indices.reversed()) {
                    bodyList[i] = bodyList[i] // .replaceAll("<br><br>", "\n")
                        .replace("<br>".toRegex(), " ") // .replaceAll("<div>", "\n")
                        .replace("</div>".toRegex(), "\n")
                        .replace("</DIV>".toRegex(), "\n") // .replaceAll("<DIV>", "\n")
                        .replace("</span>".toRegex(), "")
                        .replace("  ".toRegex(), " ")
                        .replace("!!".toRegex(), "!")
                        .replace("/a".toRegex(), "\n")
                        .replace("<div dir=\\\"ltr\\\">", "")
                        .replace("</div><div>".toRegex(), "")
                    if (bodyList[i] == "\";" || bodyList[i] == "</div><br><br>") {
                        bodyList[i] = ""
                    }
                }
                // List<String> bodyCleanItems = new LinkedList<String>();
                for (i in bodyList.indices) {
                    var tmpline = bodyList[i].replace("  ".toRegex(), " ").trim { it <= ' ' }
                    if (tmpline == "") {
                        continue
                    }
                    tmpline = removeToken(tmpline, "table")
                    tmpline = removeToken(tmpline, "div")
                    tmpline = removeToken(tmpline, "DIV")
                    tmpline = removeToken(tmpline, "TD")
                    tmpline = removeToken(tmpline, "DIV")
                    tmpline = removeToken(tmpline, "font")
                    tmpline = removeToken(tmpline, "font")
                    tmpline = removeToken(tmpline, "FONT")
                    tmpline = removeToken(tmpline, "FONT")
                    tmpline = removeToken(tmpline, "SPAN")
                    tmpline = removeToken(tmpline, "SPAN")
                    tmpline = removeToken(tmpline, "P")
                    tmpline = removeToken(tmpline, "p")
                    tmpline = removeToken(tmpline, "span")
                    tmpline = removeToken(tmpline, "span")
                    tmpline = tmpline.replace("$name.", "")
                        .replace("*", "")
                        .replace("$name is ", "It is ")
                        .trim { it <= ' ' }
                    trackLength = getTrackLength(tmpline, trackLength)
                    tmpline = tmpline.trim { it <= ' ' }

                    // Rating
                    if (tmpline.contains("many")) {
                        rating = 2
                    }
                    if (tmpline.contains("good")) {
                        rating = 3
                    }
                    if (tmpline.contains("very good")) {
                        rating = 4
                    }

                    // Boden
                    if (tmpline.lowercase(Locale.getDefault()).contains("clay")) {
                        soil = 1
                        Timber.d("boden clay $tmpline")
                        trackUrl += extractUrl(tmpline)
                        tmpline = ""
                    } else if (tmpline.lowercase(Locale.getDefault()).contains("hard ground")) {
                        soil = 7
                        Timber.d("boden hard $tmpline")
                        trackUrl += extractUrl(tmpline)
                        tmpline = ""
                    } else if (tmpline.lowercase(Locale.getDefault()).contains("sandy")) {
                        soil = 2
                        Timber.d("boden sandy $tmpline")
                        trackUrl += extractUrl(tmpline)
                        tmpline = ""
                    } else if (tmpline.lowercase(Locale.getDefault()).contains("soft ground")) {
                        soil = 8
                        Timber.d("boden soft $tmpline")
                        trackUrl += extractUrl(tmpline)
                        tmpline = ""
                    } else if (tmpline.lowercase(Locale.getDefault()).contains("middle ground")) {
                        soil = 9
                        Timber.d("boden middle $tmpline")
                        trackUrl += extractUrl(tmpline)
                        tmpline = ""
                    }

                    // removeToken(tmpline, "a", spezLink);
                    // telnr
                    if (tmpline.startsWith("+") || isPhoneNumber(tmpline)) {
                        tmpline = removeToken(tmpline, "DIV")
                        tmpline = removeToken(tmpline, "a")
                            .replace("Track VIDEO", "")
                            .replace("Click here:  ADD DETAILS", "")
                            .replace("<", "").replace(">", "")
                            .replace("\\.".toRegex(), "")
                        telNr = "$telNr$tmpline\\n"
                        Timber.d("telnr %s %s", name, tmpline)
                        tmpline = ""
                    }
                    val httpToke = " href=\\\""
                    if (tmpline.contains(httpToke)) {
                        var tmpHttp = tmpline.substring(tmpline.indexOf(httpToke) + httpToke.length)
                        tmpHttp = tmpHttp
                            .replace(Pattern.quote("\\").toRegex(), "\"")
                            .replace(Pattern.quote("/\"").toRegex(), "\"")
                        trackUrl = """
                            $trackUrl${tmpHttp.substring(0, tmpHttp.indexOf("\""))}
                            
                            """.trimIndent()
                        if (trackUrl.contains("youtube") || trackUrl.contains("tracksmap")) {
                            trackUrl = ""
                        } else {
                            Timber.d(
                                "trackUrl %s %s",
                                name,
                                tmpHttp.substring(0, tmpHttp.indexOf("\""))
                            )
                        }
                        tmpline = ""
                    }
                    if (tmpline.startsWith("http://")) {
                        trackUrl = """
                            $trackUrl$tmpline
                            
                            """.trimIndent()
                        tmpline = ""
                    }
                    if (tmpline.startsWith("www")) {
                        trackUrl = """
                            ${trackUrl}http://$tmpline
                            
                            """.trimIndent()
                        tmpline = ""
                    }
                    if (tmpline.lowercase(Locale.getDefault()).contains("mini")) {
                        kids = true
                        Timber.d("kids %s", tmpline)
                        tmpline = ""
                    }
                    if (tmpline.lowercase(Locale.getDefault()).contains("a sx-style track") ||
                        tmpline.lowercase(Locale.getDefault()).contains("a little sx-style track") ||
                        tmpline.lowercase(Locale.getDefault()).contains("SX") ||
                        tmpline.lowercase(Locale.getDefault()).contains("supercross")
                    ) {
                        sx = true
                        Timber.d("sx %s", tmpline)
                        tmpline = ""
                    }
                    if (tmpline.lowercase(Locale.getDefault()).contains("only for races") ||
                        tmpline.lowercase(Locale.getDefault()).contains("race track") ||
                        tmpline.lowercase(Locale.getDefault()).contains("open for races")
                    ) {
                        trackAccess = "R"
                        Timber.d("race %s", tmpline)
                        tmpline = ""
                    }
                    if (!startDatum(tmpline) &&
                        (tmpline.lowercase(Locale.getDefault()).contains("club drivers only") ||
                                tmpline.lowercase(Locale.getDefault()).contains("club riders only") ||
                                tmpline.lowercase(Locale.getDefault()).contains("club members only") ||
                                tmpline.lowercase(Locale.getDefault()).contains("for club members") ||
                                tmpline.lowercase(Locale.getDefault()).contains("only club members") ||
                                tmpline.lowercase(Locale.getDefault()).contains("only club owner") ||
                                tmpline.lowercase(Locale.getDefault()).contains("for club riders") ||
                                tmpline.lowercase(Locale.getDefault()).contains("for club drivers"))
                    ) {
                        trackAccess = "M"
                        Timber.d("memberOnly %s", tmpline)
                        tmpline = ""
                    }
                    if (tmpline.trim { it <= ' ' } != "" &&
                        tmpline.trim { it <= ' ' } != name.trim { it <= ' ' } &&
                        !tmpline.trim { it <= ' ' }.startsWith("hanks!") &&
                        !tmpline.trim { it <= ' ' }.startsWith("Thanks") &&
                        !tmpline.trim { it <= ' ' }.lowercase(Locale.getDefault()).contains("thenks") &&
                        !tmpline.trim { it <= ' ' }.startsWith("offroadxfilms.de") &&
                        !tmpline.trim { it <= ' ' }.startsWith("<EM>Thanks") &&
                        !tmpline.trim { it <= ' ' }.startsWith("<EM> Thanks") &&
                        tmpline.trim { it <= ' ' } != "thank you!" &&
                        tmpline.trim { it <= ' ' } != "It is a small track." &&
                        tmpline.trim { it <= ' ' } != "<" &&
                        tmpline.trim { it <= ' ' } != ">"
                    ) {
                        // if (tmpline.contains("\n\n")) {
                        // bodyCleanItems.add(tmpline.trim().substring(0, tmpline.indexOf("\n\n")));
                        // bodyCleanItems.add(tmpline.trim().substring(tmpline.indexOf("\n\n") + 2));
                        // } else {
                        bodyList[i] = tmpline.trim { it <= ' ' }
                        // bodyCleanItems.add(tmpline.trim());
                        // }
                    } else {
                        bodyList[i] = ""
                    }
                }
                val itemsFilter: MutableList<String> = ArrayList()
                for (ii in bodyList.indices) {
                    if (bodyList[ii] != "") {
                        itemsFilter.add(bodyList[ii])
                    }
                }
                // Datum
                val itemsDatumZwi: MutableList<String> = ArrayList()
                for (i in itemsFilter.indices) {
                    if (i < itemsFilter.size - 1 &&
                        startDatum(itemsFilter[i]) &&
                        isZeitZeile(itemsFilter[i + 1])
                    ) {
                        if (i < itemsFilter.size - 2 && isZeitZeile(itemsFilter[i + 2])) {
                            if (i < itemsFilter.size - 3 && isZeitZeile(itemsFilter[i + 3])) {
                                itemsDatumZwi.add(
                                    itemsFilter[i] + " " + itemsFilter[i + 1] + " " + itemsFilter[i + 2] + " " + itemsFilter[i + 3]
                                )
                                itemsFilter[i + 1] = ""
                                itemsFilter[i + 2] = ""
                                itemsFilter[i + 3] = ""
                            } else {
                                itemsDatumZwi.add(itemsFilter[i] + " " + itemsFilter[i + 1] + " " + itemsFilter[i + 2])
                                itemsFilter[i + 1] = ""
                                itemsFilter[i + 2] = ""
                            }
                        } else {
                            itemsDatumZwi.add(itemsFilter[i] + " " + itemsFilter[i + 1])
                            itemsFilter[i + 1] = ""
                        }
                    } else {
                        itemsDatumZwi.add(itemsFilter[i])
                    }
                }
                val itemsDatum: MutableList<String> = ArrayList()
                val itemsRest: MutableList<String> = ArrayList()
                for (i in itemsDatumZwi.indices) {
                    var line = itemsDatumZwi[i]
                        .replace("</u>".toRegex(), "")
                        .replace("<u>".toRegex(), "")
                        .replace("</p>".toRegex(), "")
                        .replace("\n", "")
                        .replace("<>", "")
                        .replace("<p>".toRegex(), "")
                        .replace("\'", "'").replace("\"", "'")
                        .trim { it <= ' ' }
                    line = removeToken(line, "span")
                    line = removeToken(line, "SPAN")
                    if (line != "" && !line.lowercase(Locale.getDefault()).contains("click")) {
                        if (startDatum(line) || isZeitZeile(line)) {
                            if (line.contains("Holiday")) {
                                itemsDatum.add(
                                    line.substring(0, line.indexOf("Holiday")).replace(",", "")
                                )
                                itemsRest.add(line.substring(line.indexOf("Holiday")))
                            } else {
                                itemsDatum.add(line.replace(",", ""))
                            }
                        } else {
                            itemsRest.add(line)
                        }
                    }
                }
                telNr = telNr.replace(" ".toRegex(), "").trim { it <= ' ' }
                    .replace("\\+".toRegex(), "öÜ")
                val telList = telNr.split("ö|\n").toTypedArray()
                telNr = ""
                for (tel in telList) {
                    if (tel != "") {
                        telNr = """
                            $telNr
                            ${tel.replace("Ü", "+").replace("\\n", "").trim { it <= ' ' }}
                            """.trimIndent()
                        Timber.i("tel $name [${getShortCountry(country)}]${tel.replace("Ü", "+").replace("\\n", "")}")
                    }
                }
                val wwwList = trackUrl.split("\n").toTypedArray()
                trackUrl = ""
                for (www in wwwList) {
                    if (www != "" && !www.contains("offroadxfilms") && www != "http:/") {
                        var zw = www
                        if (zw.endsWith("com") &&
                            zw.substring(zw.lastIndexOf("com") - 1, zw.lastIndexOf("com")) != "."
                        ) {
                            zw = (zw + "y").replace(
                                "comy",
                                ".com"
                            ) // falls es in der Mitte auch ein com gibt
                        }
                        if (zw.contains("@")) {
                            contact = """$contact
${zw.replace("mailto:", "")}""".trim { it <= ' ' }
                        } else {
                            var type: String
                            try {
                                val url = URL(zw)
                                Timber.v("typ $name [${getShortCountry(country)}] $zw start")
                                val uc: URLConnection = url.openConnection()
                                uc.connectTimeout = TIMEOUT_VALUE
                                uc.readTimeout = TIMEOUT_VALUE
                                type = uc.contentType
                                if (trackUrl.contains(
                                        """
$zw

""".trimIndent()
                                    )
                                ) {
                                    trackUrl = """
                                        $trackUrl$zw
                                        
                                        """.trimIndent()
                                }
                                Timber.i("typ $name[${getShortCountry(country)}] $zw $type")
                            } catch (_: SocketTimeoutException) {
                                Timber.e("typ $name[${getShortCountry(country)}] SocketTimeoutException")
                            } catch (_: UnknownHostException) {
                                Timber.e("typ $name[${getShortCountry(country)}] UnknownHostException")
                            } catch (e: IOException) {
                                Timber.e("typ $name[${getShortCountry(country)}] ${e.message}")
                            }
                            trackUrl = """$trackUrl
$zw""".trim { it <= ' ' }
                            Timber.i("www $name[${getShortCountry(country)}] $zw")
                        }
                    }
                }
                trackUrl = trackUrl.trim { it <= ' ' }
                var datGes = ""
                for (itemD in itemsDatum) {
                    var item = itemD.replace(" -", "-").replace("- ", "-")
                        .replace(" >", ">").replace("> ", ">").replace("Sat:", "SAT")
                        .replace("Sun:", "SUN").replace("Fri:", "FRI")
                        .replace("Mon:", "MON").replace("Tue:", "TUE").replace("Wed:", "WED")
                        .replace("Thu:", "THU")
                        .replace("Sunday", "SUN")
                        .replace("closed SUN and", "closed")
                        .replace("SUN closed", "")
                        .replace("  ".toRegex(), " ")
                    item = removeToken(item, "DIV")
                    datGes = if (item.lowercase(Locale.getDefault()).contains("open every day") ||
                        item.lowercase(Locale.getDefault()).startsWith("every day open") ||
                        item.lowercase(Locale.getDefault()).startsWith("mon>sun") ||
                        item.lowercase(Locale.getDefault()).startsWith("all day")
                    ) {
                        ("$datGes MON TUE WED THU FRI SAT SUN " +
                                item.lowercase(Locale.getDefault())
                                    .replace("open every day", "")
                                    .replace("mon>sun", "")
                                    .replace("every day open", "")
                                    .replace("all day", "")
                                    .replace("!".toRegex(), ""))
                    } else if (item.lowercase(Locale.getDefault()).startsWith("weekends open")) {
                        datGes + " " + "SAT SUN " + item.lowercase(Locale.getDefault()).replace("weekends open", "")
                    } else if (item.contains("MON>FRI")) {
                        datGes + " " + item.replace("MON>FRI", " MON TUE WED THU FRI ")
                    } else if (item.contains("WED>MON")) {
                        datGes + " " + item.replace("WED>MON", " MON TUE WED ")
                    } else if (item.contains("MON>SAT")) {
                        datGes + " " + item.replace("MON>SAT", " MON TUE WED THU FRI SAT ")
                    } else if (item.contains("TUE>FRI")) {
                        datGes + " " + item.replace("TUE>FRI", " TUE WED THU FRI ")
                    } else if (item.contains("TUE>THU")) {
                        datGes + " " + item.replace("TUE>THU", " TUE WED THU ")
                    } else if (item.contains("TUE>SAT")) {
                        datGes + " " + item.replace("TUE>SAT", " TUE WED THU FRI SAT ")
                    } else if (item.contains("TUE>SUN")) {
                        datGes + " " + item.replace("TUE>SUN", " TUE WED THU FRI SAT SUN ")
                    } else if (item.contains("THU>MON")) {
                        datGes + " " + item.replace("THU>MON", " MON TUE WED THU ")
                    } else if (item.contains("THU>SUN")) {
                        datGes + " " + item.replace("THU>SUN", " THU FRI SAT SUN ")
                    } else if (item.contains("MON>THU")) {
                        datGes + " " + item.replace("MON>THU", " MON TUE WED THU ")
                    } else if (item.contains("WED>SUN")) {
                        datGes + " " + item.replace("WED>SUN", " WED THU FRI SAT SUN ")
                    } else {
                        "$datGes $item"
                    }
                }
                Timber.i("dat${if (datGes.contains(">")) ">" else " "}$name[${getShortCountry(country)}] ${datGes.trim { it <= ' ' }}")
                fillDatum(trackStage, datGes.trim { it <= ' ' })
                for (it in itemsRest) {
                    Timber.i("res $name[${getShortCountry(country)}] $it")
                }
                Timber.d("url ${getShortCountry(country)}$name $latlng $telNr")
                trackStage.androidid = "debug"
                trackStage.supercross = if (sx) 1 else -1
                trackStage.kidstrack = if (kids) 1 else -1
                trackStage.trackaccess = trackAccess
                trackStage.tracklength = trackLength
                trackStage.soiltype = soil
                if (getShortCountry(country) == null || getShortCountry(country) == "") {
                    trackStage.country = getShortCountry(trackStage.latitude, trackStage.longitude)
                }
                if (getShortCountry(country) != null) {
                    trackStage.country = getShortCountry(country)
                }
                if (trackUrl != "") {
                    trackStage.url = trackUrl
                }
                if (contact != "") {
                    trackStage.contact = contact
                }
                if (trackUrl != "") {
                    trackStage.url = trackUrl
                }
                if (telNr.trim { it <= ' ' } != "") {
                    trackStage.phone = telNr.trim { it <= ' ' }
                }

                // push
                val webClient = mxInfo
                val request = PostTrackstageIDRequest(trackStage)
                var res: Response<PostTrackstageIDResult?>
                try {
                    res = webClient.postTrackstageID(request)
                    var result = res.parse()
                    delay()
                    if (result == null) { // zweiter Versuch
                        res = webClient.postTrackstageID(request)
                        result = res.parse()
                    }
                    res.checkResponseCodeOk()
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            fileRecord.updatedCount = anzahlChange.toLong()
            fileRecord.save()
        }
        Timber.d("finish")
    }

    private fun getShortCountry(latitude: Double, longitude: Double): String? {
        return getCountryFromLatLng(commApiClient, latitude, longitude)
    }

    private fun fillDatum(trackStage: RESTtrackStage, line: String): RESTtrackStage {
        val posDatToken = getPosFirstDatToken(line)
        val postToken: String
        val preToken: String
        var token = ""
        if (posDatToken == -1) {
            preToken = line
            postToken = ""
        } else {
            preToken = line.substring(0, posDatToken).trim { it <= ' ' }
            token = line.substring(posDatToken, posDatToken + 3)
            postToken = line.substring(posDatToken + if (line.length > 2) 3 else line.length)
                .trim { it <= ' ' }
        }
        // zuerst die noch offenen Zeiten eintragen
        if (preToken != "") {
            if (trackStage.openmondays == 1 && trackStage.hoursmonday == null) {
                trackStage.hoursmonday = preToken
            }
            if (trackStage.opentuesdays == 1 && trackStage.hourstuesday == null) {
                trackStage.hourstuesday = preToken
            }
            if (trackStage.openwednesday == 1 && trackStage.hourswednesday == null) {
                trackStage.hourswednesday = preToken
            }
            if (trackStage.openthursday == 1 && trackStage.hoursthursday == null) {
                trackStage.hoursthursday = preToken
            }
            if (trackStage.openfriday == 1 && trackStage.hoursfriday == null) {
                trackStage.hoursfriday = preToken
            }
            if (trackStage.opensaturday == 1 && trackStage.hourssaturday == null) {
                trackStage.hourssaturday = preToken
            }
            if (trackStage.opensunday == 1 && trackStage.hourssunday == null) {
                trackStage.hourssunday = preToken
            }
        }
        when (token) {
            "MON" -> trackStage.openmondays = 1
            "TUE" -> trackStage.opentuesdays = 1
            "WED" -> trackStage.openwednesday = 1
            "THU" -> trackStage.openthursday = 1
            "FRI" -> trackStage.openfriday = 1
            "SAT" -> trackStage.opensaturday = 1
            "SUN" -> trackStage.opensunday = 1
        }
        return if (postToken == "") {
            trackStage
        } else {
            fillDatum(trackStage, postToken) // recursiv
        }
    }

    private fun getPosFirstDatToken(line: String): Int {
        var res = -1
        res = setFirstAppear(res, line.indexOf("MON"))
        res = setFirstAppear(res, line.indexOf("TUE"))
        res = setFirstAppear(res, line.indexOf("WED"))
        res = setFirstAppear(res, line.indexOf("THU"))
        res = setFirstAppear(res, line.indexOf("FRI"))
        res = setFirstAppear(res, line.indexOf("SAT"))
        res = setFirstAppear(res, line.indexOf("SUN"))
        return res
    }

    private fun setFirstAppear(res: Int, indexOf: Int): Int {
        return if ((indexOf < res || res == -1) && indexOf > -1) indexOf else res
    }

    private fun extractUrl(line: String): String {
        if (line.contains("http")) {
            var tmp = line.substring(line.indexOf("http"))
            tmp = tmp
                .replace(Pattern.quote("\\").toRegex(), "\"")
                .replace(Pattern.quote("/\"").toRegex(), "\"")
            tmp = tmp.substring(0, tmp.indexOf("\""))
            return if (tmp.contains("tracksmap") || tmp.contains("youtube") || tmp.contains("video")) {
                ""
            } else {
                """
     $tmp
     
     """.trimIndent()
            }
        }
        return ""
    }

    private fun getTrackLength(tmpline: String, trackLength: Int): Int {
        var tok: String
        val regularExpression = "[0-9]+"
        val regularExpressionComma = "^[0-9,;]+$"
        if (tmpline.contains("mt")) {
            tok = tmpline.substring(0, tmpline.indexOf("mt")).trim { it <= ' ' }
            tok = tok.substring(if (tok.lastIndexOf(" ") == -1) 0 else tok.lastIndexOf(" "))
                .trim { it <= ' ' }
            if (tok.matches(regularExpression.toRegex())) {
                return tok.toInt()
            }
        } else if (tmpline.contains("km")) {
            tok = tmpline.substring(0, tmpline.indexOf("km")).trim { it <= ' ' }
            tok = tok.substring(if (tok.lastIndexOf(" ") == -1) 0 else tok.lastIndexOf(" "))
                .trim { it <= ' ' }
            if (tok.matches(regularExpressionComma.toRegex())) {
                val zwi = tok.replace(",", ".").toDouble()
                return (if (zwi > 1000) zwi * 1 else zwi * 1000).roundToInt()
            }
        }
        return trackLength
    }

    @Throws(IOException::class)
    private fun onlyDownloadUrl(url: String, downloadId: Long) {
        val sb = RetroFileHelper.getFileContent(commApiClient, url)
        if (sb.indexOf("var point = new GLatLng(") == -1) {
            return
        }
        var optionList = sb.substring(
            sb.indexOf("var point = new GLatLng("),
            sb.indexOf("document.getElementById(\"side_bar\").innerHTML = side_bar_html")
        ).trim { it <= ' ' }
        optionList = optionList
            .replace("&amp;".toRegex(), "")
            .replace("<tbody>".toRegex(), "")
            .replace("<td>".toRegex(), "")
            .replace("</td>".toRegex(), "")
            .replace("<TD>".toRegex(), "")
            .replace("</TD>".toRegex(), "")
            .replace("<strong>".toRegex(), "")
            .replace("</strong>".toRegex(), "")
            .replace("<FONT>".toRegex(), "")
            .replace("</FONT>".toRegex(), "")
            .replace("<U>".toRegex(), "")
            .replace("<br> <br>".toRegex(), "<br><br>")
            .replace("</U>".toRegex(), "")
            .replace("<SPAN>".toRegex(), "")
            .replace("&gt;".toRegex(), ">")
            .replace("</SPAN>".toRegex(), "")
            .replace("</A>".toRegex(), "")
            .replace("\\'s".toRegex(), "'s")
            .replace("<P>".toRegex(), "")
            .replace("</P>".toRegex(), "")
        val quellFile = QuellFileRecord()
        quellFile.content = optionList
        quellFile.url = url
        quellFile.downloadSiteId = downloadId
        quellFile.save()
    }

    /**
     * @param bodyCleanItems
     * @return
     */
    protected fun handleDatum(bodyCleanItems: MutableList<String>): List<String> {
        val itemsDatum: MutableList<String> = ArrayList()
        for (i in bodyCleanItems.indices) {
            if (i < bodyCleanItems.size - 1 &&
                startDatum(bodyCleanItems[i]) &&
                isZeitZeile(bodyCleanItems[i + 1])
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

    private fun isPhoneNumber(zeile: String): Boolean {
        if (isZeitZeile(zeile)) {
            return false
        }
        val regularExpression = "[0-9]+"
        var numOnly = zeile.replace(" ".toRegex(), "").trim { it <= ' ' }
            .replace("\\.".toRegex(), "").replace("-".toRegex(), "")
        if (numOnly.contains("(")) {
            numOnly = numOnly.substring(0, numOnly.indexOf("("))
        }
        if (numOnly.contains("\n")) {
            numOnly = numOnly.substring(0, numOnly.indexOf("\n"))
        }
        if (numOnly.contains("S")) {
            numOnly = numOnly.substring(0, numOnly.indexOf("S"))
        }
        if (numOnly.contains("mt")) {
            numOnly = numOnly.substring(0, numOnly.indexOf("mt"))
        }
        if (numOnly.contains("am")) {
            numOnly = numOnly.substring(0, numOnly.indexOf("am"))
        }
        val temp = zeile.replace(" ".toRegex(), "").trim { it <= ' ' }
        return (numOnly.length > 6 &&
                temp.length > 7 &&
                temp.substring(1, 2) != "." &&
                temp.substring(2, 3) != "." &&
                temp.substring(2, 3) != ":" &&
                temp.substring(0, 1).matches(regularExpression.toRegex()))
    }

    private fun isZeitZeile(zeile: String): Boolean {
        val regExDouble = "^[0-9.;]+$"
        val regExInt = "[0-9]+"
        val temp = zeile.replace(" ".toRegex(), "").trim { it <= ' ' } + " "
        val tges: Double
        if (temp.trim { it <= ' ' }.matches(regExInt.toRegex())) {
            tges = temp.trim { it <= ' ' }.toDouble()
            if (tges > 0 && tges < 23) {
                return true // nur eine Nummer von 1..23
            }
        }
        return if (temp.isEmpty() || !(temp.indexOf("-") in 1..5)) {
            false
        } else {
            val tok1 = temp.substringBefore("-").replace(":", ".").replace("(", "")
            var tok2 = temp.substring(temp.indexOf("-") + 1).replace(":", ".").trim { it <= ' ' }
            tok2 = tok2.take(if (tok2.length < 2) tok2.length else 2)
            var t1 = 0.0
            var t2 = 0.0
            if (tok1.matches(regExDouble.toRegex())) {
                t1 = tok1.toDouble()
            }
            if (tok2.matches(regExDouble.toRegex())) {
                t2 = tok2.toDouble()
            }
            t1 > 8 &&
                    tok2 == "" ||
                    t1 > 6 &&
                    t1 < 20 &&
                    t2 > 9 &&
                    t2 < 24 ||
                    tok2.take(if (tok2.length < 2) tok2.length else 2) == "to" ||
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

    private fun removeToken(source: String, token: String): String {
        var tmp = source
        if (source.contains("<$token")) {
            val firstPart = source.substring(0, source.indexOf("<$token"))
            tmp = source.substring(source.indexOf("<$token") + 2)
            tmp = firstPart + " " + tmp.substring(tmp.indexOf(">") + 1)
        }
        return tmp.trim { it <= ' ' }
    }

    companion object {
        private const val TIMEOUT_VALUE = 3000
    }
}
