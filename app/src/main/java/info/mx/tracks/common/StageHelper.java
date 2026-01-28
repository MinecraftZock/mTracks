package info.mx.tracks.common;

import android.database.Cursor;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;

import java.util.HashMap;
import java.util.Map.Entry;

import timber.log.Timber;
import info.mx.tracks.R;
import info.mx.core_generated.prefs.MxPreferences;
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks;
import info.mx.core_generated.sqlite.MxInfoDBContract.Trackstage;
import info.mx.core_generated.sqlite.TrackstageRecord;

public class StageHelper {

    private static final HashMap<Marker, Long> stageHashMap = new HashMap<>();

    public static void addStageMarkers(GoogleMap mMap, Cursor cursor, boolean showAllStage) {
        clearStageMarkers();
        boolean showBounds = false;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        while (cursor.moveToNext()) {
            final TrackstageRecord stage = TrackstageRecord.fromCursor(cursor);
            if (stage.getLatitude() != 0) {
                final Marker stageMarker = addMarkerStage(mMap, stage, StageHelper.getStringVal(cursor));
                stageHashMap.put(stageMarker, stage.getId());
                builder.include(stageMarker.getPosition());
                showBounds = true;
            }
        }
        if (showAllStage && showBounds) {
            final LatLngBounds bounds = builder.build();
            final int padding = 10; // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }

    public static SQuery getStageQuery(long trackRestID) {
        final SQuery approved = SQuery.newQuery()
                .expr(Trackstage.APPROVED, Op.EQ, 0)
                .or()
                .append(Trackstage.APPROVED + " is null");
        final SQuery query = SQuery.newQuery()
                .expr(Trackstage.TRACK_REST_ID, Op.EQ, trackRestID);
        if (MxPreferences.getInstance().getShowOnlyNewTrackStage()) {
            query.expr(approved);
        }
        return query;
    }

    private static void clearStageMarkers() {
        for (final Entry<Marker, Long> entry : stageHashMap.entrySet()) {
            entry.getKey().remove();
        }
        stageHashMap.clear();
    }

    public static Long getId(Marker marker) {
        return stageHashMap.get(marker);
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
                    .position(latlng)
                    .title(stage.getTrackname())
                    .snippet(stageDetail.toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stage))
            );
        }
        return result;
    }

    private static String getStringVal(Cursor cursor) {
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
                        && !cursor.getString(i).isEmpty()) {
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
                    res += "\n" + cursor.getColumnName(i) + "=" + colorStrPre + cursor.getString(i) + (colorStrPre.isEmpty() ? "" : "</font>");
                }
            }
        }
        return res.trim().replaceAll("\n", "<br />");
    }

}
