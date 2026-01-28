package info.hannes.mxadmin.deepLink

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.common.FragmentUpDown.Companion.CONTENT_URI
import info.mx.tracks.common.FragmentUpDown.Companion.RECORD_ID_LOCAL
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracksges
import info.mx.tracks.trackdetail.ActivityTrackDetail
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

    companion object {
        fun createDeepLinkIntent(context: Context, uri: Uri?): Intent {
            val localId = SQuery.newQuery()
                .expr(Tracks.REST_ID, SQuery.Op.EQ, uri!!.getQueryParameter("trackid"))
                .firstLong(Tracks.CONTENT_URI, Tracks._ID)
            val intent = Intent(context, ActivityTrackDetail::class.java)
            intent.putExtra(RECORD_ID_LOCAL, localId)
            intent.putExtra(CONTENT_URI, Tracksges.CONTENT_URI.toString())
            return intent
        }
    }
}
