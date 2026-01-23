package info.mx.tracks.trackdetail

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.MxCoreApplication.Companion.isAdmin
import info.mx.tracks.R
import info.mx.tracks.common.On3StateClickListener
import info.mx.tracks.common.PictureHelper
import info.mx.tracks.image.ActivityBaseImageSlider
import info.mx.tracks.image.ActivityImageSlider
import info.mx.tracks.recyclerview.CursorRecyclerViewAdapter
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks
import info.mx.tracks.sqlite.PicturesRecord
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * [Gist](https://gist.github.com/skyfishjy/443b7448f59be978bc59)
 */
class ImageCursorAdapter(private val activity: Activity, cursor: Cursor?, private val size: Int, private val openDetailActivity: Boolean) :
    CursorRecyclerViewAdapter<ImageCursorAdapter.ViewHolder>(cursor) {

    private var trackId: Long = 0

    interface OnImageListItemClick {
        fun onImageItemClick(position: Int, imageRestId: Long)
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imagePicture)
        var imageApproved: ImageView = view.findViewById(R.id.imageApproved)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_imageslider, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, cursor: Cursor?) {
        val record = PicturesRecord.fromCursor(cursor)
        trackId = SQuery.newQuery().expr(Tracks.REST_ID, SQuery.Op.EQ, record.trackRestId).firstLong(Tracks.CONTENT_URI, Tracks._ID)
        val imgView = viewHolder.imageView
        imgView.setTag(R.string.HLIST_REST_ID, record.restId)
        imgView.setTag(R.string.HLIST_POSITION, cursor!!.position)
        imgView.setOnClickListener { v: View ->
            if (openDetailActivity) {
                openImageSlideActivity(v.getTag(R.string.HLIST_REST_ID) as Long)
            }
            if (activity !is OnImageListItemClick) {
                Timber.w("implement OnImageListItemClick missing")
            } else {
                (activity as OnImageListItemClick).onImageItemClick(
                    v.getTag(R.string.HLIST_POSITION) as Int,
                    v.getTag(R.string.HLIST_REST_ID) as Long
                )
            }
        }
        val imgApproved = viewHolder.imageApproved
        imgApproved.visibility = if (isAdmin && record.restId > 0) View.VISIBLE else View.GONE
        imgApproved.setImageLevel((record.approved + 1).toInt())
        imgApproved.tag = record.id
        imgApproved.setOnClickListener(object : On3StateClickListener() {
            override fun onClick(view: View) {
                val pictureId = view.tag as Long
                val recordP = PicturesRecord.get(pictureId)
                val app = activity.application as MxCoreApplication
                app.confirmPicture(activity, recordP.restId, recordP.approved.toInt())
            }
        })
        val filepathLocal: String = if (size == activity.resources.getDimension(R.dimen.thumbnail_size_dp).roundToInt()) {
            record.localthumb
        } else {
            record.localfile
        }
        PictureHelper.checkAndSetImage(activity, record, imgView, filepathLocal, size)
    }

    private fun openImageSlideActivity(imageId: Long) {
        val intent = Intent(activity, ActivityImageSlider::class.java)
        val bundle = Bundle()
        bundle.putLong(ActivityBaseImageSlider.TRACK_ID, trackId)
        bundle.putLong(ActivityBaseImageSlider.IMAGE_ID, imageId)
        intent.putExtras(bundle)
        activity.startActivity(intent)
    }
}
