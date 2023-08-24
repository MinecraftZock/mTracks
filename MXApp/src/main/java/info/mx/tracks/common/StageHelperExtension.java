package info.mx.tracks.common;

import android.database.Cursor;
import android.database.StaleDataException;

import com.androidmapsextensions.ClusterGroup;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;

import java.util.HashMap;
import java.util.Map.Entry;

import info.mx.tracks.R;
import info.mx.tracks.prefs.MxPreferences;
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks;
import info.mx.tracks.sqlite.MxInfoDBContract.Trackstage;
import info.mx.tracks.sqlite.TrackstageRecord;
import timber.log.Timber;

public class StageHelperExtension {

    private static HashMap<Marker, Long> stageHashMap = new HashMap<>();

    public static void addStageMarkers(GoogleMap mMap, Cursor cursor, boolean showAllStage) {
        clearStageMarkers();
        boolean showBounds = false;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        while (cursor.moveToNext()) {
            final TrackstageRecord stage = TrackstageRecord.fromCursor(cursor);
            final Marker stageMarker = addMarkerStage(mMap, stage, StageHelperExtension.getStringVal(cursor));
            stageHashMap.put(stageMarker, stage.getId());
            builder.include(stageMarker.getPosition());
            showBounds = true;
        }
        if (showAllStage && showBounds) {
            final LatLngBounds bounds = builder.build();
            final int padding = 10; // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
            Timber.d("animateCameraStage %s", padding);
        }
    }

    public static SQuery getStageQuery(long trackRestID, boolean withLatLngChange) {
        final SQuery query = SQuery.newQuery()
                .expr(Trackstage.TRACK_REST_ID, Op.EQ, trackRestID);
        if (withLatLngChange) {
            final SQuery queryLatLng = SQuery.newQuery()
                    .expr(Trackstage.LATITUDE, Op.NEQ, 0)
                    .and()
                    .expr(Trackstage.ANDROIDID, Op.NEQ, "confirm");
            query.or().expr(queryLatLng);
        }
        final SQuery mainQuery = SQuery.newQuery().expr(query);
        if (MxPreferences.getInstance().getShowOnlyNewTrackStage()) {
            final SQuery approved = SQuery.newQuery()
                    .expr(Trackstage.APPROVED, Op.EQ, 0)
                    .or()
                    .append(Trackstage.APPROVED + " is null");
            mainQuery.expr(approved);
        }
        return mainQuery;
    }

    public static void clearStageMarkers() {
        for (final Entry<Marker, Long> entry : stageHashMap.entrySet()) {
            entry.getKey().remove();
        }
        stageHashMap.clear();
    }

    public static int countStageMarkers() {
        return stageHashMap.size();
    }

    private static Marker addMarkerStage(GoogleMap mMap, TrackstageRecord stage, CharSequence stageDetail) {
        Marker result = null;
        if (mMap != null) {
            String titel = "";
            if (stage.getApproved() == -1) {
                titel = "--";
            } else if (stage.getApproved() == 1) {
                titel = "++";
            }
            final LatLng latlng = new LatLng(stage.getLatitude(), stage.getLongitude());
            result = mMap.addMarker(new MarkerOptions()
                    .clusterGroup(ClusterGroup.NOT_CLUSTERED)
                    .data(stage)
                    .position(latlng)
                    .draggable(true)
                    .title(titel + stage.getTrackname() == null ? "<kein name>" : stage.getTrackname())
                    .snippet(stageDetail.toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stage))
            );
        }
        return result;
    }

    public static String getStringVal(Cursor cursor) {
        String res = "";

        final String[] proj = null;
        Cursor trackCur;
        final int trackRestId = cursor.getColumnIndex(Trackstage.TRACK_REST_ID);
        trackCur = SQuery.newQuery().expr(Tracks.REST_ID, Op.EQ, cursor.getString(trackRestId))
                .select(Tracks.CONTENT_URI, proj);
        trackCur.moveToFirst();
        if (trackCur.getCount() == 0) {
            if (cursor.getLong(trackRestId) != 0) {
                Timber.e("Track not found trackRestId:%s", cursor.getString(trackRestId));
            }
            return res;
        }
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            if (!cursor.getColumnName(i).equals(Trackstage._ID) &&
                    !cursor.getColumnName(i).equals(Trackstage.REST_ID) &&
                    !cursor.getColumnName(i).equals(Trackstage.TRACK_REST_ID) &&
                    !cursor.getColumnName(i).startsWith("Ins") &&
                    !cursor.getColumnName(i).equals(Trackstage.ANDROIDID) &&
                    !cursor.getColumnName(i).equals(Trackstage.CREATED) &&
                    !cursor.getColumnName(i).equals(Trackstage.APPROVED)) {
                if (cursor.getString(i) != null && !cursor.getString(i).equals("0") && !cursor.getString(i).equals("0.0")
                        && !cursor.getString(i).equals("")) {
                    final int colIndex = trackCur.getColumnIndex(cursor.getColumnName(i));
                    String colorStrPre = "";
                    if (colIndex == -1) {
                        colorStrPre = "<font color=\"blue\">";
                        Timber.w("missing field:%s", cursor.getColumnName(i));
                    } else if (trackCur.getString(colIndex) == null || trackCur.getString(colIndex).equals(cursor.getString(i))) {
                        // nothing
                    } else {
                        colorStrPre = "<font color=\"red\">";
                    }
                    res += "\n" + cursor.getColumnName(i) + "=" + colorStrPre + cursor.getString(i) + (colorStrPre.equals("") ? "" : "</font>");
                }
            }
        }
        return res.trim().replaceAll("\n", "<br />");
    }

    public static String getStageValues(Cursor cursor) {
        String res = "";

        try {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (!cursor.getColumnName(i).equals(Trackstage._ID) &&
                        !cursor.getColumnName(i).equals(Trackstage.REST_ID) &&
                        !cursor.getColumnName(i).equals(Trackstage.TRACK_REST_ID) &&
                        !cursor.getColumnName(i).startsWith("Ins") &&
                        !cursor.getColumnName(i).equals(Trackstage.ANDROIDID) &&
                        !cursor.getColumnName(i).equals(Trackstage.CREATED) &&
                        !cursor.getColumnName(i).equals(Trackstage.APPROVED)) {
                    if (cursor.getString(i) != null && !cursor.getString(i).equals("0") && !cursor.getString(i).equals("0.0")
                            && !cursor.getString(i).equals("")) {
                        res += "\n" + cursor.getColumnName(i) + "=" + cursor.getString(i);
                    }
                }
            }
        } catch (StaleDataException e) {
            res = "Cursor is closed";
        }
        return res.trim().replaceAll("\n", "<br />");
    }

}
