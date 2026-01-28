package info.mx.core.ops

import android.os.Bundle
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.commonlib.NetworkHelper.isOnline
import info.mx.core.MxCoreApplication.Companion.isAdminOrDebug
import info.mx.core.MxCoreApplication.Companion.mxInfo
import info.mx.core_generated.ops.AbstractOpPostRatingsOperation
import info.mx.core.common.getAndroidId
import info.mx.core_generated.rest.PostRatingsRequest
import info.mx.core_generated.rest.RESTrating
import info.mx.core_generated.sqlite.MxInfoDBContract.Ratings
import info.mx.core_generated.sqlite.RatingsRecord
import timber.log.Timber

class OpPostRatingsOperation : AbstractOpPostRatingsOperation() {
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
