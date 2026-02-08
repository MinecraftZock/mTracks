package info.mx.tracks.common

import com.robotoworks.mechanoid.db.SQuery
import info.mx.core.MxCoreApplication.Companion.isAdmin
import info.mx.core_generated.prefs.MxPreferences.Companion.instance
import info.mx.core_generated.sqlite.AbstractMxInfoDBOpenHelper
import info.mx.core_generated.sqlite.MxInfoDBContract.*
import timber.log.Timber

object QueryHelper {
    fun getPictureFilter(tracksRestID: Long): SQuery {
        return SQuery.newQuery()
            .expr(Pictures.DELETED, SQuery.Op.NEQ, 1)
            .expr(Pictures.TRACK_REST_ID, SQuery.Op.EQ, tracksRestID)
    }

    private fun buildTrackFavorite(query: SQuery): SQuery {
        var res = query
        res =
            res.append(" " + Tracksges.REST_ID + " IN (select " + Favorits.TRACK_REST_ID + " from favorits)")
        return res
    }

    fun buildStageFilter(query: SQuery, onlyForeign: Boolean): SQuery {
        var stageFilter = ""
        val stageFilterPre = " where 1=1 "
        if (instance.showOnlyNewTrackStage) {
            stageFilter =
                " and (" + Trackstage.APPROVED + "=0 or " + Trackstage.APPROVED + " is null)"
        }
        if (onlyForeign) {
            stageFilter = stageFilter + " and " + Trackstage.ANDROIDID + SQuery.Op.NEQ + "'confirm'"
        }
        stageFilter = stageFilterPre + stageFilter
        query.append(
            (Tracksges.REST_ID + " in (select " + Trackstage.TRACK_REST_ID + " from "
                    + AbstractMxInfoDBOpenHelper.Sources.TRACKSTAGE + stageFilter + ")")
        )
        return query
    }

    fun buildUserTrackSearchFilter(
        query: SQuery,
        mFilter: String?,
        isFav: Boolean,
        table: String?
    ): SQuery {
        var query = query
        val res = query
        if (isFav) {
            query = buildTrackFavorite(query)
        } else {
            query = buildTracksFilter(query, table)
        }
        if (mFilter != null && !mFilter.trim { it <= ' ' }.isEmpty()) {
            val token: Array<String?> =
                mFilter.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            var first = true
            val filterQ = SQuery.newQuery()
            for (tok in token) {
                if (!first) {
                    filterQ.or()
                }
                first = false
                filterQ.expr(Tracksges.TRACKNAME, SQuery.Op.LIKE, "%" + tok + "%")
                filterQ.or()
                filterQ.expr(Tracksges.METATEXT, SQuery.Op.LIKE, "%" + tok + "%")
                filterQ.or()
                filterQ.expr(Tracksges.N_U_EVENTS, SQuery.Op.LIKE, "%" + tok + "%")
                filterQ.or()
                filterQ.expr(Tracksges.BRANDS, SQuery.Op.LIKE, "%" + tok + "%")
            }
            res.expr(filterQ)
        }
        Timber.d("query %s", res.toString())
        Timber.d("args %s", res.getArgs().toString())
        return res
    }

    fun buildTracksFilter(query: SQuery, table: String?): SQuery {
        // dealer mit Ablaufdatum filtern

        val gueltige = SQuery.newQuery()
            .expr(Tracksges.VALIDUNTIL, SQuery.Op.EQ, 0)
            .or()
            .append(Tracksges.VALIDUNTIL + " is null")
            .or()
            .expr(Tracksges.VALIDUNTIL, SQuery.Op.GTEQ, System.currentTimeMillis())
        query.expr(gueltige)

        val prefs = instance
        if (prefs.debugTrackAnsicht < 2) {
            query.expr(Tracksges.APPROVED, SQuery.Op.EQ, prefs.debugTrackAnsicht)
        }
        if (prefs.locationView > -1) {
            query.expr(Tracksges.INDOOR, SQuery.Op.EQ, prefs.locationView)
        }
        if (prefs.soilView > -1) {
            query.expr(Tracksges.SOILTYPE, SQuery.Op.EQ, prefs.soilView)
        }
        Timber.d("prefs.getOnlyOpen()=%s", prefs.onlyOpen)
        if (prefs.onlyOpen) {
            query.expr(Tracksges.TRACKSTATUS, SQuery.Op.NEQ, "C")
        }
        if (prefs.onlyApproved) {
            query.expr(Tracksges.APPROVED, SQuery.Op.EQ, 1)
        } else if (!isAdmin) {
            query.expr(Tracksges.APPROVED, SQuery.Op.GT, -1)
        }
        if (prefs.showMx) {
            query.expr(Tracksges.MXTRACK, SQuery.Op.GTEQ, 0)
        }
        if (prefs.showQuad) {
            query.expr(Tracksges.QUAD, SQuery.Op.GTEQ, 0)
        }
        if (prefs.showUtv) {
            query.expr(Tracksges.UTV, SQuery.Op.GTEQ, 0)
        }
        if (prefs.show4x4) {
            query.expr(Tracksges.A4X4, SQuery.Op.GTEQ, 0)
        }
        if (prefs.showEnduro) {
            query.expr(Tracksges.ENDRUO, SQuery.Op.GTEQ, 0)
        }
        if (prefs.onlyWithKids) {
            query.expr(Tracksges.KIDSTRACK, SQuery.Op.EQ, 1)
        }
        if (prefs.searchSuperCross) {
            query.expr(Tracksges.SUPERCROSS, SQuery.Op.EQ, 1)
        }
        if (prefs.searchShower) {
            query.expr(Tracksges.SHOWER, SQuery.Op.EQ, 1)
        }
        if (prefs.searchWash) {
            query.expr(Tracksges.CLEANING, SQuery.Op.EQ, 1)
        }
        if (prefs.searchCamping) {
            query.expr(Tracksges.CAMPING, SQuery.Op.EQ, 1)
        }
        if (prefs.searchElectricity) {
            query.expr(Tracksges.ELECTRICITY, SQuery.Op.EQ, 1)
        }
        if (prefs.searchPicture) {
            // query.expr(Tracksges.PICTURECOUNT, Op.GTEQ, 0);

            query.append(
                "exists (select 1 from " + AbstractMxInfoDBOpenHelper.Sources.PICTURES +
                        " where " + Pictures.TRACK_REST_ID + "= " +
                        table + "." + Tracksges.REST_ID + ")"
            )

            // query.append(Tracksges.XML_ID + " in (select " + Picturesum.TRACK_XML_ID + " from " +
            // MxInfoDBOpenHelper.Sources.PICTURESUM +
            // " where 1=?)", "1");

            // query.append(Tracksges.XML_ID + " in (select " + Pictures.TRACK_XML_ID + " from " +
            // MxInfoDBOpenHelper.Sources.PICTURES +
            // " where 1=? group by " + Pictures.TRACK_XML_ID + ")", "1");
        }
        if (!prefs.showDealers) {
            query.expr(Tracksges.TRACKACCESS, SQuery.Op.NEQ, "D")
        }
        if (!prefs.showEveryone) {
            query.expr(Tracksges.TRACKACCESS, SQuery.Op.NEQ, "N")
        }
        if (!prefs.showRace) {
            query.expr(Tracksges.TRACKACCESS, SQuery.Op.NEQ, "R")
        }
        if (!prefs.showMember) {
            query.expr(Tracksges.TRACKACCESS, SQuery.Op.NEQ, "M")
        }
        if (!(prefs.searchOpenMo &&
                    prefs.searchOpenDi &&
                    prefs.searchOpenMi &&
                    prefs.searchOpenDo &&
                    prefs.searchOpenFr &&
                    prefs.searchOpenSa && prefs.searchOpenSo)
        ) {
            if (!prefs.searchOpenMo) {
                query.expr(Tracksges.OPENMONDAYS, SQuery.Op.EQ, 0)
            }
            if (!prefs.searchOpenDi) {
                query.expr(Tracksges.OPENTUESDAYS, SQuery.Op.EQ, 0)
            }
            if (!prefs.searchOpenMi) {
                query.expr(Tracksges.OPENWEDNESDAY, SQuery.Op.EQ, 0)
            }
            if (!prefs.searchOpenDo) {
                query.expr(Tracksges.OPENTHURSDAY, SQuery.Op.EQ, 0)
            }
            if (!prefs.searchOpenFr) {
                query.expr(Tracksges.OPENFRIDAY, SQuery.Op.EQ, 0)
            }
            if (!prefs.searchOpenSa) {
                query.expr(Tracksges.OPENSATURDAY, SQuery.Op.EQ, 0)
            }
            if (!prefs.searchOpenSo) {
                query.expr(Tracksges.OPENSUNDAY, SQuery.Op.EQ, 0)
            }
        }
        if (prefs.showRating > 0) {
            query.expr(Tracksges.RATING, SQuery.Op.GTEQ, prefs.showRating)
        }
        if (prefs.showDifficult > 0) {
            query.expr(Tracksges.SCHWIERIGKEIT, SQuery.Op.GTEQ, prefs.showDifficult)
        }
        val anz = SQuery.newQuery().expr(Country.SHOW, SQuery.Op.EQ, 0).count(Country.CONTENT_URI)
        if (anz > 0) {
            query.append(
                " " + Tracksges.COUNTRY + " IN (select " + Country.COUNTRY + " from country where show=?)",
                "1"
            )
        }
        Timber.d("query %s", query.toString())
        Timber.d("args %s", query.getArgs().toString())
        return query
    }

    fun buildTracksFilterGes(query: SQuery): SQuery {
        // dealer mit Ablaufdatum filtern

        val validQuery = SQuery.newQuery()
            .expr(Tracksges.VALIDUNTIL, SQuery.Op.EQ, 0)
            .or()
            .append(Tracksges.VALIDUNTIL + " is null")
            .or()
            .expr(Tracksges.VALIDUNTIL, SQuery.Op.GTEQ, System.currentTimeMillis())
        query.expr(validQuery)

        //        final MxPreferences prefs = MxPreferences.getInstance();
        //        if (prefs.getDebugTrackAnsicht() < 2) {
        //            // query.expr(Tracksges.APPROVED, Op.EQ, prefs.getDebugTrackAnsicht());
        //        }
        // if (prefs.getOnlyApproved()) {
        // query.expr(Tracksges.APPROVED, Op.EQ, 1);
        // } else if (!MxCoreApplication.isAdmin) {
        // query.expr(Tracksges.APPROVED, Op.GT, -1);
        // }
        val anz = SQuery.newQuery().expr(Country.SHOW, SQuery.Op.EQ, 0).count(Country.CONTENT_URI)
        if (anz > 0) {
            query.append(
                " " + Tracksges.COUNTRY + " IN (select " + Country.COUNTRY + " from country where show=?)",
                "1"
            )
        }
        Timber.d("Ges query %s", query.toString())
        return query
    }

    fun resetFilter() {
        instance.edit()
            .putDebugTrackAnsicht(2)
            .putLocationView(-1)
            .putSoilView(-1)
            .putOnlyOpen(false)
            .putOnlyApproved(false)
            .putShowMx(false)
            .putShowQuad(false)
            .putShowUtv(false)
            .putShow4x4(false)
            .putShowEnduro(false)
            .putOnlyWithKids(false)
            .putSearchSuperCross(false)
            .putSearchShower(false)
            .putSearchWash(false)
            .putSearchCamping(false)
            .putSearchElectricity(false)
            .putSearchPicture(false)
            .putShowDealers(true)
            .putShowEveryone(true)
            .putShowRace(true)
            .putShowMember(true)
            .putSearchOpenMo(true)
            .putSearchOpenDi(true)
            .putSearchOpenMi(true)
            .putSearchOpenDo(true)
            .putSearchOpenFr(true)
            .putSearchOpenSa(true)
            .putSearchOpenSo(true)
            .putShowRating(0f)
            .putShowDifficult(0f)
            .commit()
    }

    val isFiltered: Boolean
        get() {
            val prefs = instance
            return prefs.debugTrackAnsicht != 2 || prefs.locationView != -1 || prefs.soilView != -1 ||
                    prefs.onlyOpen ||
                    prefs.onlyApproved ||
                    prefs.showMx ||
                    prefs.showQuad ||
                    prefs.showUtv ||
                    prefs.show4x4 ||
                    prefs.showEnduro ||
                    prefs.onlyWithKids ||
                    prefs.searchSuperCross ||
                    prefs.searchShower ||
                    prefs.searchWash ||
                    prefs.searchCamping ||
                    prefs.searchElectricity ||
                    prefs.searchPicture || !prefs.showDealers || !prefs.showEveryone || !prefs.showRace || !prefs.showMember || !prefs.searchOpenMo || !prefs.searchOpenDi || !prefs.searchOpenMi || !prefs.searchOpenDo || !prefs.searchOpenFr || !prefs.searchOpenSa || !prefs.searchOpenSo || prefs.showRating != 0f || prefs.showDifficult != 0f
        }
}
