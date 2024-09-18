package info.mx.tracks.trackdetail.event

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.NavUtils
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.sqlite.MxInfoDBContract.Events2series
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks
import timber.log.Timber

class FragmentEvent : FragmentUpDown(), LoaderManager.LoaderCallbacks<Cursor> {
    private val projectionEvents = arrayOf(Events2series.EVENT_DATE, Events2series.COMMENT, Events2series.SERIESNAME)
    private val toEvents = intArrayOf(R.id.textDatum, R.id.textKommentar, R.id.textSerie)
    private lateinit var adapter: SimpleCursorAdapter
    private var tracksRestID: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.fragment_list, null)
        // keyboard hide
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val emptyView = view.findViewById<TextView>(R.id.txt_no_entry)
        val listRatings = view.findViewById<ListView>(R.id.listEntries)
        listRatings.emptyView = emptyView
        listRatings.isLongClickable = true
        listRatings.onItemLongClickListener = OnItemLongClickListener { _: AdapterView<*>?, _: View?, _: Int, id: Long ->
            Timber.d("()")
            SQuery.newQuery()
                .expr(Events2series.TRACK_REST_ID, SQuery.Op.EQ, tracksRestID)
                .expr(Events2series._ID, SQuery.Op.EQ, id)
                .delete(Events2series.CONTENT_URI)
            true
        }
        adapter = SimpleCursorAdapter(
            requireActivity(),
            R.layout.item_event,
            null,
            projectionEvents,
            toEvents,
            0
        )
        adapter.viewBinder = EventsViewBinder()
        listRatings.adapter = adapter
        loaderManager.initLoader(LOADER_EVENTS, arguments, this)
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

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
        val trackId = bundle!!.getLong(RECORD_ID_LOCAL)
        tracksRestID = SQuery.newQuery()
            .expr(Tracks._ID, SQuery.Op.EQ, trackId)
            .firstLong(Tracks.CONTENT_URI, Tracks.REST_ID)
        return SQuery.newQuery()
            .expr(Events2series.TRACK_REST_ID, SQuery.Op.EQ, tracksRestID)
            .expr(Events2series.APPROVED, SQuery.Op.NEQ, -11)
            .createSupportLoader(
                Events2series.CONTENT_URI,
                null,
                Events2series.EVENT_DATE + " desc"
            )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        adapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.swapCursor(null)
    }

    override fun fillMask(localId: Long) {
        requireArguments().putLong(RECORD_ID_LOCAL, localId)
        loaderManager.restartLoader(LOADER_EVENTS, arguments, this)
    }

    companion object {
        private const val LOADER_EVENTS = 0
    }
}
