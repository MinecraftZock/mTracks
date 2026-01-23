package info.hannes.mxadmin.deepLink

/*
 * * Specific implementation for Matcher registry
 */
class MatcherRegistry : AbstractMatcherRegistry() {

    override fun addMatchers(matcher: MutableList<DeepLinkMatcher>) {
        matcher.add(MainMatcher())
    }

}
