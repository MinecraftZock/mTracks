package info.mx.core_generated.prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.robotoworks.mechanoid.Mechanoid;

@SuppressLint("ApplySharedPref")
public class MxPreferences {

    public static final String PREFERENCES_NAME = "MxPreferences";

    public interface Keys {
        String FIRST_TIME_USE = "FIRST_TIME_USE";
        String FIRST_TIME_LOCATION = "FIRST_TIME_LOCATION";
        String FIRST_TIME_COUNTRY = "FIRST_TIME_COUNTRY";
        String LAST_SYNC_TIME = "LAST_SYNC_TIME";
        String AGREE_TRACKING = "AGREE_TRACKING";
        String AGREE_TRACK_SURVEILLANCE = "AGREE_TRACK_SURVEILLANCE";
        String USERNAME = "USERNAME";
        String SEARCH_OPEN_MO = "SEARCH_OPEN_MO";
        String SEARCH_OPEN_DI = "SEARCH_OPEN_DI";
        String SEARCH_OPEN_MI = "SEARCH_OPEN_MI";
        String SEARCH_OPEN_DO = "SEARCH_OPEN_DO";
        String SEARCH_OPEN_FR = "SEARCH_OPEN_FR";
        String SEARCH_OPEN_SA = "SEARCH_OPEN_SA";
        String SEARCH_OPEN_SO = "SEARCH_OPEN_SO";
        String DEBUG_TRACK_ANSICHT = "DEBUG_TRACK_ANSICHT";
        String LOCATION_VIEW = "LOCATION_VIEW";
        String SOIL_VIEW = "SOIL_VIEW";
        String ONLY_OPEN = "ONLY_OPEN";
        String ONLY_APPROVED = "ONLY_APPROVED";
        String ONLY_WITH_KIDS = "ONLY_WITH_KIDS";
        String SHOW_DEALERS = "SHOW_DEALERS";
        String SHOW_EVERYONE = "SHOW_EVERYONE";
        String SHOW_RACE = "SHOW_RACE";
        String SHOW_MEMBER = "SHOW_MEMBER";
        String SHOW_RATING = "SHOW_RATING";
        String SHOW_DIFFICULT = "SHOW_DIFFICULT";
        String SHOW_MX = "SHOW_MX";
        String SHOW_QUAD = "SHOW_QUAD";
        String SHOW_UTV = "SHOW_UTV";
        String SHOW4X4 = "SHOW4X4";
        String SHOW_ENDURO = "SHOW_ENDURO";
        String UNITS_KM = "UNITS_KM";
        String REPAIR_D_B = "REPAIR_D_B";
        String MAP_CLUSTER = "MAP_CLUSTER";
        String MAP_TYPE = "MAP_TYPE";
        String MAP_TRAFFIC = "MAP_TRAFFIC";
        String MAP_ZOOM = "MAP_ZOOM";
        String MAP_LATITUDE = "MAP_LATITUDE";
        String MAP_LONGITUDE = "MAP_LONGITUDE";
        String SHOW_ONLY_NEW_TRACK_STAGE = "SHOW_ONLY_NEW_TRACK_STAGE";
        String MARKER_SHOW_LONG_CLICK_TEXT = "MARKER_SHOW_LONG_CLICK_TEXT";
        String IMAGE_CROP_TO = "IMAGE_CROP_TO";
        String SEARCH_SUPER_CROSS = "SEARCH_SUPER_CROSS";
        String SEARCH_SHOWER = "SEARCH_SHOWER";
        String SEARCH_WASH = "SEARCH_WASH";
        String SEARCH_CAMPING = "SEARCH_CAMPING";
        String SEARCH_ELECTRICITY = "SEARCH_ELECTRICITY";
        String SEARCH_PICTURE = "SEARCH_PICTURE";
        String RESTORE_I_D = "RESTORE_I_D";
        String RESTORE_CONTENT_URI = "RESTORE_CONTENT_URI";
        String LAST_OPEN_START_ACTIVITY = "LAST_OPEN_START_ACTIVITY";
        String RESET_PICTURE_STORAGE_COUNT = "RESET_PICTURE_STORAGE_COUNT";
        String TAB_DETAIL_POSITION = "TAB_DETAIL_POSITION";
    }

    private final SharedPreferences mPreferences;

    private static MxPreferences sInstance;

    public static MxPreferences getInstance() {
        if (sInstance == null) {
            sInstance = new MxPreferences(Mechanoid.getApplicationContext());
        }

        return sInstance;
    }

    private MxPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    public boolean getFirstTimeUse() {
        return mPreferences.getBoolean(Keys.FIRST_TIME_USE, true);
    }

    public boolean getFirstTimeLocation() {
        return mPreferences.getBoolean(Keys.FIRST_TIME_LOCATION, false);
    }

    public boolean getFirstTimeCountry() {
        return mPreferences.getBoolean(Keys.FIRST_TIME_COUNTRY, false);
    }

    public long getLastSyncTime() {
        return mPreferences.getLong(Keys.LAST_SYNC_TIME, 0L);
    }

    public boolean getAgreeTracking() {
        return mPreferences.getBoolean(Keys.AGREE_TRACKING, true);
    }

    public boolean getAgreeTrackSurveillance() {
        return mPreferences.getBoolean(Keys.AGREE_TRACK_SURVEILLANCE, true);
    }

    public String getUsername() {
        return mPreferences.getString(Keys.USERNAME, "");
    }

    public boolean getSearchOpenMo() {
        return mPreferences.getBoolean(Keys.SEARCH_OPEN_MO, true);
    }

    public boolean getSearchOpenDi() {
        return mPreferences.getBoolean(Keys.SEARCH_OPEN_DI, true);
    }

    public boolean getSearchOpenMi() {
        return mPreferences.getBoolean(Keys.SEARCH_OPEN_MI, true);
    }

    public boolean getSearchOpenDo() {
        return mPreferences.getBoolean(Keys.SEARCH_OPEN_DO, true);
    }

    public boolean getSearchOpenFr() {
        return mPreferences.getBoolean(Keys.SEARCH_OPEN_FR, true);
    }

    public boolean getSearchOpenSa() {
        return mPreferences.getBoolean(Keys.SEARCH_OPEN_SA, true);
    }

    public boolean getSearchOpenSo() {
        return mPreferences.getBoolean(Keys.SEARCH_OPEN_SO, true);
    }

    public int getDebugTrackAnsicht() {
        return mPreferences.getInt(Keys.DEBUG_TRACK_ANSICHT, 2);
    }

    public int getLocationView() {
        return mPreferences.getInt(Keys.LOCATION_VIEW, -1);
    }

    public int getSoilView() {
        return mPreferences.getInt(Keys.SOIL_VIEW, -1);
    }

    public boolean getOnlyOpen() {
        return mPreferences.getBoolean(Keys.ONLY_OPEN, false);
    }

    public boolean getOnlyApproved() {
        return mPreferences.getBoolean(Keys.ONLY_APPROVED, false);
    }

    public boolean getOnlyWithKids() {
        return mPreferences.getBoolean(Keys.ONLY_WITH_KIDS, false);
    }

    public boolean getShowDealers() {
        return mPreferences.getBoolean(Keys.SHOW_DEALERS, true);
    }

    public boolean getShowEveryone() {
        return mPreferences.getBoolean(Keys.SHOW_EVERYONE, true);
    }

    public boolean getShowRace() {
        return mPreferences.getBoolean(Keys.SHOW_RACE, true);
    }

    public boolean getShowMember() {
        return mPreferences.getBoolean(Keys.SHOW_MEMBER, true);
    }

    public float getShowRating() {
        return mPreferences.getFloat(Keys.SHOW_RATING, 0);
    }

    public float getShowDifficult() {
        return mPreferences.getFloat(Keys.SHOW_DIFFICULT, 0);
    }

    public boolean getShowMx() {
        return mPreferences.getBoolean(Keys.SHOW_MX, false);
    }

    public boolean getShowQuad() {
        return mPreferences.getBoolean(Keys.SHOW_QUAD, false);
    }

    public boolean getShowUtv() {
        return mPreferences.getBoolean(Keys.SHOW_UTV, false);
    }

    public boolean getShow4x4() {
        return mPreferences.getBoolean(Keys.SHOW4X4, false);
    }

    public boolean getShowEnduro() {
        return mPreferences.getBoolean(Keys.SHOW_ENDURO, false);
    }

    public boolean getUnitsKm() {
        return mPreferences.getBoolean(Keys.UNITS_KM, true);
    }

    public boolean getRepairDB() {
        return mPreferences.getBoolean(Keys.REPAIR_D_B, false);
    }

    public boolean getMapCluster() {
        return mPreferences.getBoolean(Keys.MAP_CLUSTER, true);
    }

    public int getMapType() {
        return mPreferences.getInt(Keys.MAP_TYPE, 0);
    }

    public boolean getMapTraffic() {
        return mPreferences.getBoolean(Keys.MAP_TRAFFIC, false);
    }

    public float getMapZoom() {
        return mPreferences.getFloat(Keys.MAP_ZOOM, 9);
    }

    public float getMapLatitude() {
        return mPreferences.getFloat(Keys.MAP_LATITUDE, 0);
    }

    public float getMapLongitude() {
        return mPreferences.getFloat(Keys.MAP_LONGITUDE, 0);
    }

    public boolean getShowOnlyNewTrackStage() {
        return mPreferences.getBoolean(Keys.SHOW_ONLY_NEW_TRACK_STAGE, true);
    }

    public int getMarkerShowLongClickText() {
        return mPreferences.getInt(Keys.MARKER_SHOW_LONG_CLICK_TEXT, 0);
    }

    public int getImageCropTo() {
        return mPreferences.getInt(Keys.IMAGE_CROP_TO, 1200);
    }

    public boolean getSearchSuperCross() {
        return mPreferences.getBoolean(Keys.SEARCH_SUPER_CROSS, false);
    }

    public boolean getSearchShower() {
        return mPreferences.getBoolean(Keys.SEARCH_SHOWER, false);
    }

    public boolean getSearchWash() {
        return mPreferences.getBoolean(Keys.SEARCH_WASH, false);
    }

    public boolean getSearchCamping() {
        return mPreferences.getBoolean(Keys.SEARCH_CAMPING, false);
    }

    public boolean getSearchElectricity() {
        return mPreferences.getBoolean(Keys.SEARCH_ELECTRICITY, false);
    }

    public boolean getSearchPicture() {
        return mPreferences.getBoolean(Keys.SEARCH_PICTURE, false);
    }

    public long getRestoreID() {
        return mPreferences.getLong(Keys.RESTORE_I_D, 0L);
    }

    public String getRestoreContentUri() {
        return mPreferences.getString(Keys.RESTORE_CONTENT_URI, null);
    }

    public String getLastOpenStartActivity() {
        return mPreferences.getString(Keys.LAST_OPEN_START_ACTIVITY, null);
    }

    public int getResetPictureStorageCount() {
        return mPreferences.getInt(Keys.RESET_PICTURE_STORAGE_COUNT, 0);
    }

    public int getTabDetailPosition() {
        return mPreferences.getInt(Keys.TAB_DETAIL_POSITION, 1);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public MxPreferencesEditor edit() {
        return new MxPreferencesEditor(mPreferences.edit());
    }

    public static class MxPreferencesEditor {
        private final SharedPreferences.Editor mEditor;

        public MxPreferencesEditor(SharedPreferences.Editor editor) {
            mEditor = editor;
        }

        public boolean commit() {
            return mEditor.commit();
        }

        public void apply() {
            mEditor.apply();
        }

        public MxPreferencesEditor putFirstTimeUse(boolean value) {
            mEditor.putBoolean(Keys.FIRST_TIME_USE, value);
            return this;
        }

        public MxPreferencesEditor putFirstTimeLocation(boolean value) {
            mEditor.putBoolean(Keys.FIRST_TIME_LOCATION, value);
            return this;
        }

        public MxPreferencesEditor putFirstTimeCountry(boolean value) {
            mEditor.putBoolean(Keys.FIRST_TIME_COUNTRY, value);
            return this;
        }

        public MxPreferencesEditor putLastSyncTime(long value) {
            mEditor.putLong(Keys.LAST_SYNC_TIME, value);
            return this;
        }

        public MxPreferencesEditor putAgreeTracking(boolean value) {
            mEditor.putBoolean(Keys.AGREE_TRACKING, value);
            return this;
        }

        public MxPreferencesEditor putAgreeTrackSurveillance(boolean value) {
            mEditor.putBoolean(Keys.AGREE_TRACK_SURVEILLANCE, value);
            return this;
        }

        public MxPreferencesEditor putUsername(String value) {
            mEditor.putString(Keys.USERNAME, value);
            return this;
        }

        public MxPreferencesEditor putSearchOpenMo(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_OPEN_MO, value);
            return this;
        }

        public MxPreferencesEditor putSearchOpenDi(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_OPEN_DI, value);
            return this;
        }

        public MxPreferencesEditor putSearchOpenMi(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_OPEN_MI, value);
            return this;
        }

        public MxPreferencesEditor putSearchOpenDo(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_OPEN_DO, value);
            return this;
        }

        public MxPreferencesEditor putSearchOpenFr(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_OPEN_FR, value);
            return this;
        }

        public MxPreferencesEditor putSearchOpenSa(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_OPEN_SA, value);
            return this;
        }

        public MxPreferencesEditor putSearchOpenSo(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_OPEN_SO, value);
            return this;
        }

        public MxPreferencesEditor putDebugTrackAnsicht(int value) {
            mEditor.putInt(Keys.DEBUG_TRACK_ANSICHT, value);
            return this;
        }

        public MxPreferencesEditor putLocationView(int value) {
            mEditor.putInt(Keys.LOCATION_VIEW, value);
            return this;
        }

        public MxPreferencesEditor putSoilView(int value) {
            mEditor.putInt(Keys.SOIL_VIEW, value);
            return this;
        }

        public MxPreferencesEditor putOnlyOpen(boolean value) {
            mEditor.putBoolean(Keys.ONLY_OPEN, value);
            return this;
        }

        public MxPreferencesEditor putOnlyApproved(boolean value) {
            mEditor.putBoolean(Keys.ONLY_APPROVED, value);
            return this;
        }

        public MxPreferencesEditor putOnlyWithKids(boolean value) {
            mEditor.putBoolean(Keys.ONLY_WITH_KIDS, value);
            return this;
        }

        public MxPreferencesEditor putShowDealers(boolean value) {
            mEditor.putBoolean(Keys.SHOW_DEALERS, value);
            return this;
        }

        public MxPreferencesEditor putShowEveryone(boolean value) {
            mEditor.putBoolean(Keys.SHOW_EVERYONE, value);
            return this;
        }

        public MxPreferencesEditor putShowRace(boolean value) {
            mEditor.putBoolean(Keys.SHOW_RACE, value);
            return this;
        }

        public MxPreferencesEditor putShowMember(boolean value) {
            mEditor.putBoolean(Keys.SHOW_MEMBER, value);
            return this;
        }

        public MxPreferencesEditor putShowRating(float value) {
            mEditor.putFloat(Keys.SHOW_RATING, value);
            return this;
        }

        public MxPreferencesEditor putShowDifficult(float value) {
            mEditor.putFloat(Keys.SHOW_DIFFICULT, value);
            return this;
        }

        public MxPreferencesEditor putShowMx(boolean value) {
            mEditor.putBoolean(Keys.SHOW_MX, value);
            return this;
        }

        public MxPreferencesEditor putShowQuad(boolean value) {
            mEditor.putBoolean(Keys.SHOW_QUAD, value);
            return this;
        }

        public MxPreferencesEditor putShowUtv(boolean value) {
            mEditor.putBoolean(Keys.SHOW_UTV, value);
            return this;
        }

        public MxPreferencesEditor putShow4x4(boolean value) {
            mEditor.putBoolean(Keys.SHOW4X4, value);
            return this;
        }

        public MxPreferencesEditor putShowEnduro(boolean value) {
            mEditor.putBoolean(Keys.SHOW_ENDURO, value);
            return this;
        }

        public MxPreferencesEditor putUnitsKm(boolean value) {
            mEditor.putBoolean(Keys.UNITS_KM, value);
            return this;
        }

        public MxPreferencesEditor putRepairDB(boolean value) {
            mEditor.putBoolean(Keys.REPAIR_D_B, value);
            return this;
        }

        public MxPreferencesEditor putMapCluster(boolean value) {
            mEditor.putBoolean(Keys.MAP_CLUSTER, value);
            return this;
        }

        public MxPreferencesEditor putMapType(int value) {
            mEditor.putInt(Keys.MAP_TYPE, value);
            return this;
        }

        public MxPreferencesEditor putMapTraffic(boolean value) {
            mEditor.putBoolean(Keys.MAP_TRAFFIC, value);
            return this;
        }

        public MxPreferencesEditor putMapZoom(float value) {
            mEditor.putFloat(Keys.MAP_ZOOM, value);
            return this;
        }

        public MxPreferencesEditor putMapLatitude(float value) {
            mEditor.putFloat(Keys.MAP_LATITUDE, value);
            return this;
        }

        public MxPreferencesEditor putMapLongitude(float value) {
            mEditor.putFloat(Keys.MAP_LONGITUDE, value);
            return this;
        }

        public MxPreferencesEditor putMarkerShowLongClickText(int value) {
            mEditor.putInt(Keys.MARKER_SHOW_LONG_CLICK_TEXT, value);
            return this;
        }

        public MxPreferencesEditor putSearchSuperCross(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_SUPER_CROSS, value);
            return this;
        }

        public MxPreferencesEditor putSearchShower(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_SHOWER, value);
            return this;
        }

        public MxPreferencesEditor putSearchWash(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_WASH, value);
            return this;
        }

        public MxPreferencesEditor putSearchCamping(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_CAMPING, value);
            return this;
        }

        public MxPreferencesEditor putSearchElectricity(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_ELECTRICITY, value);
            return this;
        }

        public MxPreferencesEditor putSearchPicture(boolean value) {
            mEditor.putBoolean(Keys.SEARCH_PICTURE, value);
            return this;
        }

        public MxPreferencesEditor putRestoreID(long value) {
            mEditor.putLong(Keys.RESTORE_I_D, value);
            return this;
        }

        public MxPreferencesEditor putRestoreContentUri(String value) {
            mEditor.putString(Keys.RESTORE_CONTENT_URI, value);
            return this;
        }

        public MxPreferencesEditor putLastOpenStartActivity(String value) {
            mEditor.putString(Keys.LAST_OPEN_START_ACTIVITY, value);
            return this;
        }

        public MxPreferencesEditor putResetPictureStorageCount(int value) {
            mEditor.putInt(Keys.RESET_PICTURE_STORAGE_COUNT, value);
            return this;
        }

        public MxPreferencesEditor putTabDetailPosition(int value) {
            mEditor.putInt(Keys.TAB_DETAIL_POSITION, value);
            return this;
        }
    }
}
