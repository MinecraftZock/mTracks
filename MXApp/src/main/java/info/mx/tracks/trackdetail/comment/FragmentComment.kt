package info.mx.tracks.trackdetail.comment

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.Settings.Secure
import android.view.*
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.NavUtils
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.sqlite.MxInfoDBContract.Ratings
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks
import org.koin.android.ext.android.inject

class FragmentComment : FragmentUpDown(), androidx.loader.app.LoaderManager.LoaderCallbacks<Cursor> {

    private val projection = arrayOf(Ratings.USERNAME, Ratings.CHANGED, Ratings.COUNTRY, Ratings.NOTE, Ratings.RATING)
    internal var to = intArrayOf(R.id.comlst_username, R.id.comlst_datum, R.id.comlst_country, R.id.comlst_note, R.id.comlst_rating)

    private var mAdapter: androidx.cursoradapter.widget.SimpleCursorAdapter? = null
    private var tracksRestID: Long = 0

    val mxDatabase: MxDatabase by inject()

    @SuppressLint("HardwareIds")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.fragment_list, null)
        // keyboard hide
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val emptyView = view.findViewById<TextView>(R.id.txt_no_entry)

        val listRatings = view.findViewById<ListView>(R.id.listEntries)
        listRatings.emptyView = emptyView
        listRatings.isLongClickable = true
        listRatings.setOnItemLongClickListener { _, _, _, id ->
            SQuery.newQuery()
                .expr(Ratings.TRACK_REST_ID, Op.EQ, tracksRestID)
                .expr(Ratings._ID, Op.EQ, id)
                .expr(Ratings.ANDROIDID, Op.EQ, Secure.getString(requireActivity().contentResolver, Secure.ANDROID_ID))
                .delete(Ratings.CONTENT_URI)
            true
        }

        mAdapter = androidx.cursoradapter.widget.SimpleCursorAdapter(requireActivity(), R.layout.item_comment, null, projection, to, 0)
        mAdapter!!.viewBinder = CommentViewBinder(requireActivity())

        listRatings.adapter = mAdapter

        loaderManager.initLoader(LOADER_RATINGS, arguments, this)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(requireActivity())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): androidx.loader.content.Loader<Cursor> {
        val trackId = bundle!!.getLong(RECORD_ID_LOCAL)
        tracksRestID = SQuery.newQuery()
            .expr(Tracks._ID, Op.EQ, trackId)
            .firstLong(Tracks.CONTENT_URI, Tracks.REST_ID)
        return SQuery.newQuery()
            .expr(Ratings.TRACK_REST_ID, Op.EQ, tracksRestID)
            .expr(Ratings.NOTE, Op.NEQ, "")
            .expr(Ratings.DELETED, Op.NEQ, 1)
            .createSupportLoader(Ratings.CONTENT_URI, null, Ratings.CHANGED + " desc")
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, data: Cursor) {
        mAdapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        mAdapter!!.swapCursor(null)
    }

    override fun fillMask(newId: Long) {
        requireArguments().putLong(RECORD_ID_LOCAL, newId)
        loaderManager.restartLoader(LOADER_RATINGS, arguments, this)
    }

    companion object {
        private const val LOADER_RATINGS = 0
    }

}
