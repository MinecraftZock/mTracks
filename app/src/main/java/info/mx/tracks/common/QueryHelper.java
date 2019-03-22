package info.mx.tracks.common;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;

import info.mx.tracks.MxCoreApplication;
import info.mx.tracks.prefs.MxPreferences;
import info.mx.tracks.sqlite.MxInfoDBContract.Country;
import info.mx.tracks.sqlite.MxInfoDBContract.Events2series;
import info.mx.tracks.sqlite.MxInfoDBContract.Favorits;
import info.mx.tracks.sqlite.MxInfoDBContract.Pictures;
import info.mx.tracks.sqlite.MxInfoDBContract.Tracksges;
import info.mx.tracks.sqlite.MxInfoDBContract.Trackstage;
import info.mx.tracks.sqlite.MxInfoDBOpenHelper;
import timber.log.Timber;

public class QueryHelper {

    public static SQuery getPictureFilter(long tracksRestID) {
        return SQuery.newQuery()
                .expr(Pictures.DELETED, Op.NEQ, 1)
                .expr(Pictures.TRACK_REST_ID, Op.EQ, tracksRestID);
    }

    public static SQuery getEventFilter(long tracksID) {
        final SQuery query = SQuery.newQuery()
                .expr(Events2series.EVENT_DATE, Op.GTEQ, System.currentTimeMillis() / 1000)
                .expr(Events2series.TRACK_REST_ID, Op.EQ, tracksID);
        final MxPreferences prefs = MxPreferences.getInstance();
        if (prefs.getOnlyApproved()) {
            query.expr(Events2series.APPROVED, Op.EQ, 1);
        } else if (!MxCoreApplication.Companion.isAdmin()) {
            query.expr(Events2series.APPROVED, Op.GT, -1);
        }

        return query;
    }

    private static SQuery buildTrackFavorite(SQuery query) {
        SQuery res = query;
        res = res.append(" " + Tracksges.REST_ID + " IN (select " + Favorits.TRACK_REST_ID + " from favorits)");
        return res;
    }

    public static SQuery buildStageFilter(SQuery query, boolean onlyForeign) {
        String stageFilter = "";
        String stageFilterPre = " where 1=1 ";
        if (MxPreferences.getInstance().getShowOnlyNewTrackStage()) {
            stageFilter = " and (" + Trackstage.APPROVED + "=0 or " + Trackstage.APPROVED + " is null)";
        }
        if (onlyForeign) {
            stageFilter = stageFilter + " and " + Trackstage.ANDROIDID + Op.NEQ + "'confirm'";
        }
        stageFilter = stageFilterPre + stageFilter;
        final SQuery res = query;
        res.append(Tracksges.REST_ID + " in (select " + Trackstage.TRACK_REST_ID + " from "
                + MxInfoDBOpenHelper.Sources.TRACKSTAGE + stageFilter + ")");
        return res;
    }

    public static SQuery buildUserTrackSearchFilter(SQuery query, String mFilter, boolean isFav, String table) {
        final SQuery res = query;
        if (isFav) {
            query = buildTrackFavorite(query);
        } else {
            query = buildTracksFilter(query, table);
        }
        if (mFilter != null && !mFilter.trim().equals("")) {
            final String[] token = mFilter.trim().split(" ");
            boolean first = true;
            final SQuery filterQ = SQuery.newQuery();
            for (final String tok : token) {
                if (!first) {
                    filterQ.or();
                }
                first = false;
                filterQ.expr(Tracksges.TRACKNAME, Op.LIKE, "%" + tok + "%");
                filterQ.or();
                filterQ.expr(Tracksges.METATEXT, Op.LIKE, "%" + tok + "%");
                filterQ.or();
                filterQ.expr(Tracksges.N_U_EVENTS, Op.LIKE, "%" + tok + "%");
                filterQ.or();
                filterQ.expr(Tracksges.BRANDS, Op.LIKE, "%" + tok + "%");
            }
            res.expr(filterQ);
        }
        Timber.d("query %s", res.toString());
        Timber.d("args %s", res.getArgs().toString());
        return res;
    }

    public static SQuery buildTracksFilter(SQuery query, String table) {

        // dealer mit Ablaufdatum filtern
        final SQuery gueltige = SQuery.newQuery()
                .expr(Tracksges.VALIDUNTIL, Op.EQ, 0)
                .or()
                .append(Tracksges.VALIDUNTIL + " is null")
                .or()
                .expr(Tracksges.VALIDUNTIL, Op.GTEQ, System.currentTimeMillis());
        query.expr(gueltige);

        final MxPreferences prefs = MxPreferences.getInstance();
        if (prefs.getDebugTrackAnsicht() < 2) {
            query.expr(Tracksges.APPROVED, Op.EQ, prefs.getDebugTrackAnsicht());
        }
        if (prefs.getLocationView() > -1) {
            query.expr(Tracksges.INDOOR, Op.EQ, prefs.getLocationView());
        }
        if (prefs.getSoilView() > -1) {
            query.expr(Tracksges.SOILTYPE, Op.EQ, prefs.getSoilView());
        }
        Timber.d("prefs.getOnlyOpen()=%s", prefs.getOnlyOpen());
        if (prefs.getOnlyOpen()) {
            query.expr(Tracksges.TRACKSTATUS, Op.NEQ, "C");
        }
        if (prefs.getOnlyApproved()) {
            query.expr(Tracksges.APPROVED, Op.EQ, 1);
        } else if (!MxCoreApplication.Companion.isAdmin()) {
            query.expr(Tracksges.APPROVED, Op.GT, -1);
        }
        if (prefs.getShowMx()) {
            query.expr(Tracksges.MXTRACK, Op.GTEQ, 0);
        }
        if (prefs.getShowQuad()) {
            query.expr(Tracksges.QUAD, Op.GTEQ, 0);
        }
        if (prefs.getShowUtv()) {
            query.expr(Tracksges.UTV, Op.GTEQ, 0);
        }
        if (prefs.getShow4x4()) {
            query.expr(Tracksges.A4X4, Op.GTEQ, 0);
        }
        if (prefs.getShowEnduro()) {
            query.expr(Tracksges.ENDRUO, Op.GTEQ, 0);
        }
        if (prefs.getOnlyWithKids()) {
            query.expr(Tracksges.KIDSTRACK, Op.EQ, 1);
        }
        if (prefs.getSearchSuperCross()) {
            query.expr(Tracksges.SUPERCROSS, Op.EQ, 1);
        }
        if (prefs.getSearchShower()) {
            query.expr(Tracksges.SHOWER, Op.EQ, 1);
        }
        if (prefs.getSearchWash()) {
            query.expr(Tracksges.CLEANING, Op.EQ, 1);
        }
        if (prefs.getSearchCamping()) {
            query.expr(Tracksges.CAMPING, Op.EQ, 1);
        }
        if (prefs.getSearchElectricity()) {
            query.expr(Tracksges.ELECTRICITY, Op.EQ, 1);
        }
        if (prefs.getSearchPicture()) {
            // query.expr(Tracksges.PICTURECOUNT, Op.GTEQ, 0);

            query.append("exists (select 1 from " + MxInfoDBOpenHelper.Sources.PICTURES +
                    " where " + Pictures.TRACK_REST_ID + "= " +
                    table + "." + Tracksges.REST_ID + ")");
            // query.append(Tracksges.XML_ID + " in (select " + Picturesum.TRACK_XML_ID + " from " +
            // MxInfoDBOpenHelper.Sources.PICTURESUM +
            // " where 1=?)", "1");

            // query.append(Tracksges.XML_ID + " in (select " + Pictures.TRACK_XML_ID + " from " +
            // MxInfoDBOpenHelper.Sources.PICTURES +
            // " where 1=? group by " + Pictures.TRACK_XML_ID + ")", "1");
        }
        if (!prefs.getShowDealers()) {
            query.expr(Tracksges.TRACKACCESS, Op.NEQ, "D");
        }
        if (!prefs.getShowEveryone()) {
            query.expr(Tracksges.TRACKACCESS, Op.NEQ, "N");
        }
        if (!prefs.getShowRace()) {
            query.expr(Tracksges.TRACKACCESS, Op.NEQ, "R");
        }
        if (!prefs.getShowMember()) {
            query.expr(Tracksges.TRACKACCESS, Op.NEQ, "M");
        }
        if (!(prefs.getSearchOpenMo() &&
                prefs.getSearchOpenDi() &&
                prefs.getSearchOpenMi() &&
                prefs.getSearchOpenDo() &&
                prefs.getSearchOpenFr() &&
                prefs.getSearchOpenSa() && prefs.getSearchOpenSo())) {
            if (!prefs.getSearchOpenMo()) {
                query.expr(Tracksges.OPENMONDAYS, Op.EQ, 0);
            }
            if (!prefs.getSearchOpenDi()) {
                query.expr(Tracksges.OPENTUESDAYS, Op.EQ, 0);
            }
            if (!prefs.getSearchOpenMi()) {
                query.expr(Tracksges.OPENWEDNESDAY, Op.EQ, 0);
            }
            if (!prefs.getSearchOpenDo()) {
                query.expr(Tracksges.OPENTHURSDAY, Op.EQ, 0);
            }
            if (!prefs.getSearchOpenFr()) {
                query.expr(Tracksges.OPENFRIDAY, Op.EQ, 0);
            }
            if (!prefs.getSearchOpenSa()) {
                query.expr(Tracksges.OPENSATURDAY, Op.EQ, 0);
            }
            if (!prefs.getSearchOpenSo()) {
                query.expr(Tracksges.OPENSUNDAY, Op.EQ, 0);
            }
        }
//        if (prefs.getShowRating() > 0) {
//            query.expr(Tracksges.RATING, Op.GTEQ, prefs.getShowRating());
//        }
        if (prefs.getShowDifficult() > 0) {
            query.expr(Tracksges.SCHWIERIGKEIT, Op.GTEQ, prefs.getShowDifficult());
        }
        final int anz = SQuery.newQuery().expr(Country.SHOW, Op.EQ, 0).count(Country.CONTENT_URI);
        if (anz > 0) {
            query.append(" " + Tracksges.COUNTRY + " IN (select " + Country.COUNTRY + " from country where show=?)", "1");
        }
        Timber.d("query %s", query.toString());
        Timber.d("args %s", query.getArgs().toString());
        return query;
    }

    public static SQuery buildTracksFilterGes(SQuery query) {

        // dealer mit Ablaufdatum filtern
        final SQuery validQuery = SQuery.newQuery()
                .expr(Tracksges.VALIDUNTIL, Op.EQ, 0)
                .or()
                .append(Tracksges.VALIDUNTIL + " is null")
                .or()
                .expr(Tracksges.VALIDUNTIL, Op.GTEQ, System.currentTimeMillis());
        query.expr(validQuery);

        //        final MxPreferences prefs = MxPreferences.getInstance();
        //        if (prefs.getDebugTrackAnsicht() < 2) {
        //            // query.expr(Tracksges.APPROVED, Op.EQ, prefs.getDebugTrackAnsicht());
        //        }
        // if (prefs.getOnlyApproved()) {
        // query.expr(Tracksges.APPROVED, Op.EQ, 1);
        // } else if (!MxCoreApplication.isAdmin) {
        // query.expr(Tracksges.APPROVED, Op.GT, -1);
        // }
        final int anz = SQuery.newQuery().expr(Country.SHOW, Op.EQ, 0).count(Country.CONTENT_URI);
        if (anz > 0) {
            query.append(" " + Tracksges.COUNTRY + " IN (select " + Country.COUNTRY + " from country where show=?)", "1");
        }
        Timber.d("Ges query %s", query.toString());
        return query;
    }

    public static void resetFilter() {
        MxPreferences.getInstance().edit()
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
                .putShowRating(0)
                .putShowDifficult(0)
                .commit();
    }

    public static boolean isFiltered() {
        MxPreferences prefs = MxPreferences.getInstance();
        return prefs.getDebugTrackAnsicht() != 2 ||
                prefs.getLocationView() != -1 ||
                prefs.getSoilView() != -1 ||
                prefs.getOnlyOpen() ||
                prefs.getOnlyApproved() ||
                prefs.getShowMx() ||
                prefs.getShowQuad() ||
                prefs.getShowUtv() ||
                prefs.getShow4x4() ||
                prefs.getShowEnduro() ||
                prefs.getOnlyWithKids() ||
                prefs.getSearchSuperCross() ||
                prefs.getSearchShower() ||
                prefs.getSearchWash() ||
                prefs.getSearchCamping() ||
                prefs.getSearchElectricity() ||
                prefs.getSearchPicture() ||
                !prefs.getShowDealers() ||
                !prefs.getShowEveryone() ||
                !prefs.getShowRace() ||
                !prefs.getShowMember() ||
                !prefs.getSearchOpenMo() ||
                !prefs.getSearchOpenDi() ||
                !prefs.getSearchOpenMi() ||
                !prefs.getSearchOpenDo() ||
                !prefs.getSearchOpenFr() ||
                !prefs.getSearchOpenSa() ||
                !prefs.getSearchOpenSo() ||
                prefs.getShowRating() != 0 ||
                prefs.getShowDifficult() != 0;
    }
}
