package info.hannes.mxadmin.deepLink

import android.content.Context
import android.content.Intent
import android.net.Uri

/*
 * A single matcher to match a deeplink URI to a specific activity intent.
 */
interface DeepLinkMatcher {
    /**
     * Tries to match the given URI to a specific intent we can handle.
     *
     * @param context The context
     * @param uri     The deep link URI
     * @return The intent if the match succeeded, otherwise null
     */
    fun matches(context: Context, uri: Uri): Intent?
}