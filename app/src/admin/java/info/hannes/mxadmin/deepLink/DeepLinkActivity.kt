package info.hannes.mxadmin.deepLink

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import info.hannes.mxadmin.base.ActivityAdminBase
import info.mx.tracks.BuildConfig
import info.mx.tracks.MxCoreApplication.Companion.doSync
import info.mx.tracks.common.parcelable
import timber.log.Timber

class DeepLinkActivity : ActivityAdminBase() {

    private var matcherRegistry = MatcherRegistry()
    private var saveIntent: Intent? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(intent.toString())
        if (savedInstanceState != null && savedInstanceState.containsKey(DEEP_LINK)) {
            saveIntent = savedInstanceState.parcelable(DEEP_LINK)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(DEEP_LINK, saveIntent)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        intent.data?.let {
            handleIntent(it)
        }
    }

    private fun configureIntent(intent: Intent) {
        val flags: Int = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        if (intent.getBooleanExtra(KEY_DO_NOT_CLEAR_TOP, false)) {
            intent.flags = flags
        } else {
            // FLAG_ACTIVITY_NEW_TASK
            intent.flags = flags or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    private fun handleIntent(uri: Uri) {
        saveIntent = matcherRegistry.findMatch(this, uri)
        if (saveIntent == null) {
            Timber.w("could not match deeplink %s", uri.toString())
            finish(false)
        } else {
            doSync(true, force = true, flavor = BuildConfig.FLAVOR)
            Timber.w("match deeplink %s", uri.toString())
            configureIntent(saveIntent!!)
            startActivity(saveIntent)
            saveIntent = null
            finish(true)
        }
    }

    private fun finish(success: Boolean) {
        setResult(if (success) Activity.RESULT_OK else Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        private const val DEEP_LINK = "DEEP_LINK"
        val KEY_DO_NOT_CLEAR_TOP = DeepLinkActivity::class.java.simpleName + "" + ".KEY_DO_NOT_CLEAR_TOP"
    }
}
