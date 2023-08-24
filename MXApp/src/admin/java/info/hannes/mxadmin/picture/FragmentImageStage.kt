package info.hannes.mxadmin.picture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.google.android.material.snackbar.Snackbar
import com.ortiz.touchview.TouchImageView
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract
import info.hannes.mechadmin_gen.sqlite.PictureStageRecord
import info.hannes.mechadmin_gen.sqlite.TrackstageBrotherRecord
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.SecHelper
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.trackdetail.ActivityTrackDetail
import info.mx.tracks.trackdetail.FragmentTrackDetail
import timber.log.Timber

class FragmentImageStage : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    var imageView: TouchImageView? = null
        private set
    protected var imageClientId: Long = 0
    private var textName: TextView? = null
    private var chkUnInteresting: CheckBox? = null
    private var btnFacebook: ImageButton? = null
    private var btnWww: ImageButton? = null
    private var currStage: PictureStageRecord? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        @SuppressLint("InflateParams")
        val rootView = inflater.inflate(R.layout.fragment_image_stage_slider, null)

        imageClientId = requireArguments().getLong(ActivityBaseImageStageSlider.IMAGE_CLIENT_ID, 0)

        textName = rootView.findViewById(R.id.textStageName)
        textName!!.setOnClickListener { v -> openDetail(v.tag as Long, 0) }
        chkUnInteresting = rootView.findViewById(R.id.chkUninteressant)
        chkUnInteresting!!.setOnClickListener { v ->
            currStage!!.uninteressant = (if ((v as CheckBox).isChecked) 1 else 0).toLong()
            currStage!!.save()
        }
        btnFacebook = rootView.findViewById(R.id.btnStageFacebook)
        btnFacebook!!.setOnClickListener { v -> openFaceBook(requireActivity(), v.tag as String) }
        btnWww = rootView.findViewById(R.id.btnStageWww)
        btnWww!!.setOnClickListener { v -> FragmentTrackDetail.openWebSite(requireActivity(), v.tag as String) }
        imageView = rootView.findViewById(R.id.imageStageFullSize)

        loaderManager.initLoader(LOADER_PICTURE_FULL_SIZE, null, this)

        return rootView
    }

    // boilerplate begin
    private fun openFaceBook(activity: Activity, facebook: String) {
        try {
            activity.startActivity(getFaceBookIntent(SecHelper.decryptB64(facebook)))
        } catch (e: ActivityNotFoundException) {
            showInfo(activity, activity.getString(R.string.wrong_facebook_url))
        }
    }

    private fun getFaceBookIntent(facebook: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(facebook))
    }

    private fun showInfo(activity: Activity, text: String) {
        val snackbar: Snackbar
        snackbar = Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
    // boilerplate end

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
//            LOADER_PICTURE_FULL_SIZE ->
        return SQuery
                .newQuery().expr(MxAdminDBContract.PictureStage._ID, SQuery.Op.EQ, imageClientId)
                .createSupportLoader(MxAdminDBContract.PictureStage.CONTENT_URI, null, MxAdminDBContract.PictureStage._ID)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_PICTURE_FULL_SIZE -> if (cursor.count >= 1) {
                if (cursor.isBeforeFirst) {
                    cursor.moveToNext()
                }
                val rec = PictureStageRecord.fromCursor(cursor)
                setImage(rec)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_PICTURE_FULL_SIZE -> {
            }
        }
    }

    @SuppressLint("SetTextI18n")
    protected fun setImage(record: PictureStageRecord) {
        currStage = record
        val display = requireActivity().windowManager.defaultDisplay
        val size = getDisplaySize(display)
        val pictureShown = imageView?.let {
            PictureAdminHelper.checkAndSetImage(requireActivity(), record, it, if (size.y > size.x) size.y else size.x)
        }

        val trackBrother = TrackstageBrotherRecord.get(record.trackId)
        val track = SQuery.newQuery()
                .expr(MxInfoDBContract.Tracks.REST_ID, SQuery.Op.EQ, trackBrother!!.trackRestId)
                .selectFirst<TracksRecord>(MxInfoDBContract.Tracks.CONTENT_URI)
        if (track != null) {
            textName!!.text = "[" + track.country + "] " + track.trackname
            textName!!.tag = track.id
            chkUnInteresting!!.isChecked = record.uninteressant == 1L
            chkUnInteresting!!.text = "[" + track.id + "] uninteressant"
            btnFacebook!!.tag = SecHelper.decryptB64(track.facebook)
            btnFacebook!!.visibility = if (track.facebook != null && track.facebook != "") View.VISIBLE else View.GONE
            btnWww!!.tag = SecHelper.decryptB64(track.url)
            btnWww!!.visibility = if (track.url != null && track.url != "") View.VISIBLE else View.GONE
        } else {
            textName!!.text = "[?] unbekannt"
            chkUnInteresting!!.isChecked = record.uninteressant == 1L
            chkUnInteresting!!.text = "[?] uninteressant"
            btnFacebook!!.visibility = View.GONE
            btnWww!!.visibility = View.GONE
        }

        // stop loading file changes
        pictureShown?.let {
            if (it)
                loaderManager.destroyLoader(LOADER_PICTURE_FULL_SIZE)
        }
    }

    private fun openDetail(id: Long, position: Int) {
        val qWfIntent = Intent(activity, ActivityTrackDetail::class.java)
        val bundle = Bundle()
        Timber.d("open:%s", id)
        bundle.putLong(FragmentUpDown.RECORD_ID_LOCAL, id)
        bundle.putString(FragmentUpDown.CONTENT_URI, MxInfoDBContract.Tracksges.CONTENT_URI.toString())
        //        bundle.putString(FragmentUpDown.FILTER, mCurFilter);
        //        bundle.putString(FragmentUpDown.ORDER, FragmentTrackList.this.getArguments().getString(FragmentUpDown.ORDER).toLowerCase());
        bundle.putInt(FragmentUpDown.CURSOR_POSITION, position)
        qWfIntent.putExtras(bundle)
        startActivity(qWfIntent)
    }

    companion object {

        private const val LOADER_PICTURE_FULL_SIZE = 123

        /**
         * Create a new instance of FragmentImage, providing "imageRestId" as an argument.
         */
        internal fun newInstance(imageClientID: Long): FragmentImageStage {
            val f = FragmentImageStage()

            // Supply num input as an argument.
            val args = Bundle()
            args.putLong(ActivityBaseImageStageSlider.IMAGE_CLIENT_ID, imageClientID)
            f.arguments = args

            return f
        }

        private fun getDisplaySize(display: Display): Point {
            val point = Point()
            display.getSize(point)
            return point
        }
    }
}
