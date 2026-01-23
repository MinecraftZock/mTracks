package info.hannes.mxadmin.deepLink

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.*

abstract class AbstractMatcherRegistry protected constructor() {

    init {
        internalMatchers(matcher)
    }

    // avoid "Calling non-final function"
    private fun internalMatchers(matcher: MutableList<DeepLinkMatcher>) {
        addMatchers(matcher)
    }

    protected abstract fun addMatchers(matcher: MutableList<DeepLinkMatcher>)

    fun findMatch(context: Context, uri: Uri): Intent? {
        for (matcher in matcher) {
            val intent = matcher.matches(context, uri)
            if (intent != null) {
                return intent
            }
        }
        return null
    }

    companion object {
        val matcher: MutableList<DeepLinkMatcher> = ArrayList()
    }

}
