package info.mx.comlib.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import info.mx.rxcommlibrary.BuildConfig

// TODO rework all
class CommLibPrefs private constructor(context: Context?) {

    private val sharedPreferences: SharedPreferences = context!!.getSharedPreferences(PREFERENCES_NAME, 0)
    val serverUrl: String
        get() = sharedPreferences.getString(SERVER_URL, BuildConfig.BACKEND_PROD_URL)!!

    fun edit(): MxPreferencesEditor {
        return MxPreferencesEditor(sharedPreferences.edit())
    }

    inner class MxPreferencesEditor(private val editor: SharedPreferences.Editor) {
        fun commit(): Boolean {
            return editor.commit()
        }

        fun apply() {
            editor.apply()
        }

        fun clear(): MxPreferencesEditor {
            editor.clear()
            return this
        }

        fun putServerUrl(value: String?): MxPreferencesEditor {
            editor.putString(SERVER_URL, value)
            return this
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "CommLibPreferences"
        const val SERVER_URL = "SERVER_URL"

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        @SuppressLint("StaticFieldLeak")
        private var sInstance: CommLibPrefs? = null

        @JvmStatic
        val instance: CommLibPrefs
            get() {
                if (sInstance == null) {
                    sInstance = CommLibPrefs(context)
                }
                return sInstance!!
            }

        fun init(context: Context) {
            Companion.context = context
        }
    }

}
