package info.mx.tracks.ops

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.location.Location
import android.net.Uri
import android.nfc.tech.MifareClassic.BLOCK_SIZE
import android.provider.Settings.Secure
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
import com.robotoworks.mechanoid.net.ServiceException
import com.robotoworks.mechanoid.ops.OperationContext
import info.mx.comlib.retrofit.service.model.TrackR
import info.mx.core.MxCoreApplication
import info.mx.core.common.LoggingHelper
import info.mx.core.ops.ImportHelper
import info.mx.core.rest.MxInfo
import info.mx.core.util.Wait
import info.mx.core_generated.rest.GetTracksStageFromRequest
import info.mx.core_generated.rest.PostTrackstageIDRequest
import info.mx.core_generated.rest.RESTtrackStage
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks
import info.mx.core_generated.sqlite.MxInfoDBContract.Trackstage
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.core_generated.sqlite.TrackstageRecord
import info.mx.tracks.common.SecHelper
import info.mx.tracks.ops.OpSyncFromServerOperation.Companion.IMPORT_REC
import info.mx.tracks.ops.OpSyncFromServerOperation.Companion.imported

object MechanoidHelper {

    fun bulkInsertMechanoid(
        context: Context,
        contentValuesList: MutableList<ContentValues>,
        @Suppress("SameParameterValue") uri: Uri,
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

    fun proceedTracksMechanoid(
        tracksResponse: List<TrackR>,
        zlrInserted: Int,
        contentValuesTrackList: ArrayList<ContentValues>,
        opName: String,
        previousImported: Int,
        updateProvider: Boolean,
        operationContext: OperationContext,
        lastKnown: Location?
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
                    if (trackREST.approved == -1) {
                        // wenn der einzige übertragene ein abgelehnter ist, muss man ihn importieren, damit man in keine endlosschleife kommt
                        oneDeclinedAtLeast = true
                    }

                    // skip during initial all approved to speed it up
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
                        lastKnown?.let {
                            val locTrack = Location("calc")
                            locTrack.latitude = trackREST.latitude!!
                            locTrack.longitude = trackREST.longitude!!
                            builderTrack.setDistance2location(it.distanceTo(locTrack).toLong())
                        }

                        contentValuesTrackList.add(builderTrack.values)
                    }

                    // bulk insert
                    if (zlrUpdated > BLOCK_SIZE) {
                        zlrUpdated = bulkInsertMechanoid(
                            context = operationContext.applicationContext,
                            contentValuesList = contentValuesTrackList,
                            uri = Tracks.CONTENT_URI,
                            ges = tracksResponse.size,
                            was = opName,
                            previousImported = previousImported,
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

                    lastKnown?.let {
                        val locTrack = Location("calc")
                        locTrack.latitude = SecHelper.cryptXtude(trackREST.latitude!!)
                        locTrack.longitude = SecHelper.cryptXtude(trackREST.longitude!!)
                        recordTrack.distance2location = it.distanceTo(locTrack).toLong()
                    }
                    recordTrack.save(updateProvider)
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message + " $trackName")
        }
        return zlrInsertedReturn
    }

    @Throws(ServiceException::class, NotFoundException::class, InterruptedException::class)
    fun doStagesPushMechanoid(context: Context, webClient: MxInfo) {
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
            val restTrackStage = RESTtrackStage()
            if (MxCoreApplication.isAdmin && recordStage.androidid != null && recordStage.androidid != androidId) {
                androidId = recordStage.androidid
            } else if (MxCoreApplication.isAdmin) {
                androidId = "debug"
            }
            restTrackStage.androidid = androidId
            if (recordStage.trackRestId > 0) {
                restTrackStage.trackId = recordStage.trackRestId.toInt()
            }
            if (recordStage.trackname != null) {
                restTrackStage.trackname = recordStage.trackname
            }
            if (recordStage.latitude != 0.0) {
                restTrackStage.latitude = recordStage.latitude
            }
            if (recordStage.longitude != 0.0) {
                restTrackStage.longitude = recordStage.longitude
            }
            if (recordStage.country == null || recordStage.country == "") {
                if (recordStage.latitude != 0.0) { // probably new entry
                    restTrackStage.country = ImportHelper.getShortCountryCoder(
                        recordStage.latitude,
                        recordStage.longitude, context.applicationContext
                    )
                }
                if (restTrackStage.country == null) {
                    if (recordStage.restId > 0) {
                        val trackId = SQuery.newQuery().expr(Tracks.REST_ID, Op.EQ, recordStage.restId).firstLong(
                            Tracks.CONTENT_URI, Tracks._ID
                        )
                        val origTrack = TracksRecord.get(trackId)
                        if (origTrack != null) {
                            if (origTrack.country == null || origTrack.country == "") {
                                if (origTrack.latitude != 0.0) {
                                    restTrackStage.country = ImportHelper.getShortCountryCoder(
                                        SecHelper.entcryptXtude(origTrack.latitude),
                                        SecHelper.entcryptXtude(origTrack.longitude), context.applicationContext
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                restTrackStage.country = recordStage.country
            }
            restTrackStage.changed = recordStage.created.toInt().toLong()
            restTrackStage.insLatitude = recordStage.insLatitude
            restTrackStage.insLongitude = recordStage.insLongitude
            restTrackStage.insDistance = recordStage.insDistance.toInt()
            restTrackStage.url = recordStage.url
            restTrackStage.fees = recordStage.fees
            restTrackStage.phone = recordStage.phone
            restTrackStage.notes = recordStage.notes
            restTrackStage.contact = recordStage.contact
            restTrackStage.licence = recordStage.licence
            restTrackStage.kidstrack = recordStage.kidstrack.toInt()
            restTrackStage.openmondays = recordStage.openmondays.toInt()
            restTrackStage.opentuesdays = recordStage.opentuesdays.toInt()
            restTrackStage.openwednesday = recordStage.openwednesday.toInt()
            restTrackStage.openthursday = recordStage.openthursday.toInt()
            restTrackStage.openfriday = recordStage.openfriday.toInt()
            restTrackStage.opensaturday = recordStage.opensaturday.toInt()
            restTrackStage.opensunday = recordStage.opensunday.toInt()
            restTrackStage.hoursmonday = recordStage.hoursmonday
            restTrackStage.hourstuesday = recordStage.hourstuesday
            restTrackStage.hourswednesday = recordStage.hourswednesday
            restTrackStage.hoursthursday = recordStage.hoursthursday
            restTrackStage.hoursfriday = recordStage.hoursfriday
            restTrackStage.hourssaturday = recordStage.hourssaturday
            restTrackStage.hourssunday = recordStage.hourssunday
            restTrackStage.tracklength = recordStage.tracklength.toInt()
            restTrackStage.soiltype = recordStage.soiltype.toInt()
            restTrackStage.camping = recordStage.camping.toInt()
            restTrackStage.shower = recordStage.shower.toInt()
            restTrackStage.cleaning = recordStage.cleaning.toInt()
            restTrackStage.electricity = recordStage.electricity.toInt()
            restTrackStage.supercross = recordStage.supercross.toInt()
            restTrackStage.trackaccess = recordStage.trackaccess
            restTrackStage.facebook = recordStage.facebook

            restTrackStage.adress = recordStage.adress
            restTrackStage.feescamping = recordStage.feescamping
            restTrackStage.daysopen = recordStage.daysopen
            restTrackStage.noiselimit = recordStage.noiselimit
            restTrackStage.campingrvhookups = recordStage.campingRVhookups.toInt()
            restTrackStage.singletrack = recordStage.singleTrack.toInt()
            restTrackStage.mxtrack = recordStage.mxTrack.toInt()
            restTrackStage.a4x4 = recordStage.a4X4.toInt()
            restTrackStage.enduro = recordStage.enduro.toInt()
            restTrackStage.utv = recordStage.utv.toInt()
            restTrackStage.quad = recordStage.quad.toInt()
            restTrackStage.trackstatus = recordStage.trackstatus
            restTrackStage.areatype = recordStage.areatype
            restTrackStage.indoor = recordStage.indoor.toInt()
            restTrackStage.schwierigkeit = recordStage.schwierigkeit.toInt()
            val request = PostTrackstageIDRequest(restTrackStage)
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
    fun doStagesReceiveNewMechanoid(webClient: MxInfo) {
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
}