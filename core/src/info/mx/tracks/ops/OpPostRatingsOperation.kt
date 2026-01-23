package info.mx.tracks.ops

import android.os.Bundle
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.commonlib.NetworkHelper.isOnline
import info.mx.tracks.MxCoreApplication.Companion.isAdminOrDebug
import info.mx.tracks.MxCoreApplication.Companion.mxInfo
import info.mx.tracks.common.getAndroidId
import info.mx.tracks.rest.PostRatingsRequest
import info.mx.tracks.rest.RESTrating
import info.mx.tracks.sqlite.MxInfoDBContract.Ratings
import info.mx.tracks.sqlite.RatingsRecord
import timber.log.Timber

internal class OpPostRatingsOperation : AbstractOpPostRatingsOperation() {
    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val webClient = mxInfo
        return try {
            if (isOnline(context.applicationContext)) {
                val ratingsNeu = SQuery.newQuery()
                    .expr(Ratings.REST_ID, SQuery.Op.LTEQ, 0)
                    .or()
                    .append(Ratings.REST_ID + " is null")
                    .select<RatingsRecord>(Ratings.CONTENT_URI)
                for (ratingDB in ratingsNeu) {
                    val ratingREST = RESTrating()
                    ratingREST.country = ratingDB.country
                    ratingREST.note = ratingDB.note
                    ratingREST.rating = ratingDB.rating.toInt()
                    ratingREST.androidid = context.applicationContext.getAndroidId()
                    ratingREST.trackId = ratingDB.trackRestId.toInt()
                    ratingREST.username = ratingDB.username
                    val request = PostRatingsRequest(ratingREST)
                    val res = webClient.postRatings(request)
                    val response = res.parse()
                    res.checkResponseCodeOk()
                    ratingDB.restId = response.baseResponse.id.toLong()
                    ratingDB.save()
                }
            }
            val bundle = Bundle()
            OperationResult.ok(bundle)
        } catch (e: Exception) {
            Timber.e(e)
            if (isAdminOrDebug) {
                Toast.makeText(context.applicationContext, "${this.javaClass.name} ${e.message}", Toast.LENGTH_LONG).show()
            }
            OperationResult.error(e)
        }
    }
}
