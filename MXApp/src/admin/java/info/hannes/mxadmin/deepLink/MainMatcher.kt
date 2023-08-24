package info.hannes.mxadmin.deepLink

import android.content.Context
import android.net.Uri
import info.mx.tracks.trackdetail.ActivityTrackDetail.Companion.createDeepLinkIntent
import io.reactivex.functions.BiFunction

class MainMatcher : AbstractDeepLinkMatcher() {

    init {
        //        addDeeplink("ecoupons(/not_activated|/activated)?(/filter)?/?", (context, uri) -> MainActivity.createDeepLinkIntent(context,
        //                 CouponsFragment.class, uri));
        //        addDeeplink("ecoupons/(\\d+)(/)*", (context, uri) -> CouponDetailActivity.createIntent(context, getSomethingId(uri)));
        //        addDeeplink("(feed|dashboard)(/)*", (context, uri) -> MainActivity.createDeepLinkIntent(context, FeedFragment.class, uri));
        //        addDeeplink("shopping/azlist(/)*", (context, uri) -> MainActivity.createDeepLinkIntent(context, OnlineShoppingListFragment.class,
        // uri));
        //        addDeeplink("accountbalance/payback(/)*", (context, uri) -> MainActivity.createDeepLinkIntent(context, TransactionFragment.class,
        // uri));
        //        addDeeplink("more(/)*", (context, uri) -> MainActivity.createDeepLinkIntent(context, MoreFragment.class, uri));
        //        addDeeplink("shopping/detail/(.+)", (context, uri) -> OnlineShoppingDetailActivity.createIntent(context, getShoppingDetails(uri)));
        //        addDeeplink("(more/)?mypayback/?", (context, uri) -> MyAccountOverviewActivity.createIntent(context));
        //        addDeeplink("more/help/?", (context, uri) -> WebViewActivity.createIntent(context, WebViewConfig.FAQ));
        addDeepLink("trackedit/?", BiFunction { context: Context?, uri: Uri? -> createDeepLinkIntent(context!!, uri) })
    }
}
