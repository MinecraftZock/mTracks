package info.mx.core_generated.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.robotoworks.mechanoid.Mechanoid

@SuppressLint("ApplySharedPref")
class MxPreferences private constructor(context: Context) {
    interface Keys {
        companion object {
            const val FIRST_TIME_USE: String = "FIRST_TIME_USE"
            const val FIRST_TIME_LOCATION: String = "FIRST_TIME_LOCATION"
            const val FIRST_TIME_COUNTRY: String = "FIRST_TIME_COUNTRY"
            const val LAST_SYNC_TIME: String = "LAST_SYNC_TIME"
            const val AGREE_TRACKING: String = "AGREE_TRACKING"
            const val AGREE_TRACK_SURVEILLANCE: String = "AGREE_TRACK_SURVEILLANCE"
            const val USERNAME: String = "USERNAME"
            const val SEARCH_OPEN_MO: String = "SEARCH_OPEN_MO"
            const val SEARCH_OPEN_DI: String = "SEARCH_OPEN_DI"
            const val SEARCH_OPEN_MI: String = "SEARCH_OPEN_MI"
            const val SEARCH_OPEN_DO: String = "SEARCH_OPEN_DO"
            const val SEARCH_OPEN_FR: String = "SEARCH_OPEN_FR"
            const val SEARCH_OPEN_SA: String = "SEARCH_OPEN_SA"
            const val SEARCH_OPEN_SO: String = "SEARCH_OPEN_SO"
            const val DEBUG_TRACK_ANSICHT: String = "DEBUG_TRACK_ANSICHT"
            const val LOCATION_VIEW: String = "LOCATION_VIEW"
            const val SOIL_VIEW: String = "SOIL_VIEW"
            const val ONLY_OPEN: String = "ONLY_OPEN"
            const val ONLY_APPROVED: String = "ONLY_APPROVED"
            const val ONLY_WITH_KIDS: String = "ONLY_WITH_KIDS"
            const val SHOW_DEALERS: String = "SHOW_DEALERS"
            const val SHOW_EVERYONE: String = "SHOW_EVERYONE"
            const val SHOW_RACE: String = "SHOW_RACE"
            const val SHOW_MEMBER: String = "SHOW_MEMBER"
            const val SHOW_RATING: String = "SHOW_RATING"
            const val SHOW_DIFFICULT: String = "SHOW_DIFFICULT"
            const val SHOW_MX: String = "SHOW_MX"
            const val SHOW_QUAD: String = "SHOW_QUAD"
            const val SHOW_UTV: String = "SHOW_UTV"
            const val SHOW4X4: String = "SHOW4X4"
            const val SHOW_ENDURO: String = "SHOW_ENDURO"
            const val UNITS_KM: String = "UNITS_KM"
            const val REPAIR_D_B: String = "REPAIR_D_B"
            const val MAP_CLUSTER: String = "MAP_CLUSTER"
            const val MAP_TYPE: String = "MAP_TYPE"
            const val MAP_TRAFFIC: String = "MAP_TRAFFIC"
            const val MAP_ZOOM: String = "MAP_ZOOM"
            const val MAP_LATITUDE: String = "MAP_LATITUDE"
            const val MAP_LONGITUDE: String = "MAP_LONGITUDE"
            const val SHOW_ONLY_NEW_TRACK_STAGE: String = "SHOW_ONLY_NEW_TRACK_STAGE"
            const val MARKER_SHOW_LONG_CLICK_TEXT: String = "MARKER_SHOW_LONG_CLICK_TEXT"
            const val IMAGE_CROP_TO: String = "IMAGE_CROP_TO"
            const val SEARCH_SUPER_CROSS: String = "SEARCH_SUPER_CROSS"
            const val SEARCH_SHOWER: String = "SEARCH_SHOWER"
            const val SEARCH_WASH: String = "SEARCH_WASH"
            const val SEARCH_CAMPING: String = "SEARCH_CAMPING"
            const val SEARCH_ELECTRICITY: String = "SEARCH_ELECTRICITY"
            const val SEARCH_PICTURE: String = "SEARCH_PICTURE"
            const val RESTORE_I_D: String = "RESTORE_I_D"
            const val RESTORE_CONTENT_URI: String = "RESTORE_CONTENT_URI"
            const val LAST_OPEN_START_ACTIVITY: String = "LAST_OPEN_START_ACTIVITY"
            const val RESET_PICTURE_STORAGE_COUNT: String = "RESET_PICTURE_STORAGE_COUNT"
            const val TAB_DETAIL_POSITION: String = "TAB_DETAIL_POSITION"
        }
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0)

    var firstTimeUse: Boolean = true
        get() = sharedPreferences.getBoolean(
            Keys.FIRST_TIME_USE,
            true
        )
        set(value) {
            sharedPreferences.edit().putBoolean(
                Keys.FIRST_TIME_USE,
                value
            ).commit()
            field = value
        }

    var firstTimeLocation: Boolean = false
        get() = sharedPreferences.getBoolean(
            Keys.FIRST_TIME_LOCATION,
            false
        )
        set(value) {
            sharedPreferences.edit().putBoolean(
                Keys.FIRST_TIME_LOCATION,
                value
            ).commit()
            field = value
        }

    var firstTimeCountry: Boolean = false
        get() = sharedPreferences.getBoolean(
            Keys.FIRST_TIME_COUNTRY,
            false
        )
        set(value) {
            sharedPreferences.edit().putBoolean(
                Keys.FIRST_TIME_COUNTRY,
                value
            ).commit()
            field = value
        }

    val lastSyncTime: Long
        get() = sharedPreferences.getLong(
            Keys.LAST_SYNC_TIME,
            0L
        )

    val agreeTracking: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.AGREE_TRACKING,
            true
        )

    val agreeTrackSurveillance: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.AGREE_TRACK_SURVEILLANCE,
            true
        )

    val username: String
        get() = sharedPreferences.getString(
            Keys.USERNAME,
            ""
        )!!

    val searchOpenMo: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_OPEN_MO,
            true
        )

    val searchOpenDi: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_OPEN_DI,
            true
        )

    val searchOpenMi: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_OPEN_MI,
            true
        )

    val searchOpenDo: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_OPEN_DO,
            true
        )

    val searchOpenFr: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_OPEN_FR,
            true
        )

    val searchOpenSa: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_OPEN_SA,
            true
        )

    val searchOpenSo: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_OPEN_SO,
            true
        )

    val debugTrackAnsicht: Int
        get() = sharedPreferences.getInt(
            Keys.DEBUG_TRACK_ANSICHT,
            2
        )

    val locationView: Int
        get() = sharedPreferences.getInt(
            Keys.LOCATION_VIEW,
            -1
        )

    val soilView: Int
        get() = sharedPreferences.getInt(
            Keys.SOIL_VIEW,
            -1
        )

    val onlyOpen: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.ONLY_OPEN,
            false
        )

    val onlyApproved: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.ONLY_APPROVED,
            false
        )

    val onlyWithKids: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.ONLY_WITH_KIDS,
            false
        )

    val showDealers: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_DEALERS,
            true
        )

    val showEveryone: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_EVERYONE,
            true
        )

    val showRace: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_RACE,
            true
        )

    val showMember: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_MEMBER,
            true
        )

    val showRating: Float
        get() = sharedPreferences.getFloat(
            Keys.SHOW_RATING,
            0f
        )

    val showDifficult: Float
        get() = sharedPreferences.getFloat(
            Keys.SHOW_DIFFICULT,
            0f
        )

    val showMx: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_MX,
            false
        )

    val showQuad: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_QUAD,
            false
        )

    val showUtv: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_UTV,
            false
        )

    val show4x4: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW4X4,
            false
        )

    val showEnduro: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_ENDURO,
            false
        )

    val unitsKm: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.UNITS_KM,
            true
        )

    val repairDB: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.REPAIR_D_B,
            false
        )

    val mapCluster: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.MAP_CLUSTER,
            true
        )

    val mapType: Int
        get() = sharedPreferences.getInt(
            Keys.MAP_TYPE,
            0
        )

    val mapTraffic: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.MAP_TRAFFIC,
            false
        )

    val mapZoom: Float
        get() = sharedPreferences.getFloat(
            Keys.MAP_ZOOM,
            9f
        )

    val mapLatitude: Float
        get() = sharedPreferences.getFloat(
            Keys.MAP_LATITUDE,
            0f
        )

    val mapLongitude: Float
        get() = sharedPreferences.getFloat(
            Keys.MAP_LONGITUDE,
            0f
        )

    val showOnlyNewTrackStage: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SHOW_ONLY_NEW_TRACK_STAGE,
            true
        )

    val markerShowLongClickText: Int
        get() = sharedPreferences.getInt(
            Keys.MARKER_SHOW_LONG_CLICK_TEXT,
            0
        )

    val imageCropTo: Int
        get() = sharedPreferences.getInt(
            Keys.IMAGE_CROP_TO,
            1200
        )

    val searchSuperCross: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_SUPER_CROSS,
            false
        )

    val searchShower: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_SHOWER,
            false
        )

    val searchWash: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_WASH,
            false
        )

    val searchCamping: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_CAMPING,
            false
        )

    val searchElectricity: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_ELECTRICITY,
            false
        )

    val searchPicture: Boolean
        get() = sharedPreferences.getBoolean(
            Keys.SEARCH_PICTURE,
            false
        )

    val restoreID: Long
        get() = sharedPreferences.getLong(
            Keys.RESTORE_I_D,
            0L
        )

    val restoreContentUri: String?
        get() = sharedPreferences.getString(
            Keys.RESTORE_CONTENT_URI,
            null
        )

    val lastOpenStartActivity: String?
        get() = sharedPreferences.getString(
            Keys.LAST_OPEN_START_ACTIVITY,
            null
        )

    val resetPictureStorageCount: Int
        get() = sharedPreferences.getInt(
            Keys.RESET_PICTURE_STORAGE_COUNT,
            0
        )

    val tabDetailPosition: Int
        get() = sharedPreferences.getInt(
            Keys.TAB_DETAIL_POSITION,
            1
        )

    fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun edit(): MxPreferencesEditor {
        return MxPreferencesEditor(sharedPreferences.edit())
    }

    class MxPreferencesEditor(private val editor: SharedPreferences.Editor) {
        fun commit(): Boolean {
            return editor.commit()
        }

        fun apply() {
            editor.apply()
        }

        fun putLastSyncTime(value: Long): MxPreferencesEditor {
            editor.putLong(Keys.LAST_SYNC_TIME, value)
            return this
        }

        fun putAgreeTracking(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.AGREE_TRACKING, value)
            return this
        }

        fun putAgreeTrackSurveillance(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.AGREE_TRACK_SURVEILLANCE, value)
            return this
        }

        fun putUsername(value: String?): MxPreferencesEditor {
            editor.putString(Keys.USERNAME, value)
            return this
        }

        fun putSearchOpenMo(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_OPEN_MO, value)
            return this
        }

        fun putSearchOpenDi(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_OPEN_DI, value)
            return this
        }

        fun putSearchOpenMi(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_OPEN_MI, value)
            return this
        }

        fun putSearchOpenDo(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_OPEN_DO, value)
            return this
        }

        fun putSearchOpenFr(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_OPEN_FR, value)
            return this
        }

        fun putSearchOpenSa(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_OPEN_SA, value)
            return this
        }

        fun putSearchOpenSo(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_OPEN_SO, value)
            return this
        }

        fun putDebugTrackAnsicht(value: Int): MxPreferencesEditor {
            editor.putInt(Keys.DEBUG_TRACK_ANSICHT, value)
            return this
        }

        fun putLocationView(value: Int): MxPreferencesEditor {
            editor.putInt(Keys.LOCATION_VIEW, value)
            return this
        }

        fun putSoilView(value: Int): MxPreferencesEditor {
            editor.putInt(Keys.SOIL_VIEW, value)
            return this
        }

        fun putOnlyOpen(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.ONLY_OPEN, value)
            return this
        }

        fun putOnlyApproved(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.ONLY_APPROVED, value)
            return this
        }

        fun putOnlyWithKids(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.ONLY_WITH_KIDS, value)
            return this
        }

        fun putShowDealers(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_DEALERS, value)
            return this
        }

        fun putShowEveryone(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_EVERYONE, value)
            return this
        }

        fun putShowRace(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_RACE, value)
            return this
        }

        fun putShowMember(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_MEMBER, value)
            return this
        }

        fun putShowRating(value: Float): MxPreferencesEditor {
            editor.putFloat(Keys.SHOW_RATING, value)
            return this
        }

        fun putShowDifficult(value: Float): MxPreferencesEditor {
            editor.putFloat(Keys.SHOW_DIFFICULT, value)
            return this
        }

        fun putShowMx(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_MX, value)
            return this
        }

        fun putShowQuad(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_QUAD, value)
            return this
        }

        fun putShowUtv(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_UTV, value)
            return this
        }

        fun putShow4x4(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW4X4, value)
            return this
        }

        fun putShowEnduro(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SHOW_ENDURO, value)
            return this
        }

        fun putUnitsKm(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.UNITS_KM, value)
            return this
        }

        fun putRepairDB(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.REPAIR_D_B, value)
            return this
        }

        fun putMapCluster(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.MAP_CLUSTER, value)
            return this
        }

        fun putMapType(value: Int): MxPreferencesEditor {
            editor.putInt(Keys.MAP_TYPE, value)
            return this
        }

        fun putMapTraffic(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.MAP_TRAFFIC, value)
            return this
        }

        fun putMapZoom(value: Float): MxPreferencesEditor {
            editor.putFloat(Keys.MAP_ZOOM, value)
            return this
        }

        fun putMapLatitude(value: Float): MxPreferencesEditor {
            editor.putFloat(Keys.MAP_LATITUDE, value)
            return this
        }

        fun putMapLongitude(value: Float): MxPreferencesEditor {
            editor.putFloat(Keys.MAP_LONGITUDE, value)
            return this
        }

        fun putMarkerShowLongClickText(value: Int): MxPreferencesEditor {
            editor.putInt(Keys.MARKER_SHOW_LONG_CLICK_TEXT, value)
            return this
        }

        fun putSearchSuperCross(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_SUPER_CROSS, value)
            return this
        }

        fun putSearchShower(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_SHOWER, value)
            return this
        }

        fun putSearchWash(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_WASH, value)
            return this
        }

        fun putSearchCamping(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_CAMPING, value)
            return this
        }

        fun putSearchElectricity(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_ELECTRICITY, value)
            return this
        }

        fun putSearchPicture(value: Boolean): MxPreferencesEditor {
            editor.putBoolean(Keys.SEARCH_PICTURE, value)
            return this
        }

        fun putRestoreID(value: Long): MxPreferencesEditor {
            editor.putLong(Keys.RESTORE_I_D, value)
            return this
        }

        fun putRestoreContentUri(value: String?): MxPreferencesEditor {
            editor.putString(Keys.RESTORE_CONTENT_URI, value)
            return this
        }

        fun putLastOpenStartActivity(value: String?): MxPreferencesEditor {
            editor.putString(Keys.LAST_OPEN_START_ACTIVITY, value)
            return this
        }

        fun putResetPictureStorageCount(value: Int): MxPreferencesEditor {
            editor.putInt(Keys.RESET_PICTURE_STORAGE_COUNT, value)
            return this
        }

        fun putTabDetailPosition(value: Int): MxPreferencesEditor {
            editor.putInt(Keys.TAB_DETAIL_POSITION, value)
            return this
        }
    }

    companion object {
        const val PREFERENCES_NAME: String = "MxPreferences"

        private var sInstance: MxPreferences? = null

        @JvmStatic
        val instance: MxPreferences
            get() {
                if (sInstance == null) {
                    sInstance =
                        MxPreferences(Mechanoid.getApplicationContext())
                }

                return sInstance!!
            }
    }
}
