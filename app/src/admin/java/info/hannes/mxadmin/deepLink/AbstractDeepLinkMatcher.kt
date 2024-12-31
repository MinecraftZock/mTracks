package info.hannes.mxadmin.deepLink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Pair
import io.reactivex.functions.BiFunction
import java.util.*
import java.util.regex.Pattern

open class AbstractDeepLinkMatcher : DeepLinkMatcher {

    private val intentFactories: MutableList<Pair<Pattern, BiFunction<Context, Uri, Intent>>> = ArrayList()

    protected fun addDeepLink(regex: String, intentFactory: BiFunction<Context, Uri, Intent>) {
        intentFactories.add(Pair(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), intentFactory))
    }

    override fun matches(context: Context, uri: Uri): Intent? {
        val target = uri.host!!.trim { it <= ' ' } + uri.path!!.trim { it <= ' ' }
        for (pair in intentFactories) {
            if (pair.first.matcher(target).matches()) {
                try {
                    return pair.second.apply(context, uri)
                } catch (ignored: Exception) {
                }
            }
        }
        return null
    }
}