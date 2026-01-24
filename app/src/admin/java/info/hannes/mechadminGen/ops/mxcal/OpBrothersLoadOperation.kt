package info.hannes.mechadminGen.ops.mxcal

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import com.google.gson.Gson
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadmin.brothers.BrotherTracks
import info.hannes.mechadminGen.ops.ImportDataBrother
import info.hannes.mechadminGen.sqlite.MxAdminDBContract.TrackstageBrother
import info.hannes.mechadminGen.sqlite.TrackstageBrotherRecord
import info.mx.comlib.retrofit.CommApiClient
import info.mx.comlib.util.RetroFileHelper
import info.mx.tracks.common.DistanceHelper.calcDistance2TracksCrypt
import info.mx.tracks.koin.CoreKoinComponent
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks
import info.mx.tracks.sqlite.TracksRecord
import org.koin.core.component.inject
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class OpBrothersLoadOperation : AbstractOpBrothersLoadOperation(), CoreKoinComponent {

    val commApiClient: CommApiClient by inject()

    /**
     * Gets addresses from location using modern API (Android 13+) or legacy API (older versions).
     * This method blocks until addresses are retrieved or timeout occurs.
     */
    private fun getAddressesFromLocation(geocoder: Geocoder, latitude: Double, longitude: Double, @Suppress("SameParameterValue") maxResults: Int): List<Address>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Modern API (Android 13+)
            val latch = CountDownLatch(1)
            var addresses: List<Address>? = null

            geocoder.getFromLocation(latitude, longitude, maxResults) { result ->
                addresses = result
                latch.countDown()
            }

            // Wait up to 5 seconds for the result
            latch.await(5, TimeUnit.SECONDS)
            addresses
        } else {
            // Legacy API (Android 12 and below)
            @Suppress("DEPRECATION")
            try {
                geocoder.getFromLocation(latitude, longitude, maxResults)
            } catch (e: IOException) {
                Timber.e(e, "Error getting addresses from location")
                null
            }
        }
    }

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        try {
            val filename = context.applicationContext.externalCacheDir.toString() + "/" + FILE_MAIN
            val file = File(filename)
            if (!file.exists()) {
                // Hole die Länderurl's
                LoggingHelperAdmin.setMessage("get " + args.url.replace("http://", ""))
                var httpContent = RetroFileHelper.getFileContent(commApiClient, args.url)
                LoggingHelperAdmin.setMessage("write $FILE_MAIN")
                httpContent = "{" + httpContent.substringAfter(TOKEN_START_1)
                httpContent = httpContent.take(httpContent.indexOf(TOKEN_END_1) + TOKEN_END_1.length)
                httpContent = "{" + httpContent.substringAfter(TOKEN_START_FEAT)
                httpContent = httpContent.take(httpContent.indexOf(TOKEN_END_1) - 2) + "}"
                writeToFile(file, httpContent)
            }
            LoggingHelperAdmin.setMessage("pars $FILE_MAIN")
            val inStream = BufferedInputStream(FileInputStream(file))
            val gson = Gson()
            val reader = BufferedReader(InputStreamReader(inStream))
            val brotherTracks = gson.fromJson(reader, BrotherTracks::class.java)
            val server = args.url.substring(0, args.url.indexOf("/", 11))
            LoggingHelperAdmin.setMessage("parsed $FILE_MAIN")

            // zum wieder aufsetzen
            val brotherExists = SQuery.newQuery().count(TrackstageBrother.CONTENT_URI)
            var zlr = 0
            for (trackBrother in brotherTracks.features!!) {
                zlr++
                LoggingHelperAdmin.setMessage(
                    "check if known: " + zlr + "/" + brotherTracks.features!!.size + " " +
                            trackBrother.attributes?.city
                )
                // wiederaufsetzen
                if (zlr <= brotherExists) {
                    continue
                }
                val newTrackStage = TrackstageBrotherRecord()
                newTrackStage.androidid = "debug"
                val geoCoder = Geocoder(context.applicationContext)
                val addresses = getAddressesFromLocation(geoCoder, trackBrother.lat, trackBrother.lon, 1)
                var countryKz = ""
                addresses?.let {
                    if (it.isNotEmpty()) {
                        countryKz = it[0].countryCode
                    }
                }
                var trackDB: TracksRecord? = null
                if (trackBrother.attributes?.city != trackBrother.attributes?.title) {
                    Timber.d("Namenunterschied. City:${trackBrother.attributes?.city} Title:${trackBrother.attributes?.title}")
                }
                calcDistance2TracksCrypt(
                    trackBrother.lat, trackBrother.lon, false, countryKz, context
                        .applicationContext
                )
                val byName = SQuery.newQuery()
                    .expr(
                        Tracks.TRACKNAME,
                        SQuery.Op.LIKE,
                        "%" + trackBrother.attributes?.city + "%"
                    )
                    .or()
                    .expr(
                        Tracks.TRACKNAME,
                        SQuery.Op.LIKE,
                        "%" + trackBrother.attributes?.title + "%"
                    )
                val tracks = SQuery.newQuery()
                    .expr(byName)
                    .expr(Tracks.COUNTRY, SQuery.Op.EQ, countryKz)
                    .select<TracksRecord>(Tracks.CONTENT_URI)
                for (track in tracks) {
                    if (trackDB == null) {
                        trackDB = track
                        if (trackDB!!.distance2location > 100) {
                            Timber.w("${trackDB.trackname} weit weg ${trackDB.distance2location}m")
                            newTrackStage.androidid = "admin"
                        }
                    }
                }
                // wenn keiner gefunden, wie weit ist der nächste weg
                if (trackDB == null) {
                    val trackNext = SQuery.newQuery()
                        .expr(Tracks.COUNTRY, SQuery.Op.EQ, countryKz)
                        .selectFirst<TracksRecord>(Tracks.CONTENT_URI, Tracks.DISTANCE2LOCATION)
                    if (trackNext != null && trackNext.distance2location < 100) {
                        trackDB = trackNext
                    } else if (trackNext != null && trackNext.distance2location < 500) {
                        trackDB = trackNext
                        newTrackStage.androidid = "admin"
                        newTrackStage.latitude = trackBrother.lat
                        newTrackStage.longitude = trackBrother.lon
                        Timber.i("$countryKz ${trackBrother.attributes?.city} nextA ${trackNext.trackname} ${trackNext.distance2location}m")
                    } else {
                        newTrackStage.latitude = trackBrother.lat
                        newTrackStage.longitude = trackBrother.lon
                        if (trackNext != null) {
                            Timber.i("$countryKz ${trackBrother.attributes?.city} nextB ${trackNext.trackname} ${trackNext.distance2location}m")
                        }
                    }
                }
                if (trackDB != null) {
                    newTrackStage.trackRestId = trackDB.restId
                } else {
                    newTrackStage.trackname = trackBrother.attributes?.city
                }
                // trackBrother.getAttributes().getName();
                // trackBrother.getAttributes().getTitle();
                val url = server + trackBrother.attributes?.url
                newTrackStage.urlDetailXml = url
                newTrackStage.urlPhoto = trackBrother.attributes?.photo
                newTrackStage.country = countryKz
                newTrackStage.save(true)
                // TODO temp

                //
                // SQuery.newQuery().expr(QuellFile.DOWNLOAD_SITE_ID, Op.LIKE, args.downLoadId).delete(QuellFile.CONTENT_URI);
                // for (final String url : urls) {
                // final QuellFileRecord rec = new QuellFileRecord();
                // rec.setDownloadSiteId(args.downLoadId);
                // rec.setUrl(url);
                // rec.save();
                // }
                LoggingHelperAdmin.setMessage("")
            }

            // wegen des hohen Speicherverbrauches des Html geht eine List<> nicht, daher ein Cursor
            val projection = arrayOf<String>()
            val cursor = SQuery.newQuery()
                .select(TrackstageBrother.CONTENT_URI, projection)
            zlr = 0
            while (cursor.moveToNext()) {
                zlr++
                val track = TrackstageBrotherRecord.fromCursor(cursor)
                ImportDataBrother.download2xHtml(
                    track,
                    track.urlDetailXml,
                    zlr.toString() + "_" + FILE_DETAIL,
                    zlr
                )
            }
            LoggingHelperAdmin.setMessage("")
        } catch (e: NullPointerException) {
            Timber.e(e)
            LoggingHelperAdmin.setMessage(e.message.toString())
            return OperationResult.error(e)
        } catch (e: IllegalStateException) {
            Timber.e(e)
            LoggingHelperAdmin.setMessage(e.message.toString())
            return OperationResult.error(e)
        } catch (e1: Exception) {
            Timber.e(e1)
            LoggingHelperAdmin.setMessage(e1.message.toString())
        }
        return OperationResult.ok(bundle)
    }

    @Throws(IOException::class)
    private fun writeToFile(file: File, text: String) {
        val fileOutputStream = FileOutputStream(file, true)
        val bufferedOutputStream = BufferedOutputStream(fileOutputStream, 128 * 100)
        bufferedOutputStream.write(text.toByteArray(Charset.forName("ISO-8859-1")))
        bufferedOutputStream.flush()
        fileOutputStream.close()
    }

    companion object {
        private const val FILE_MAIN = "brothers.json"
        private const val FILE_DETAIL = "brothersDetail.json"
        private const val TOKEN_START_FEAT = "\"features\":["
        private const val TOKEN_START_1 = ", \"tracks_map_openlayers_1\":{\""
        private const val TOKEN_END_1 = "\"title\":\"Tracks Map\", \"weight\":\"1\"}}"
    }
}
