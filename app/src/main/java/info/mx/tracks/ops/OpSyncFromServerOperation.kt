package info.mx.tracks.ops

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robotoworks.mechanoid.net.Response
import com.robotoworks.mechanoid.net.ServiceException
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.commonlib.TrackingApplication
import info.mx.comlib.retrofit.service.model.TrackR
import info.mx.commonlib.NetworkHelper
import info.mx.core.MxCoreApplication
import info.mx.core.MxCoreApplication.Companion.isAdminOrDebug
import info.mx.core.common.ImportStatusMessage
import info.mx.core.common.LoggingHelper
import info.mx.core.ops.ImportIdlingResource
import info.mx.core.rest.MxInfo
import info.mx.core.util.LocationHelper
import info.mx.core.util.NetworkUtils
import info.mx.core.util.Wait
import info.mx.core.util.checkResponseCodeOk
import info.mx.core_generated.ops.AbstractOpPostImagesOperation
import info.mx.core_generated.ops.AbstractOpSyncFromServerOperation
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.rest.PostNetworkErrorsRequest
import info.mx.core_generated.rest.PostNetworkErrorsResult
import info.mx.core_generated.rest.PutTrackstageRequest
import info.mx.core_generated.rest.RESTnetworkError
import info.mx.core_generated.rest.RESTtrackStage
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks
import info.mx.tracks.MxAccessApplication.Companion.aadhresU
import info.mx.tracks.common.SecHelper
import info.mx.tracks.data.DataManagerApp
import info.mx.tracks.ops.MechanoidHelper.bulkInsertMechanoid
import info.mx.tracks.ops.MechanoidHelper.doStagesPushMechanoid
import info.mx.tracks.ops.MechanoidHelper.doStagesReceiveNewMechanoid
import info.mx.tracks.ops.MechanoidHelper.proceedTracksMechanoid
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Comment
import info.mx.tracks.room.entity.Picture
import info.mx.tracks.room.entity.Track
import info.mx.tracks.room.entity.TrackStage
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.io.inputstream.ZipInputStream
import net.lingala.zip4j.util.UnzipUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.OutputStream
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class OpSyncFromServerOperation : AbstractOpSyncFromServerOperation(), KoinComponent {
    private var lastKnown: Location? = null
    private lateinit var operationContext: OperationContext

    private val dataManagerApp: DataManagerApp by inject()
    private val locationHelper: LocationHelper by inject()
    private val mxDatabase: MxDatabase by inject()

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
        operationContext = context
        requestLastLocation()

        val webClient = MxCoreApplication.mxInfo
        ImportIdlingResource.increment()

        try {
            val trackCount = mxDatabase.trackDao().countAll()
            if (trackCount == 0) {
                doImportInitial()
            }

            var countryResult = ""
            if (NetworkHelper.isOnline(context.applicationContext)) {
                doHandleTrackStage(context.applicationContext, webClient)
                var gesImported = 0
                var newImported = doSyncTracksBlock(context, args.updateProvider, gesImported, args.flavor)
                while (newImported > 0) {
                    gesImported += newImported
                    newImported = doSyncTracksBlock(context, args.updateProvider, gesImported, args.flavor)
                }

                if (MxCoreApplication.isAdmin) {
                    doFixMissingCounty(context.applicationContext)
                    doHandleTrackStage(context.applicationContext, webClient)
                }
                countryResult = doBuildCountryTableRoom(context.applicationContext)
                imported = 0
                Thread.sleep(4000)

                if (lastKnown != null) {
                    calcDistance(context.applicationContext, lastKnown!!)
                }

                doSyncPictures(context)
                imported = 0
                doSyncComments(context)
                imported = 0
                doPushNetworkErrorsAtOnce(webClient)

                val intentM = AbstractOpPostImagesOperation.newIntent()
                Ops.execute(intentM)

                doCleanFromDecline()

                MxPreferences.instance.edit().putLastSyncTime(System.currentTimeMillis()).apply()
            }
            LoggingHelper.setMessage("")
            val bundle = Bundle()
            bundle.putString(COUNTRY_RESULT, countryResult)
            ImportIdlingResource.decrement()
            return OperationResult.ok(bundle)
        } catch (e: ServiceException) {
            Timber.e(e)
            LoggingHelper.setMessage("")
            if (MxCoreApplication.isAdmin) {
                LoggingHelper.setMessage(e.message.toString())
                Toast.makeText(context.applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            ImportIdlingResource.decrement()
            return OperationResult.error(e)
        } catch (e: Exception) {
            Timber.e(e)
            LoggingHelper.setMessage("")
            if (MxCoreApplication.isAdmin) {
                LoggingHelper.setMessage(e.message.toString())
                Toast.makeText(context.applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            LoggingHelper.setMessage("")
            ImportIdlingResource.decrement()
            return OperationResult.error(e)
        }

    }

    private fun doImportInitial() {
        deZip(operationContext.applicationContext)?.let {
            val gson = Gson()
            try {
                LoggingHelper.setMessage("prepare data")
                val trackRS = gson.fromJson<List<TrackR>>(FileReader(it), object : TypeToken<List<TrackR?>?>() {}.type)
                Timber.i("${trackRS.size}")

                val contentValuesTrackList = ArrayList<ContentValues>()
                val trackList = mutableListOf<Track>()
                var opName = "Tracks Init Room"

                var zlrInserted = 0
                zlrInserted = proceedTracksRoom(trackRS, zlrInserted, trackList, opName, 0)
                if (zlrInserted > 0) {
                    bulkInsertTrackRoom(
                        trackList = trackList,
                        ges = trackRS.size,
                        was = opName,
                        previousImported = 0
                    )
                }

                opName = "Tracks Init Mechanoid"
                zlrInserted = 0
                zlrInserted = proceedTracksMechanoid(
                    tracksResponse = trackRS,
                    zlrInserted = zlrInserted,
                    contentValuesTrackList = contentValuesTrackList,
                    opName = opName,
                    previousImported = 0,
                    updateProvider = true,
                    operationContext = operationContext,
                    lastKnown = lastKnown
                )
                // clean up
                if (zlrInserted > 0) {
                    bulkInsertMechanoid(
                        context = operationContext.applicationContext,
                        contentValuesList = contentValuesTrackList,
                        uri = Tracks.CONTENT_URI,
                        ges = trackRS.size,
                        was = opName,
                        previousImported = 0
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun deZip(context: Context): String? {
        var outFilePath: String? = null
        var zipInputStream: ZipInputStream? = null
        var os: FileOutputStream? = null
        try {
            val inputStream = context.assets.open("tracks.zip")
            val zipFileTmp = File(context.cacheDir.toString() + File.separator + "tracks.zip")
            val outputStream = FileOutputStream(zipFileTmp)
            var read: Int
            val bytes = ByteArray(1024)
            while (inputStream.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
            inputStream.close()
            outputStream.flush()
            outputStream.close()
            val zipFile = ZipFile(zipFileTmp)
            zipFile.setPassword(aadhresU.toCharArray())
            val fileHeaderList = zipFile.fileHeaders
            for (fileHeader in fileHeaderList) {
                if (fileHeader != null) { // Build the output file
                    outFilePath = context.cacheDir.toString() + File.separator + fileHeader.fileName
                    val outFile = File(outFilePath)
                    if (fileHeader.isDirectory) {
                        outFile.mkdirs()
                        continue
                    }
                    outFile.parentFile?.let {
                        if (!it.exists()) {
                            it.mkdirs()
                        }
                    }

                    zipInputStream = zipFile.getInputStream(fileHeader)
                    os = FileOutputStream(outFile)
                    var readLen: Int
                    val buff = ByteArray(1024)
                    // Loop until End of File and write the contents to the output stream
                    while (zipInputStream.read(buff).also { readLen = it } != -1) {
                        os.write(buff, 0, readLen)
                    }
                    // Please have a look into this method for some important comments
                    closeFileHandlers(zipInputStream, os)
                    // To restore File attributes (ex: last modified file time,
                    // read only flag, etc) of the extracted file, a utility
                    // class can be used as shown below
                    UnzipUtil.applyFileAttributes(fileHeader, outFile)
                }
            }
            zipFileTmp.delete()
        } catch (e: IOException) {
            Timber.e(e)
        } finally {
            try {
                closeFileHandlers(zipInputStream, os)
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
        return outFilePath
    }

    @Throws(IOException::class)
    private fun closeFileHandlers(`is`: ZipInputStream?, os: OutputStream?) { // Close output stream
        os?.close()
        // Closing inputstream also checks for CRC of the just extracted
        // file. If CRC check has to be skipped (for ex: to cancel the unzip
        // operation, etc) use method is.close(boolean skipCRCCheck) and set the
        // flag, skipCRCCheck to false
        // NOTE: It is recommended to close outputStream first because Zip4j
        // throws an exception if CRC check fails
        `is`?.close()
    }

    private fun calcDistance(context: Context, location: Location) {
        val recalculateTracks = Intent(RECALC_TRACKS)
        recalculateTracks.setPackage(context.packageName)
        recalculateTracks.putExtra(LOCATION, location)
        recalculateTracks.putExtra(SOURCE, "Sync")
        context.sendBroadcast(recalculateTracks)
    }

    @Throws(RemoteException::class, OperationApplicationException::class, IOException::class)
    fun doSyncPictures(context: OperationContext) {
        val opName = "Pictures"
        val maxCreated = mxDatabase.pictureDao().getNewest()

        try {
            val picturesResponse = dataManagerApp.getPictures(maxCreated.toLong())

            picturesResponse.checkResponseCodeOk()

            LoggingHelper.setMessage("$DOWNLOAD $opName")
            var zlrUpdated = 0
            var zlrInserted = 0
            val opsTracks = mutableListOf<Picture>()
            for (pictureR in picturesResponse.body()!!) {
                if (context.isAborted) {
                    return
                }

                zlrUpdated++
                val localPicture = mxDatabase.pictureDao().getById(pictureR.id.toLong())

                if (localPicture == null) { // new entry
                    zlrInserted++
                    val newPicture = Picture()

                    newPicture.id = pictureR.id.toLong()
                    newPicture.changed = pictureR.changed.toLong()
                    newPicture.approved = pictureR.approved
                    newPicture.deleted = pictureR.deleted
                    newPicture.trackId = pictureR.trackId.toLong()
                    newPicture.username = pictureR.username
                    newPicture.androidid = pictureR.androidid

                    opsTracks.add(newPicture)

                    // bulk insert
                    if (zlrUpdated > BLOCK_SIZE) {
                        zlrUpdated = doApplyBatchRoom(mxDatabase, opsTracks, picturesResponse.body()!!.size, 0)
                    }
                } else {
                    deleteFile(localPicture.localfile)
                    deleteFile(localPicture.localthumb)
                    localPicture.localfile = ""
                    localPicture.localthumb = ""

                    localPicture.changed = pictureR.changed!!.toLong()
                    localPicture.approved = pictureR.approved!!
                    localPicture.deleted = pictureR.deleted!!
                    localPicture.trackId = pictureR.trackId!!.toLong()
                    localPicture.username = pictureR.username
                    mxDatabase.pictureDao().update(localPicture)
                }
            }
            // clean up
            if (zlrInserted > 0) {
                doApplyBatchRoom(mxDatabase, opsTracks, picturesResponse.body()!!.size, 0)
            }
            LoggingHelper.setMessage("") // SyncPictures is also called from pushImages, so we need to clear the message here
            Timber.i("$opName all ${(if (picturesResponse.body() != null) picturesResponse.body()!!.size else 0)} updated: $zlrUpdated")
        } catch (e: Exception) {
            if (isAdminOrDebug) {
                Toast.makeText(context.applicationContext, "${this.javaClass.name} ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun doPushNetworkErrorsAtOnce(webClient: MxInfo) {
        val networkErrors = mxDatabase.networkDao().all

        val requestList = ArrayList<RESTnetworkError>()
        for (networkError in networkErrors) {
            val requestSingle = RESTnetworkError()
            requestSingle.changed = networkError.changed
            requestSingle.reason = networkError.reason
            requestSingle.tracks = networkError.tracks.toInt()
            requestSingle.androidid = TrackingApplication.androidId
            requestList.add(requestSingle)
        }
        val requestNetwork = PostNetworkErrorsRequest(requestList)
        val resultTrack: Response<PostNetworkErrorsResult>
        try {
            resultTrack = webClient.postNetworkErrors(requestNetwork)
            if (resultTrack.responseCode == Response.HTTP_NO_CONTENT) {
                mxDatabase.networkDao().deleteAll()
            } else {
                Timber.w("Network result code:%s", resultTrack.responseCode)
            }
        } catch (_: ServiceException) {
        }
    }

    private fun doFixMissingCounty(context: Context) {
        val coder = Geocoder(context)
        mxDatabase.trackDao().emptyCountry("").forEachIndexed { index, track ->
            try {
                val addresses = getAddressesFromLocation(coder, SecHelper.entcryptXtude(track.latitude), SecHelper.entcryptXtude(track.longitude), 1)
                if (!addresses.isNullOrEmpty()) {
                    val country = addresses[0].countryCode
                    Timber.d("${track.trackname} country:$country")
                    val stage = TrackStage()
                    stage.trackId = track.id!!
                    stage.country = country
                    mxDatabase.trackStageDao().insertTrackStagesAll(stage)
                    track.country = country
                    mxDatabase.trackDao().update(track)
                    Timber.d("$index ${track.trackname} country updated:$country")
                }
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    private fun doCleanFromDecline() {
        // the last should be kept
        val maxCreated = mxDatabase.trackDao().latest()
        val del = mxDatabase.trackDao().deleteNotApproved(maxCreated)
        Timber.d("del approved:%s", del)
        mxDatabase.trackStageDao().deleteNotApproved()
        mxDatabase.pictureDao().deleteNotApproved()
        mxDatabase.commentDao().deleteNotApproved()
    }

    private fun doHandleTrackStage(context: Context, webClient: MxInfo) {
        try {
            doStagesUpdate(webClient) //put
            doStagesPushMechanoid(context, webClient)
            if (MxCoreApplication.isAdmin) {
                doStagesReceiveNewMechanoid(webClient)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    @Throws(ServiceException::class, NotFoundException::class, InterruptedException::class)
    private fun doStagesUpdate(webClient: MxInfo) {
        // already known on server and updated
        val trackStages = mxDatabase.trackDao().alreadyKnownAndUpdated()

        var i = 0
        for (trackStage in trackStages) {
            i++
            LoggingHelper.setMessage("update:" + i + "/" + trackStages.size)
            val restTrackStage = RESTtrackStage()
            restTrackStage.androidid = trackStage.andoridid
            restTrackStage.id = trackStage.restId.toInt()
            if (trackStage.restId > 0) {
                restTrackStage.trackId = trackStage.restId.toInt()
            }
            restTrackStage.trackname = trackStage.trackname
            if (trackStage.latitude != 0.0) {
                restTrackStage.latitude = trackStage.latitude
            }
            if (trackStage.longitude != 0.0) {
                restTrackStage.longitude = trackStage.longitude
            }
            restTrackStage.country = trackStage.country
            restTrackStage.changed = trackStage.changed
            restTrackStage.insLatitude = trackStage.insLatitude
            restTrackStage.insLongitude = trackStage.insLongitude
            restTrackStage.insDistance = trackStage.insDistance.toInt()
            restTrackStage.url = trackStage.url
            restTrackStage.fees = trackStage.fees
            restTrackStage.phone = trackStage.phone
            restTrackStage.notes = trackStage.notes
            restTrackStage.contact = trackStage.contact
            restTrackStage.licence = trackStage.licence
            restTrackStage.kidstrack = trackStage.kidstrack
            restTrackStage.openmondays = trackStage.openmondays
            restTrackStage.opentuesdays = trackStage.opentuesdays
            restTrackStage.openwednesday = trackStage.openwednesday
            restTrackStage.openthursday = trackStage.openthursday
            restTrackStage.openfriday = trackStage.openfriday
            restTrackStage.opensaturday = trackStage.opensaturday
            restTrackStage.opensunday = trackStage.opensunday
            restTrackStage.hoursmonday = trackStage.hoursmonday
            restTrackStage.hourstuesday = trackStage.hourstuesday
            restTrackStage.hourswednesday = trackStage.hourswednesday
            restTrackStage.hoursthursday = trackStage.hoursthursday
            restTrackStage.hoursfriday = trackStage.hoursfriday
            restTrackStage.hourssaturday = trackStage.hourssaturday
            restTrackStage.hourssunday = trackStage.hourssunday
            restTrackStage.tracklength = trackStage.tracklength
            restTrackStage.soiltype = trackStage.soiltype
            restTrackStage.camping = trackStage.camping
            restTrackStage.shower = trackStage.shower
            restTrackStage.cleaning = trackStage.cleaning
            restTrackStage.electricity = trackStage.electricity
            restTrackStage.supercross = trackStage.supercross
            restTrackStage.trackaccess = trackStage.trackaccess
            restTrackStage.facebook = trackStage.facebook

            restTrackStage.adress = trackStage.adress
            restTrackStage.feescamping = trackStage.feescamping
            restTrackStage.daysopen = trackStage.daysopen
            restTrackStage.noiselimit = trackStage.noiselimit
            restTrackStage.campingrvhookups = trackStage.campingrvrvhookup
            restTrackStage.singletrack = trackStage.singletracks
            restTrackStage.mxtrack = trackStage.mxtrack
            restTrackStage.a4x4 = trackStage.a4x4
            restTrackStage.enduro = trackStage.enduro
            restTrackStage.utv = trackStage.utv
            restTrackStage.quad = trackStage.quad
            restTrackStage.trackstatus = trackStage.trackstatus
            restTrackStage.areatype = trackStage.areatype
            restTrackStage.schwierigkeit = trackStage.schwierigkeit
            val request = PutTrackstageRequest(trackStage.restId, restTrackStage)
            val res = webClient.putTrackstage(request)
            res.checkResponseCode(Response.HTTP_NO_CONTENT)
            trackStage.changed = 0
            mxDatabase.trackStageDao().update(trackStage)
            Wait.delay()
        }
        LoggingHelper.setMessage("")
    }

    @SuppressLint("HardwareIds")
    @Throws(Exception::class)
    private fun doSyncTracksBlock(context: OperationContext, updateProvider: Boolean, previousImported: Int, flavor: String): Int {
        val opName = "Tracks"
        val ip: String
        var res: Int
        operationContext = context
        try {
//            val maxCreated = SQuery.newQuery().firstInt(Tracks.CONTENT_URI, "max(" + Tracks.CHANGED + ")")
            val maxCreated = mxDatabase.trackDao().getNewest()

            if (maxCreated == 0) {
                LoggingHelper.setMessage("$IMPORT_REC $opName")
            }
            var androidId = ""
            if (MxCoreApplication.isEmulator) {
                androidId = "Emulator|"
            }
            if (TrackingApplication.isDebug) {
                androidId += "Debug|"
            }
            if (MxCoreApplication.isAdmin) {
                androidId += "Admin|"
            }
            if (MxCoreApplication.isAdmin) {
                androidId += "Admin|"
            }
            ip = NetworkUtils.getIPAddress(true)

            androidId += Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
            val tracksResponse = dataManagerApp.getTracks(
                maxCreated,
                androidId,
                TrackingApplication.getVersion(context.applicationContext),
                Build.VERSION.SDK_INT,
                Locale.getDefault().country, flavor, ip
            )

            tracksResponse.checkResponseCodeOk()

            LoggingHelper.setMessage("$IMPORT_REC $opName $previousImported/${(tracksResponse.body()!!.size + previousImported)} ...")

            var zlrInserted = 0
            val contentValuesTrackList = ArrayList<ContentValues>()
            val trackList = mutableListOf<Track>()
            res = tracksResponse.body()!!.size
            zlrInserted = proceedTracksMechanoid(
                tracksResponse = tracksResponse.body()!!,
                zlrInserted = zlrInserted,
                contentValuesTrackList = contentValuesTrackList,
                opName = opName,
                previousImported = previousImported,
                updateProvider = updateProvider,
                operationContext = operationContext,
                lastKnown = lastKnown
            )
            zlrInserted = proceedTracksRoom(
                tracksResponse = tracksResponse.body()!!,
                zlrInserted = zlrInserted,
                trackList = trackList,
                opName = opName,
                previousImported = previousImported
            )
            // clean up
            if (zlrInserted > 0) {
                bulkInsertMechanoid(
                    context = context.applicationContext,
                    contentValuesList = contentValuesTrackList,
                    uri = Tracks.CONTENT_URI,
                    ges = tracksResponse.body()!!.size,
                    was = opName,
                    previousImported = previousImported
                )
                bulkInsertTrackRoom(
                    trackList = trackList,
                    ges = tracksResponse.body()!!.size,
                    was = opName,
                    previousImported = previousImported
                )
            }
            Timber.i("$opName from $maxCreated overall ${(if (tracksResponse.body() != null) tracksResponse.body()!!.size else 0)}")

        } catch (e: SocketTimeoutException) {
            Timber.w("${e.javaClass.simpleName} ${e.message}")
            res = 0
        } catch (e: UnknownHostException) {
            Timber.w("${e.javaClass.simpleName} ${e.message}")
            res = 0
        } catch (e: Exception) {
            throw Exception(e.javaClass.toString() + " " + e.message)
        }

        return res
    }

    private fun proceedTracksRoom(
        tracksResponse: List<TrackR>,
        zlrInserted: Int,
        trackList: MutableList<Track>,
        opName: String,
        previousImported: Int,
    ): Int {
        var trackName = ""
        var zlrUpdated = 0
        var zlrInsertedReturn = zlrInserted
        try {
            val initial = mxDatabase.trackDao().countAll() == 0

            for (trackREST in tracksResponse) {
                if (operationContext.isAborted) {
                    return 0
                }
                trackName = trackREST.id.toString() + ":" + trackREST.trackname
                zlrUpdated++
                var oneDeclinedAtLeast = false

                var roomId = 0L
                if (!initial) {
                    roomId = mxDatabase.trackDao().getIdByRestId(trackREST.id!!.toLong())
                }

                if (roomId == 0L) { // new record
                    zlrInsertedReturn++
                    if (trackREST.approved == -1) {
                        // wenn der einzige übertragene ein abgelehnter ist, muss man ihn importieren, damit man in keine endlosschleife kommt
                        oneDeclinedAtLeast = true
                    }

                    // skip during initial all approved to speed it up
                    if (trackREST.approved == -1 && initial) {
                        continue
                    }

                    if (trackREST.approved > -1 || oneDeclinedAtLeast) {
                        val newTrack = Track()

                        newTrack.restId = trackREST.id!!.toLong()
                        newTrack.changed = trackREST.changed!!.toLong()
                        newTrack.trackname = trackREST.trackname
                        newTrack.longitude = SecHelper.cryptXtude(trackREST.longitude!!)
                        newTrack.latitude = SecHelper.cryptXtude(trackREST.latitude!!)
                        newTrack.approved = trackREST.approved!!
                        trackREST.country?.let {
                            newTrack.country = it
                        } ?: run {
                            ""
                        }
                        newTrack.url = SecHelper.encryptB64(trackREST.url)
                        newTrack.facebook = SecHelper.encryptB64(trackREST.facebook)
                        newTrack.fees = trackREST.fees
                        newTrack.phone = SecHelper.encryptB64(trackREST.phone)
                        newTrack.notes = trackREST.notes
                        newTrack.contact = SecHelper.encryptB64(trackREST.contact)
                        newTrack.metatext = trackREST.metatext
                        newTrack.licence = trackREST.licence
                        newTrack.kidstrack = trackREST.kidstrack!!
                        newTrack.openmondays = trackREST.openmondays!!
                        newTrack.opentuesdays = trackREST.opentuesdays!!
                        newTrack.openwednesday = trackREST.openwednesday!!
                        newTrack.openthursday = trackREST.openthursday!!
                        newTrack.openfriday = trackREST.openfriday!!
                        newTrack.opensaturday = trackREST.opensaturday!!
                        newTrack.opensunday = trackREST.opensunday!!
                        newTrack.hoursmonday = trackREST.hoursmonday
                        newTrack.hourstuesday = trackREST.hourstuesday
                        newTrack.hourswednesday = trackREST.hourswednesday
                        newTrack.hoursthursday = trackREST.hoursthursday
                        newTrack.hoursfriday = trackREST.hoursfriday
                        newTrack.hourssaturday = trackREST.hourssaturday
                        newTrack.hourssunday = trackREST.hourssunday
                        newTrack.tracklength = trackREST.tracklength!!
                        newTrack.soiltype = trackREST.soiltype!!
                        newTrack.camping = trackREST.camping!!
                        newTrack.shower = trackREST.shower!!
                        newTrack.cleaning = trackREST.cleaning!!
                        newTrack.electricity = trackREST.electricity!!
                        newTrack.supercross = trackREST.supercross!!

                        newTrack.feescamping = trackREST.feescamping
                        newTrack.daysopen = trackREST.daysopen
                        newTrack.noiselimit = trackREST.noiselimit
                        newTrack.campingrvrvhookup = trackREST.campingrvrvhookup!!
                        newTrack.singletracks = trackREST.singletracks!!
                        newTrack.mxtrack = trackREST.mxtrack!!
                        newTrack.a4x4 = trackREST.a4x4!!
                        newTrack.enduro = trackREST.enduro!!
                        newTrack.utv = trackREST.utv!!
                        newTrack.quad = trackREST.quad!!
                        newTrack.trackstatus = trackREST.trackstatus
                        newTrack.areatype = trackREST.areatype
                        newTrack.schwierigkeit = trackREST.schwierigkeit!!
                        newTrack.indoor = trackREST.indoor!!

                        if (trackREST.trackaccess != null) {
                            newTrack.trackaccess = trackREST.trackaccess
                        }
                        newTrack.logoURL = trackREST.logourl
                        newTrack.showroom = trackREST.showroom!!
                        newTrack.workshop = trackREST.workshop!!
                        newTrack.validuntil = trackREST.validuntil!!
                        newTrack.brands = trackREST.brands
                        newTrack.adress = SecHelper.encryptB64(trackREST.adress)
                        if (lastKnown != null) {
                            val locTrack = Location("calc")
                            locTrack.latitude = trackREST.latitude!!
                            locTrack.longitude = trackREST.longitude!!
                            newTrack.distance2location = lastKnown!!.distanceTo(locTrack).toInt()
                        }
//                        mxDatabase.trackDao().insertTrack(newTrack) TODO check if this is possible without id and if it is faster than bulk insert
                        trackList.add(newTrack)
                    }

                    // bulk insert
                    if (zlrUpdated > BLOCK_SIZE) {
                        zlrUpdated = bulkInsertTrackRoom(
                            trackList = trackList,
                            ges = tracksResponse.size,
                            was = opName,
                            previousImported = previousImported
                        )
                    }
                } else {
                    val currentTrack = mxDatabase.trackDao().getById(roomId)
//                    currentTrack!!.restId = trackREST.id!!.toLong()
                    currentTrack!!.changed = trackREST.changed!!.toLong()
                    currentTrack.trackname = trackREST.trackname
                    currentTrack.longitude = SecHelper.cryptXtude(trackREST.longitude!!)
                    currentTrack.latitude = SecHelper.cryptXtude(trackREST.latitude!!)
                    currentTrack.approved = trackREST.approved!!
                    trackREST.country?.let {
                        currentTrack.country = it
                    } ?: run {
                        ""
                    }
                    currentTrack.url = SecHelper.encryptB64(trackREST.url)
                    currentTrack.facebook = SecHelper.encryptB64(trackREST.facebook)
                    currentTrack.fees = trackREST.fees
                    currentTrack.phone = SecHelper.encryptB64(trackREST.phone)
                    currentTrack.notes = trackREST.notes
                    currentTrack.contact = SecHelper.encryptB64(trackREST.contact)
                    currentTrack.metatext = trackREST.metatext
                    currentTrack.licence = trackREST.licence
                    currentTrack.kidstrack = trackREST.kidstrack!!
                    currentTrack.openmondays = trackREST.openmondays!!
                    currentTrack.opentuesdays = trackREST.opentuesdays!!
                    currentTrack.openwednesday = trackREST.openwednesday!!
                    currentTrack.openthursday = trackREST.openthursday!!
                    currentTrack.openfriday = trackREST.openfriday!!
                    currentTrack.opensaturday = trackREST.opensaturday!!
                    currentTrack.opensunday = trackREST.opensunday!!
                    currentTrack.hoursmonday = trackREST.hoursmonday
                    currentTrack.hourstuesday = trackREST.hourstuesday
                    currentTrack.hourswednesday = trackREST.hourswednesday
                    currentTrack.hoursthursday = trackREST.hoursthursday
                    currentTrack.hoursfriday = trackREST.hoursfriday
                    currentTrack.hourssaturday = trackREST.hourssaturday
                    currentTrack.hourssunday = trackREST.hourssunday
                    currentTrack.tracklength = trackREST.tracklength!!
                    currentTrack.soiltype = trackREST.soiltype!!
                    currentTrack.camping = trackREST.camping!!
                    currentTrack.shower = trackREST.shower!!
                    currentTrack.cleaning = trackREST.cleaning!!
                    currentTrack.electricity = trackREST.electricity!!
                    currentTrack.supercross = trackREST.supercross!!
                    if (trackREST.trackaccess != null) {
                        currentTrack.trackaccess = trackREST.trackaccess
                    }
                    currentTrack.logoURL = trackREST.logourl
                    currentTrack.showroom = trackREST.showroom!!
                    currentTrack.workshop = trackREST.workshop!!
                    currentTrack.validuntil = trackREST.validuntil!!
                    currentTrack.brands = trackREST.brands
                    currentTrack.adress = trackREST.adress
                    currentTrack.feescamping = trackREST.feescamping
                    currentTrack.daysopen = trackREST.daysopen
                    currentTrack.noiselimit = trackREST.noiselimit
                    currentTrack.campingrvrvhookup = trackREST.campingrvrvhookup!!
                    currentTrack.singletracks = trackREST.singletracks!!
                    currentTrack.mxtrack = trackREST.mxtrack!!
                    currentTrack.a4x4 = trackREST.a4x4!!
                    currentTrack.enduro = trackREST.enduro!!
                    currentTrack.utv = trackREST.utv!!
                    currentTrack.quad = trackREST.quad!!
                    currentTrack.trackstatus = trackREST.trackstatus
                    currentTrack.areatype = trackREST.areatype
                    currentTrack.schwierigkeit = trackREST.schwierigkeit!!
                    currentTrack.indoor = trackREST.indoor!!

                    if (lastKnown != null) {
                        val locTrack = Location("calc")
                        locTrack.latitude = SecHelper.cryptXtude(trackREST.latitude!!)
                        locTrack.longitude = SecHelper.cryptXtude(trackREST.longitude!!)
                        currentTrack.distance2location = lastKnown!!.distanceTo(locTrack).toInt()
                    }
                    mxDatabase.trackDao().update(currentTrack)
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message + " $trackName")
        }
        return zlrInsertedReturn
    }

    @Throws(Exception::class)
    private fun doSyncComments(context: OperationContext) {
        val opName = "Comments"
        val maxCreated = mxDatabase.commentDao().maxChangedDate()

        val ratingsResponse = dataManagerApp.getRatings(maxCreated / 1000)

        ratingsResponse.checkResponseCodeOk()

        LoggingHelper.setMessage("$DOWNLOAD $opName")
        var zlrUpdated = 0
        imported = 0
        val commentList = mutableListOf<Comment>()
        for (trackREST in ratingsResponse.body()!!) {
            if (context.isAborted) {
                return
            }

            zlrUpdated++

            mxDatabase.commentDao().byRestId(trackREST.id.toLong())?.let { comment ->
                comment.restId = trackREST.id.toLong()
                comment.changed = trackREST.changed.toLong() * 1000
                comment.country = trackREST.country
                comment.deleted = trackREST.deleted
                comment.note = trackREST.note
                comment.rating = trackREST.rating
                comment.trackId = trackREST.trackId!!.toLong()
                comment.username = trackREST.username
                comment.approved = trackREST.approved
                comment.androidid = trackREST.androidid
                mxDatabase.commentDao().update(comment)
            } ?: run {
                // new entry
                val comment = Comment()

                comment.restId = trackREST.id.toLong()
                comment.changed = trackREST.changed.toLong() * 1000
                comment.country = trackREST.country
                comment.deleted = trackREST.deleted
                comment.note = trackREST.note ?: ""
                comment.rating = trackREST.rating
                comment.trackId = trackREST.trackId!!.toLong()
                comment.username = trackREST.username ?: ""
                comment.approved = trackREST.approved
                comment.androidid = trackREST.androidid
                commentList.add(comment)

                // bulk insert
                if (zlrUpdated > BLOCK_SIZE) {
                    zlrUpdated = bulkInsertCommentRoom(
                        commentList = commentList,
                        ges = ratingsResponse.body()!!.size,
                        previousImported = imported
                    )
                }
            }
        }
        // clean up
        if (commentList.isNotEmpty()) {
            bulkInsertCommentRoom(
                commentList = commentList,
                ges = ratingsResponse.body()!!.size,
                previousImported = imported
            )
        }
        Timber.i("$opName all ${(if (ratingsResponse.body() != null) ratingsResponse.body()!!.size else 0)} updated $zlrUpdated")
    }

    private fun doBuildCountryTableRoom(context: Context): String {
        val result = ""
        val deleteCount = mxDatabase.countryDao().cleanupFromEmptyCounties()
        Timber.d("Country deleted $deleteCount")

        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mxDatabase.trackDao().countrySum.forEach { countrySum ->
            val countryExist = mxDatabase.countryDao().exists(if (countrySum.country == "") "\"\"" else countrySum.country)
            if (!countryExist) {
                val countryEntity = info.mx.tracks.room.entity.Country()
                if (countrySum.country != "") {
                    countryEntity.country = countrySum.country
                }
                var country = Locale.getDefault().country
                if (country.length < 2) {
                    country = "DE"
                }
                var showCountry = if (country == countrySum.country) 1 else 0
                val isoSim = telephonyManager.simCountryIso
                val isoNetwork = telephonyManager.networkCountryIso
                if (country == isoSim) {
                    showCountry = 1
                }
                if (country == isoNetwork) {
                    showCountry = 1
                }

                countryEntity.show = showCountry
                mxDatabase.countryDao().insertCountry(countryEntity)
                Timber.d("${countryEntity.country} Country added ${countryEntity.show}")
            }
        }

        //reset countries to show
        if (!MxPreferences.instance.firstTimeLocation && locationHelper.isAmericaShown) {
            //FIXME for US
            // when locations is confirmed and we are in US
            // currently to nothing. Flavor US should handle this
            Timber.d("We are not first time, let's keep America")
        } else if (!MxPreferences.instance.firstTimeCountry) {
            if (mxDatabase.countryDao().allShown.size > 2) {
                Timber.d("We are not firstTimeCountry, hide Europe")
                locationHelper.hideAmerica()
                MxPreferences.instance.firstTimeCountry = true
            } else {
                Timber.d("We are not firstTimeCountry, Nothing to do with hide/show countries")
            }
        } else
            Timber.d("Nothing to do with hide/show countries")

        mxDatabase.countryDao().deleteByCountryCode("zz")
        return result
    }

    private fun requestLastLocation() {
        try {
            if (!(ActivityCompat.checkSelfPermission(
                    operationContext.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            operationContext.applicationContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED)
            ) {
                LocationServices.getFusedLocationProviderClient(operationContext.applicationContext).lastLocation
                    .addOnSuccessListener { last -> lastKnown = last }
            }
        } catch (_: Exception) {
        }
    }

    private fun bulkInsertTrackRoom(
        trackList: MutableList<Track>,
        ges: Int,
        was: String,
        previousImported: Int
    ): Int {
        val proceedCount = trackList.size
        imported += trackList.size
        LoggingHelper.setMessage(IMPORT_TRACK_ROOM + " " + was + " " + imported + "/" + (ges + previousImported))
//        mxDatabase.runInTransaction {
        // for better performance with large lists, *trackList.toTypedArray() converts to a vararg parameter
        // trackList.toTypedArray() - Converts the list to an Array<Track>
        // * (spread operator) - Unpacks the array into individual arguments
        mxDatabase.trackDao().insertTracksAll(*trackList.toTypedArray())
//        }
        trackList.clear()
        return proceedCount
    }

    private fun bulkInsertCommentRoom(
        commentList: MutableList<Comment>,
        ges: Int,
        previousImported: Int
    ): Int {
        val proceedCount = commentList.size
        imported += commentList.size
        LoggingHelper.setMessage(IMPORT_COMMENT_ROOM + " Comments " + imported + "/" + (ges + previousImported))
//        mxDatabase.runInTransaction {
        // for better performance with large lists, *trackList.toTypedArray() converts to a vararg parameter
        // trackList.toTypedArray() - Converts the list to an Array<Track>
        // * (spread operator) - Unpacks the array into individual arguments
        mxDatabase.commentDao().insertCommentsAll(*commentList.toTypedArray())
//        }
        commentList.clear()
        return proceedCount
    }

    companion object {
        var importStatusMessage: MutableLiveData<ImportStatusMessage> = MutableLiveData()
        var adminImportStatusCalMessage: MutableLiveData<ImportStatusMessage> = MutableLiveData()

        internal const val IMPORT_REC = "Import"
        private const val IMPORT_TRACK_ROOM = "Room Import track"
        private const val IMPORT_COMMENT_ROOM = "Room Import comment"
        private const val DOWNLOAD = "Download/Import"

        const val COUNTRY_RESULT = "country_result"
        var LOCATION = "location"
        var SOURCE = "source"
        private const val BLOCK_SIZE = 49
        const val RECALC_TRACKS = "recalcTracks"
        internal var imported = 0

        private fun deleteFile(localThumb: String) {
            if (localThumb != "") {
                val toDel = File(localThumb)
                if (toDel.exists()) {
                    toDel.delete()
                }
            }
        }

        @Throws(RemoteException::class, OperationApplicationException::class)
        private fun doApplyBatchRoom(
            mxDatabase: MxDatabase,
            listPictures: MutableList<Picture>,
            ges: Int,
            @Suppress("SameParameterValue") previousImported: Int
        ): Int {
            imported += listPictures.size
            mxDatabase.pictureDao().insertPicturesAll(listPictures)
            val zlr = 0
            LoggingHelper.setMessage(IMPORT_REC + " Pictures " + imported + "/" + (ges + previousImported))
            listPictures.clear()
            return zlr
        }
    }
}
