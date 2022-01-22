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
import android.net.Uri
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
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
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
import info.mx.core.ops.ImportHelper
import info.mx.core.ops.ImportIdlingResource
import info.mx.core.rest.MxInfo
import info.mx.core.util.LocationHelper
import info.mx.core.util.NetworkUtils
import info.mx.core.util.Wait
import info.mx.core.util.checkResponseCodeOk
import info.mx.core_generated.ops.AbstractOpPostImagesOperation
import info.mx.core_generated.ops.AbstractOpSyncFromServerOperation
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.rest.GetTracksStageFromRequest
import info.mx.core_generated.rest.PostNetworkErrorsRequest
import info.mx.core_generated.rest.PostNetworkErrorsResult
import info.mx.core_generated.rest.PostTrackstageIDRequest
import info.mx.core_generated.rest.PutTrackstageRequest
import info.mx.core_generated.rest.RESTnetworkError
import info.mx.core_generated.rest.RESTtrackStage
import info.mx.core_generated.sqlite.AbstractMxInfoDBOpenHelper
import info.mx.core_generated.sqlite.CountryRecord
import info.mx.core_generated.sqlite.CountrysumRecord
import info.mx.core_generated.sqlite.MxInfoDBContract.*
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.core_generated.sqlite.TrackstageRecord
import info.mx.tracks.MxAccessApplication.Companion.aadhresU
import info.mx.tracks.common.SecHelper
import info.mx.tracks.data.DataManagerApp
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Picture
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
                countryResult = doBuildCountryTable(context.applicationContext)
                imported = 0
                Thread.sleep(4000)

                if (lastKnown != null) {
                    calcDistance(context.applicationContext, lastKnown!!)
                }

                doSyncPictures(context)
                imported = 0
                doPushNetworkErrorsAtOnce(webClient)

                val intentM = AbstractOpPostImagesOperation.newIntent()
                Ops.execute(intentM)

                doCleanFromDecline()

                MxPreferences.getInstance().edit().putLastSyncTime(System.currentTimeMillis()).apply()
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
                var zlrInserted = 0
                val contentValuesTrackList = ArrayList<ContentValues>()
                val opName = "Tracks Init"

                zlrInserted = proceedTracks(trackRS, zlrInserted, contentValuesTrackList, opName, 0, true)

                // clean up
                if (zlrInserted > 0) {
                    bulkInsert(operationContext.applicationContext, contentValuesTrackList, Tracks.CONTENT_URI, trackRS.size, opName, 0)
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
        // Closing inputstream also checks for CRC of the the just extracted
        // file. If CRC check has to be skipped (for ex: to cancel the unzip
        // operation, etc) use method is.close(boolean skipCRCCheck) and set the
        // flag, skipCRCCheck to false
        // NOTE: It is recommended to close outputStream first because Zip4j
        // throws an exception if CRC check fails
        `is`?.close()
    }

    private fun calcDistance(context: Context, location: Location) {
        val recalculateTracks = Intent(RECALC_TRACKS)
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

                if (localPicture == null) { // neuanlage
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
                        zlrUpdated = doApplyBatchRoom(mxDatabase, opsTracks, picturesResponse.body()!!.size, opName, 0)
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
                doApplyBatchRoom(mxDatabase, opsTracks, picturesResponse.body()!!.size, opName, 0)
            }
            LoggingHelper.setMessage("") // dies SysncPictures wird auch vom pushImages aufgerufen
            Timber.i("$opName gesamt ${(if (picturesResponse.body() != null) picturesResponse.body()!!.size else 0)} updated: $zlrUpdated")
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
        val emptyTracks = mxDatabase.trackDao().emptyCountry("")
        for (track in emptyTracks) {
            val coder = Geocoder(context)
            try {
                val addresses = getAddressesFromLocation(coder, SecHelper.entcryptXtude(track.latitude), SecHelper.entcryptXtude(track.longitude), 1)
                if (addresses != null && addresses.size > 0) {
                    val country = addresses[0].countryCode
                    Timber.d("${track.trackname} country:$country")
                    val stage = TrackStage()
                    stage.trackId = track.id!!
                    stage.country = country
                    mxDatabase.trackStageDao().insertTrackStagesAll(stage)
                    track.country = country
                    mxDatabase.trackDao().update(track)
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
    }

    private fun doHandleTrackStage(context: Context, webClient: MxInfo) {
        try {
            doStagesUpdate(webClient) //put
            doStagesPush(context, webClient)
            if (MxCoreApplication.isAdmin) {
                doStagesReceiveNew(webClient)
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
            val restTrackstage = RESTtrackStage()
            restTrackstage.androidid = trackStage.andoridid
            restTrackstage.id = trackStage.restId.toInt()
            if (trackStage.restId > 0) {
                restTrackstage.trackId = trackStage.restId.toInt()
            }
            restTrackstage.trackname = trackStage.trackname
            if (trackStage.latitude != 0.0) {
                restTrackstage.latitude = trackStage.latitude
            }
            if (trackStage.longitude != 0.0) {
                restTrackstage.longitude = trackStage.longitude
            }
            restTrackstage.country = trackStage.country
            restTrackstage.changed = trackStage.changed
            restTrackstage.insLatitude = trackStage.insLatitude
            restTrackstage.insLongitude = trackStage.insLongitude
            restTrackstage.insDistance = trackStage.insDistance.toInt()
            restTrackstage.url = trackStage.url
            restTrackstage.fees = trackStage.fees
            restTrackstage.phone = trackStage.phone
            restTrackstage.notes = trackStage.notes
            restTrackstage.contact = trackStage.contact
            restTrackstage.licence = trackStage.licence
            restTrackstage.kidstrack = trackStage.kidstrack
            restTrackstage.openmondays = trackStage.openmondays
            restTrackstage.opentuesdays = trackStage.opentuesdays
            restTrackstage.openwednesday = trackStage.openwednesday
            restTrackstage.openthursday = trackStage.openthursday
            restTrackstage.openfriday = trackStage.openfriday
            restTrackstage.opensaturday = trackStage.opensaturday
            restTrackstage.opensunday = trackStage.opensunday
            restTrackstage.hoursmonday = trackStage.hoursmonday
            restTrackstage.hourstuesday = trackStage.hourstuesday
            restTrackstage.hourswednesday = trackStage.hourswednesday
            restTrackstage.hoursthursday = trackStage.hoursthursday
            restTrackstage.hoursfriday = trackStage.hoursfriday
            restTrackstage.hourssaturday = trackStage.hourssaturday
            restTrackstage.hourssunday = trackStage.hourssunday
            restTrackstage.tracklength = trackStage.tracklength
            restTrackstage.soiltype = trackStage.soiltype
            restTrackstage.camping = trackStage.camping
            restTrackstage.shower = trackStage.shower
            restTrackstage.cleaning = trackStage.cleaning
            restTrackstage.electricity = trackStage.electricity
            restTrackstage.supercross = trackStage.supercross
            restTrackstage.trackaccess = trackStage.trackaccess
            restTrackstage.facebook = trackStage.facebook

            restTrackstage.adress = trackStage.adress
            restTrackstage.feescamping = trackStage.feescamping
            restTrackstage.daysopen = trackStage.daysopen
            restTrackstage.noiselimit = trackStage.noiselimit
            restTrackstage.campingrvhookups = trackStage.campingrvrvhookup
            restTrackstage.singletrack = trackStage.singletracks
            restTrackstage.mxtrack = trackStage.mxtrack
            restTrackstage.a4x4 = trackStage.a4x4
            restTrackstage.enduro = trackStage.enduro
            restTrackstage.utv = trackStage.utv
            restTrackstage.quad = trackStage.quad
            restTrackstage.trackstatus = trackStage.trackstatus
            restTrackstage.areatype = trackStage.areatype
            restTrackstage.schwierigkeit = trackStage.schwierigkeit
            val request = PutTrackstageRequest(trackStage.restId, restTrackstage)
            val res = webClient.putTrackstage(request)
            res.checkResponseCode(Response.HTTP_NO_CONTENT)
            trackStage.changed = 0
            mxDatabase.trackStageDao().update(trackStage)
            Wait.delay()
        }
        LoggingHelper.setMessage("")
    }

    @Throws(ServiceException::class, NotFoundException::class, InterruptedException::class)
    private fun doStagesPush(context: Context, webClient: MxInfo) {
        @SuppressLint("HardwareIds")
        var androidId = Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
        val notPublished = SQuery.newQuery().expr(Trackstage.REST_ID, Op.EQ, 0)
            .or()
            .append(Trackstage.REST_ID + " is null")
        val stageRecords = SQuery.newQuery()
            .expr(notPublished)
            .expr(Trackstage.UPDATED, Op.NEQ, 1)
            .select<TrackstageRecord>(Trackstage.CONTENT_URI, Trackstage._ID)
        var i = 0
        for (recordStage in stageRecords) {
            i++
            LoggingHelper.setMessage("push:" + i + "/" + stageRecords.size)
            val restTrackstage = RESTtrackStage()
            if (MxCoreApplication.isAdmin && recordStage.androidid != null && recordStage.androidid != androidId) {
                androidId = recordStage.androidid
            } else if (MxCoreApplication.isAdmin) {
                androidId = "debug"
            }
            restTrackstage.androidid = androidId
            if (recordStage.trackRestId > 0) {
                restTrackstage.trackId = recordStage.trackRestId.toInt()
            }
            if (recordStage.trackname != null) {
                restTrackstage.trackname = recordStage.trackname
            }
            if (recordStage.latitude != 0.0) {
                restTrackstage.latitude = recordStage.latitude
            }
            if (recordStage.longitude != 0.0) {
                restTrackstage.longitude = recordStage.longitude
            }
            if (recordStage.country == null || recordStage.country == "") {
                if (recordStage.latitude != 0.0) { // probably new entry
                    restTrackstage.country = ImportHelper.getShortCountryCoder(
                        recordStage.latitude,
                        recordStage.longitude, context.applicationContext
                    )
                }
                if (restTrackstage.country == null) {
                    if (recordStage.restId > 0) {
                        val trackId = SQuery.newQuery().expr(Tracks.REST_ID, Op.EQ, recordStage.restId).firstLong(
                            Tracks.CONTENT_URI, Tracks._ID
                        )
                        val origTrack = TracksRecord.get(trackId)
                        if (origTrack != null) {
                            if (origTrack.country == null || origTrack.country == "") {
                                if (origTrack.latitude != 0.0) {
                                    restTrackstage.country = ImportHelper.getShortCountryCoder(
                                        SecHelper.entcryptXtude(origTrack.latitude),
                                        SecHelper.entcryptXtude(origTrack.longitude), context.applicationContext
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                restTrackstage.country = recordStage.country
            }
            restTrackstage.changed = recordStage.created.toInt().toLong()
            restTrackstage.insLatitude = recordStage.insLatitude
            restTrackstage.insLongitude = recordStage.insLongitude
            restTrackstage.insDistance = recordStage.insDistance.toInt()
            restTrackstage.url = recordStage.url
            restTrackstage.fees = recordStage.fees
            restTrackstage.phone = recordStage.phone
            restTrackstage.notes = recordStage.notes
            restTrackstage.contact = recordStage.contact
            restTrackstage.licence = recordStage.licence
            restTrackstage.kidstrack = recordStage.kidstrack.toInt()
            restTrackstage.openmondays = recordStage.openmondays.toInt()
            restTrackstage.opentuesdays = recordStage.opentuesdays.toInt()
            restTrackstage.openwednesday = recordStage.openwednesday.toInt()
            restTrackstage.openthursday = recordStage.openthursday.toInt()
            restTrackstage.openfriday = recordStage.openfriday.toInt()
            restTrackstage.opensaturday = recordStage.opensaturday.toInt()
            restTrackstage.opensunday = recordStage.opensunday.toInt()
            restTrackstage.hoursmonday = recordStage.hoursmonday
            restTrackstage.hourstuesday = recordStage.hourstuesday
            restTrackstage.hourswednesday = recordStage.hourswednesday
            restTrackstage.hoursthursday = recordStage.hoursthursday
            restTrackstage.hoursfriday = recordStage.hoursfriday
            restTrackstage.hourssaturday = recordStage.hourssaturday
            restTrackstage.hourssunday = recordStage.hourssunday
            restTrackstage.tracklength = recordStage.tracklength.toInt()
            restTrackstage.soiltype = recordStage.soiltype.toInt()
            restTrackstage.camping = recordStage.camping.toInt()
            restTrackstage.shower = recordStage.shower.toInt()
            restTrackstage.cleaning = recordStage.cleaning.toInt()
            restTrackstage.electricity = recordStage.electricity.toInt()
            restTrackstage.supercross = recordStage.supercross.toInt()
            restTrackstage.trackaccess = recordStage.trackaccess
            restTrackstage.facebook = recordStage.facebook

            restTrackstage.adress = recordStage.adress
            restTrackstage.feescamping = recordStage.feescamping
            restTrackstage.daysopen = recordStage.daysopen
            restTrackstage.noiselimit = recordStage.noiselimit
            restTrackstage.campingrvhookups = recordStage.campingRVhookups.toInt()
            restTrackstage.singletrack = recordStage.singleTrack.toInt()
            restTrackstage.mxtrack = recordStage.mxTrack.toInt()
            restTrackstage.a4x4 = recordStage.a4X4.toInt()
            restTrackstage.enduro = recordStage.enduro.toInt()
            restTrackstage.utv = recordStage.utv.toInt()
            restTrackstage.quad = recordStage.quad.toInt()
            restTrackstage.trackstatus = recordStage.trackstatus
            restTrackstage.areatype = recordStage.areatype
            restTrackstage.indoor = recordStage.indoor.toInt()
            restTrackstage.schwierigkeit = recordStage.schwierigkeit.toInt()
            val request = PostTrackstageIDRequest(restTrackstage)
            val res = webClient.postTrackstageID(request)
            // res.checkResponseCode(204);
            res.checkResponseCodeOk()
            val resTrackStage = res.parse()
            if (MxCoreApplication.isAdmin) {
                recordStage.restId = resTrackStage.insertResponse.id.toLong()
                recordStage.trackRestId = resTrackStage.insertResponse.trackRestId.toLong()
                recordStage.approved = 1
                recordStage.save(false)
            } else {
                recordStage.delete(false)
            }
            Wait.delay()
        }
        LoggingHelper.setMessage("")
    }

    @Throws(ServiceException::class)
    private fun doStagesReceiveNew(webClient: MxInfo) {
        if (MxCoreApplication.isAdmin) {
            LoggingHelper.setMessage("Stage DOWNLOAD")
            val maxStageID = SQuery.newQuery().firstInt(Trackstage.CONTENT_URI, "max(" + Trackstage.CREATED + ")").toLong()
            val requestChanged = GetTracksStageFromRequest(maxStageID)
            val resChanged = webClient.getTracksStageFrom(requestChanged)
            LoggingHelper.setMessage("Stage parse")
            val resultStages = resChanged.parse()
            resChanged.checkResponseCodeOk()
            var zlr = 0
            for (stageNet in resultStages.resTtrackStages) {
                zlr++
                LoggingHelper.setMessage("Stage " + zlr + "/" + resultStages.resTtrackStages.size)
                var stageRec: TrackstageRecord? = SQuery.newQuery().expr(Trackstage.REST_ID, Op.EQ, stageNet.id)
                    .selectFirst(Trackstage.CONTENT_URI)
                if (stageRec == null) {
                    stageRec = TrackstageRecord()
                }

                //                ContentValues stageContent = stageNet.toContentValues();
                //                SQuery.newQuery()
                //                        .expr(PictureStage.REST_ID, Op.GT, 0)
                //                        .update(PictureStage.CONTENT_URI, valuesImg);
                //                stageRec.
                stageRec.restId = stageNet.id.toLong()
                stageRec.trackRestId = stageNet.trackId.toLong()
                stageRec.androidid = stageNet.androidid
                stageRec.approved = stageNet.approved.toLong()
                stageRec.created = stageNet.changed
                stageRec.country = stageNet.country
                stageRec.trackname = stageNet.trackname
                stageRec.longitude = stageNet.longitude
                stageRec.latitude = stageNet.latitude
                stageRec.insLongitude = stageNet.insLongitude
                stageRec.insLatitude = stageNet.insLatitude
                stageRec.insDistance = stageNet.insDistance.toLong()
                stageRec.url = stageNet.url
                stageRec.phone = stageNet.phone
                stageRec.notes = stageNet.notes
                stageRec.contact = stageNet.contact
                stageRec.licence = stageNet.licence
                stageRec.kidstrack = stageNet.kidstrack.toLong()
                stageRec.openmondays = stageNet.openmondays.toLong()
                stageRec.opentuesdays = stageNet.opentuesdays.toLong()
                stageRec.openwednesday = stageNet.openwednesday.toLong()
                stageRec.openthursday = stageNet.openthursday.toLong()
                stageRec.openfriday = stageNet.openfriday.toLong()
                stageRec.opensaturday = stageNet.opensaturday.toLong()
                stageRec.opensunday = stageNet.opensunday.toLong()
                stageRec.hoursmonday = stageNet.hoursmonday
                stageRec.hourstuesday = stageNet.hourstuesday
                stageRec.hourswednesday = stageNet.hourswednesday
                stageRec.hoursthursday = stageNet.hoursthursday
                stageRec.hoursfriday = stageNet.hoursfriday
                stageRec.hourssaturday = stageNet.hourssaturday
                stageRec.hourssunday = stageNet.hourssunday
                stageRec.tracklength = stageNet.tracklength.toLong()
                stageRec.soiltype = stageNet.soiltype.toLong()
                stageRec.camping = stageNet.camping.toLong()
                stageRec.shower = stageNet.shower.toLong()
                stageRec.cleaning = stageNet.cleaning.toLong()
                stageRec.electricity = stageNet.electricity.toLong()
                stageRec.supercross = stageNet.supercross.toLong()
                stageRec.trackaccess = stageNet.trackaccess
                stageRec.facebook = stageNet.facebook
                stageRec.fees = stageNet.fees
                stageRec.adress = stageNet.adress
                stageRec.feescamping = stageNet.feescamping
                stageRec.daysopen = stageNet.daysopen
                stageRec.noiselimit = stageNet.noiselimit
                // stageRec.setRating (stageNet.getRating ());
                stageRec.campingRVhookups = stageNet.campingrvhookups.toLong()
                stageRec.singleTrack = stageNet.singletrack.toLong()
                stageRec.mxTrack = stageNet.mxtrack.toLong()
                stageRec.a4X4 = stageNet.a4x4.toLong()
                stageRec.enduro = stageNet.enduro.toLong()
                stageRec.utv = stageNet.utv.toLong()
                stageRec.quad = stageNet.quad.toLong()
                stageRec.trackstatus = stageNet.trackstatus
                stageRec.areatype = stageNet.areatype
                stageRec.schwierigkeit = stageNet.schwierigkeit.toLong()
                stageRec.indoor = stageNet.indoor.toLong()
                stageRec.save(false)
            }
            LoggingHelper.setMessage("")
        }
    }

    @SuppressLint("HardwareIds")
    @Throws(Exception::class)
    private fun doSyncTracksBlock(context: OperationContext, updateProvider: Boolean, vorherImportiert: Int, flavor: String): Int {
        val opName = "Tracks"
        val ip: String
        var res: Int
        operationContext = context
        try {
            val maxCreated = SQuery.newQuery().firstInt(Tracks.CONTENT_URI, "max(" + Tracks.CHANGED + ")")

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

            LoggingHelper.setMessage(
                IMPORT_REC + " " + opName + " " +
                        vorherImportiert + "/" + (tracksResponse.body()!!.size + vorherImportiert) + " ..."
            )

            var zlrInserted = 0
            val contentValuesTrackList = ArrayList<ContentValues>()
            res = tracksResponse.body()!!.size
            zlrInserted = proceedTracks(tracksResponse.body()!!, zlrInserted, contentValuesTrackList, opName, vorherImportiert, updateProvider)
            // clean up
            if (zlrInserted > 0) {
                bulkInsert(
                    context.applicationContext, contentValuesTrackList,
                    Tracks.CONTENT_URI, tracksResponse.body()!!.size, opName, vorherImportiert
                )
            }
            Timber.i("$opName overall ${(if (tracksResponse.body() != null) tracksResponse.body()!!.size else 0)}")

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

    private fun proceedTracks(
        tracksResponse: List<TrackR>,
        zlrInserted: Int,
        contentValuesTrackList: ArrayList<ContentValues>,
        opName: String,
        vorherImportiert: Int,
        updateProvider: Boolean
    ): Int {
        var trackName = ""
        var zlrUpdated = 0
        var zlrInsertedReturn = zlrInserted
        try {
            val initial = SQuery.newQuery().count(Tracks.CONTENT_URI) == 0

            for (trackREST in tracksResponse) {
                if (operationContext.isAborted) {
                    return 0
                }
                trackName = trackREST.id.toString() + ":" + trackREST.trackname
                zlrUpdated++
                var oneDeclinedAtLeast = false

                var restId = 0
                if (!initial) {
                    restId = SQuery.newQuery()
                        .expr(Tracks.REST_ID, Op.EQ, trackREST.id!!)
                        .firstInt(Tracks.CONTENT_URI, Tracks._ID)
                }

                if (restId == 0) { // new record
                    zlrInsertedReturn++
                    if (trackREST.approved == -1 && !oneDeclinedAtLeast) {
                        // wenn der einzige übertragene ein abgelehnter ist, muss man ihn importieren, damit man in keine endlosschleife kommt
                        oneDeclinedAtLeast = true
                    }

                    // skip during inital all approved to speed it up
                    if (trackREST.approved == -1 && initial) {
                        continue
                    }

                    if (trackREST.approved > -1 || oneDeclinedAtLeast) {
                        val builderTrack = Tracks.newBuilder()

                        builderTrack.setRestId(trackREST.id!!.toLong())
                        builderTrack.setChanged(trackREST.changed!!.toLong())
                        builderTrack.setTrackname(trackREST.trackname)
                        builderTrack.setLongitude(SecHelper.cryptXtude(trackREST.longitude!!))
                        builderTrack.setLatitude(SecHelper.cryptXtude(trackREST.latitude!!))
                        builderTrack.setApproved(trackREST.approved!!.toLong())
                        builderTrack.setCountry(trackREST.country)
                        builderTrack.setUrl(SecHelper.encryptB64(trackREST.url))
                        builderTrack.setFacebook(SecHelper.encryptB64(trackREST.facebook))
                        builderTrack.setFees(trackREST.fees)
                        builderTrack.setPhone(SecHelper.encryptB64(trackREST.phone))
                        builderTrack.setNotes(trackREST.notes)
                        builderTrack.setContact(SecHelper.encryptB64(trackREST.contact))
                        builderTrack.setMetatext(trackREST.metatext)
                        builderTrack.setLicence(trackREST.licence)
                        builderTrack.setKidstrack(trackREST.kidstrack!!.toLong())
                        builderTrack.setOpenmondays(trackREST.openmondays!!.toLong())
                        builderTrack.setOpentuesdays(trackREST.opentuesdays!!.toLong())
                        builderTrack.setOpenwednesday(trackREST.openwednesday!!.toLong())
                        builderTrack.setOpenthursday(trackREST.openthursday!!.toLong())
                        builderTrack.setOpenfriday(trackREST.openfriday!!.toLong())
                        builderTrack.setOpensaturday(trackREST.opensaturday!!.toLong())
                        builderTrack.setOpensunday(trackREST.opensunday!!.toLong())
                        builderTrack.setHoursmonday(trackREST.hoursmonday)
                        builderTrack.setHourstuesday(trackREST.hourstuesday)
                        builderTrack.setHourswednesday(trackREST.hourswednesday)
                        builderTrack.setHoursthursday(trackREST.hoursthursday)
                        builderTrack.setHoursfriday(trackREST.hoursfriday)
                        builderTrack.setHourssaturday(trackREST.hourssaturday)
                        builderTrack.setHourssunday(trackREST.hourssunday)
                        builderTrack.setTracklength(trackREST.tracklength!!.toLong())
                        builderTrack.setSoiltype(trackREST.soiltype!!.toLong())
                        builderTrack.setCamping(trackREST.camping!!.toLong())
                        builderTrack.setShower(trackREST.shower!!.toLong())
                        builderTrack.setCleaning(trackREST.cleaning!!.toLong())
                        builderTrack.setElectricity(trackREST.electricity!!.toLong())
                        builderTrack.setSupercross(trackREST.supercross!!.toLong())

                        builderTrack.setFeescamping(trackREST.feescamping)
                        builderTrack.setDaysopen(trackREST.daysopen)
                        builderTrack.setNoiselimit(trackREST.noiselimit)
                        builderTrack.setCampingrvrvhookup(trackREST.campingrvrvhookup!!.toLong())
                        builderTrack.setSingletracks(trackREST.singletracks!!.toLong())
                        builderTrack.setMxtrack(trackREST.mxtrack!!.toLong())
                        builderTrack.setA4x4(trackREST.a4x4!!.toLong())
                        builderTrack.setEnduro(trackREST.enduro!!.toLong())
                        builderTrack.setUtv(trackREST.utv!!.toLong())
                        builderTrack.setQuad(trackREST.quad!!.toLong())
                        builderTrack.setTrackstatus(trackREST.trackstatus)
                        builderTrack.setAreatype(trackREST.areatype)
                        builderTrack.setSchwierigkeit(trackREST.schwierigkeit!!.toLong())
                        builderTrack.setIndoor(trackREST.indoor!!.toLong())

                        if (trackREST.trackaccess != null) {
                            builderTrack.setTrackaccess(trackREST.trackaccess)
                        }
                        builderTrack.setLogoURL(trackREST.logourl)
                        builderTrack.setShowroom(trackREST.showroom!!.toLong())
                        builderTrack.setWorkshop(trackREST.workshop!!.toLong())
                        builderTrack.setValiduntil(trackREST.validuntil!!.toLong())
                        builderTrack.setBrands(trackREST.brands)
                        builderTrack.setAdress(SecHelper.encryptB64(trackREST.adress))
                        if (lastKnown != null) {
                            val locTrack = Location("calc")
                            locTrack.latitude = trackREST.latitude!!
                            locTrack.longitude = trackREST.longitude!!
                            builderTrack.setDistance2location(lastKnown!!.distanceTo(locTrack).toLong())
                        }

                        contentValuesTrackList.add(builderTrack.values)
                    }

                    // bulk insert
                    if (zlrUpdated > BLOCK_SIZE) {
                        zlrUpdated = bulkInsert(
                            operationContext.applicationContext, contentValuesTrackList,
                            Tracks.CONTENT_URI, tracksResponse.size, opName, vorherImportiert
                        )
                    }
                } else {
                    val recordTrack = TracksRecord.get(restId.toLong())
                    recordTrack!!.restId = trackREST.id!!.toLong()
                    recordTrack.changed = trackREST.changed!!.toLong()
                    recordTrack.trackname = trackREST.trackname
                    recordTrack.longitude = SecHelper.cryptXtude(trackREST.longitude!!)
                    recordTrack.latitude = SecHelper.cryptXtude(trackREST.latitude!!)
                    recordTrack.approved = trackREST.approved!!.toLong()
                    recordTrack.country = trackREST.country
                    recordTrack.url = SecHelper.encryptB64(trackREST.url)
                    recordTrack.facebook = SecHelper.encryptB64(trackREST.facebook)
                    recordTrack.fees = trackREST.fees
                    recordTrack.phone = SecHelper.encryptB64(trackREST.phone)
                    recordTrack.notes = trackREST.notes
                    recordTrack.contact = SecHelper.encryptB64(trackREST.contact)
                    recordTrack.metatext = trackREST.metatext
                    recordTrack.licence = trackREST.licence
                    recordTrack.kidstrack = trackREST.kidstrack!!.toLong()
                    recordTrack.openmondays = trackREST.openmondays!!.toLong()
                    recordTrack.opentuesdays = trackREST.opentuesdays!!.toLong()
                    recordTrack.openwednesday = trackREST.openwednesday!!.toLong()
                    recordTrack.openthursday = trackREST.openthursday!!.toLong()
                    recordTrack.openfriday = trackREST.openfriday!!.toLong()
                    recordTrack.opensaturday = trackREST.opensaturday!!.toLong()
                    recordTrack.opensunday = trackREST.opensunday!!.toLong()
                    recordTrack.hoursmonday = trackREST.hoursmonday
                    recordTrack.hourstuesday = trackREST.hourstuesday
                    recordTrack.hourswednesday = trackREST.hourswednesday
                    recordTrack.hoursthursday = trackREST.hoursthursday
                    recordTrack.hoursfriday = trackREST.hoursfriday
                    recordTrack.hourssaturday = trackREST.hourssaturday
                    recordTrack.hourssunday = trackREST.hourssunday
                    recordTrack.tracklength = trackREST.tracklength!!.toLong()
                    recordTrack.soiltype = trackREST.soiltype!!.toLong()
                    recordTrack.camping = trackREST.camping!!.toLong()
                    recordTrack.shower = trackREST.shower!!.toLong()
                    recordTrack.cleaning = trackREST.cleaning!!.toLong()
                    recordTrack.electricity = trackREST.electricity!!.toLong()
                    recordTrack.supercross = trackREST.supercross!!.toLong()
                    if (trackREST.trackaccess != null) {
                        recordTrack.trackaccess = trackREST.trackaccess
                    }
                    recordTrack.logoURL = trackREST.logourl
                    recordTrack.showroom = trackREST.showroom!!.toLong()
                    recordTrack.workshop = trackREST.workshop!!.toLong()
                    recordTrack.validuntil = trackREST.validuntil!!.toLong()
                    recordTrack.brands = trackREST.brands
                    recordTrack.adress = trackREST.adress
                    recordTrack.feescamping = trackREST.feescamping
                    recordTrack.daysopen = trackREST.daysopen
                    recordTrack.noiselimit = trackREST.noiselimit
                    recordTrack.campingrvrvhookup = trackREST.campingrvrvhookup!!.toLong()
                    recordTrack.singletracks = trackREST.singletracks!!.toLong()
                    recordTrack.mxtrack = trackREST.mxtrack!!.toLong()
                    recordTrack.a4x4 = trackREST.a4x4!!.toLong()
                    recordTrack.enduro = trackREST.enduro!!.toLong()
                    recordTrack.utv = trackREST.utv!!.toLong()
                    recordTrack.quad = trackREST.quad!!.toLong()
                    recordTrack.trackstatus = trackREST.trackstatus
                    recordTrack.areatype = trackREST.areatype
                    recordTrack.schwierigkeit = trackREST.schwierigkeit!!.toLong()
                    recordTrack.indoor = trackREST.indoor!!.toLong()

                    if (lastKnown != null) {
                        val locTrack = Location("calc")
                        locTrack.latitude = SecHelper.cryptXtude(trackREST.latitude!!)
                        locTrack.longitude = SecHelper.cryptXtude(trackREST.longitude!!)
                        recordTrack.distance2location = lastKnown!!.distanceTo(locTrack).toLong()
                    }
                    recordTrack.save(updateProvider)
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message + " $trackName")
        }
        return zlrInsertedReturn
    }

    private fun doBuildCountryTable(context: Context): String {
        val result = ""
        val deleteCount = SQuery.newQuery().append(
            Country._ID + " in (select " + Countrycount._ID + " from " + AbstractMxInfoDBOpenHelper.Sources.COUNTRYCOUNT + " where " +
                    Countrycount.COUNT + "=0)"
        ).delete(Country.CONTENT_URI)
        Timber.d("Country deleted $deleteCount")

        val countryGroup = SQuery.newQuery().select<CountrysumRecord>(Countrysum.CONTENT_URI, Countrysum._ID)
        for (recordC in countryGroup) {
            val countryExist = SQuery.newQuery()
                .expr(
                    Country.COUNTRY, Op.EQ,
                    if (recordC.country == null || recordC.country == "") "\"\"" else recordC.country
                )
                .exists(Country.CONTENT_URI)
            if (!countryExist) {
                val rec = CountryRecord()
                if (!(recordC.country == null || recordC.country == "")) {
                    rec.country = recordC.country
                }
                var country = Locale.getDefault().country
                if (country.length < 2) {
                    country = "DE"
                }
                var showCountry = if (country == recordC.country) 1 else 0
                val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val isoSim = manager.simCountryIso
                val isoNetwork = manager.networkCountryIso
                if (country == isoSim) {
                    showCountry = 1
                }
                if (country == isoNetwork) {
                    showCountry = 1
                }

                rec.show = showCountry.toLong()
                rec.save()
                Timber.d("${rec.country} Country added ${rec.show}")
            }
        }

        //reset countries to show
        if (!MxPreferences.getInstance().firstTimeLocation && LocationHelper.isAmericaShown) {
            //FIXME for US
            // when locations is confirmed and we are in US
            // currently to nothing. Flavor US should handle this
        } else if (!MxPreferences.getInstance().firstTimeCountry) {
            if (SQuery.newQuery().count(Country.CONTENT_URI) > 2) {
                LocationHelper.hideAmerica(context)
                MxPreferences.getInstance().edit().putFirstTimeCountry(true).commit()
            }
        }

        val countries = SQuery.newQuery()
            .expr(Country.COUNTRY, Op.EQ, "zz")
            .select<CountryRecord>(Country.CONTENT_URI)
        for (recordC in countries) {
            SQuery.newQuery()
                .expr(Country._ID, Op.EQ, recordC.id)
                .delete(Country.CONTENT_URI, false)
        }
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

    companion object {
        var importStatusMessage: MutableLiveData<ImportStatusMessage> = MutableLiveData()
        var adminImportStatusCalMessage: MutableLiveData<ImportStatusMessage> = MutableLiveData()

        private const val IMPORT_REC = "Import"
        private const val DOWNLOAD = "Download/Import"

        const val COUNTRY_RESULT = "country_result"
        var LOCATION = "location"
        var SOURCE = "source"
        private const val BLOCK_SIZE = 49
        const val RECALC_TRACKS = "recalcTracks"
        private var imported = 0

        private fun bulkInsert(
            context: Context,
            contentValuesList: MutableList<ContentValues>,
            uri: Uri,
            ges: Int,
            was: String,
            previousImported: Int
        ): Int {
            imported += contentValuesList.size
            LoggingHelper.setMessage(IMPORT_REC + " " + was + " " + imported + "/" + (ges + previousImported))
            val contentValues = arrayOfNulls<ContentValues>(contentValuesList.size)
            for ((i, values) in contentValuesList.withIndex()) {
                contentValues[i] = values
            }
            contentValuesList.clear()

            context.contentResolver.bulkInsert(uri, contentValues)
            return contentValuesList.size
        }

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
            what: String,
            @Suppress("SameParameterValue") previousImported: Int
        ): Int {
            imported += listPictures.size
            mxDatabase.pictureDao().insertPicturesAll(listPictures)
            val zlr = 0
            LoggingHelper.setMessage(IMPORT_REC + " " + what + " " + imported + "/" + (ges + previousImported))
            listPictures.clear()
            return zlr
        }
    }
}
