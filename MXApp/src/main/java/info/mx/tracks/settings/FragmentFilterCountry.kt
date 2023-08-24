package info.mx.tracks.settings

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import info.mx.tracks.base.ListFragmentBase
import info.mx.tracks.sqlite.MxInfoDBContract.Countrycount
import info.mx.tracks.R
import android.os.Bundle
import android.view.Menu
import info.mx.tracks.sqlite.CountrycountRecord
import timber.log.Timber
import android.widget.CheckBox
import info.mx.tracks.sqlite.CountryRecord
import android.widget.TextView
import com.robotoworks.mechanoid.db.SQuery
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import info.mx.tracks.sqlite.MxInfoDBContract
import java.util.*

class FragmentFilterCountry : ListFragmentBase(), LoaderManager.LoaderCallbacks<Cursor> {
    private var mAdapter: SimpleCursorAdapter? = null
    private val projection =
        arrayOf(Countrycount.COUNTRY, Countrycount.COUNTRY, Countrycount.SHOW, Countrycount.COUNT)
    private val to = intArrayOf(
        R.id.tvFilterCountryText,
        R.id.imFilterCountry,
        R.id.chkFilterCountry,
        R.id.tvFilterCountryAnz
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillData()
        setEmptyText(getString(R.string.empty))
        setHasOptionsMenu(true)
        loaderManager.initLoader(0, this.arguments, this)
    }

    private fun fillData() {
        // Create an empty adapter we will use to display the loaded data.
        mAdapter =
            SimpleCursorAdapter(requireActivity(), R.layout.item_country, null, projection, to, 0)
        mAdapter!!.viewBinder = ViewBinderCountry(requireActivity())
        listAdapter = mAdapter
    }

    private inner class ViewBinderCountry(private val context: Context) :
        SimpleCursorAdapter.ViewBinder {
        override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
            var res = false
            val cRec = CountrycountRecord.fromCursor(cursor)
            if (view.id == R.id.imFilterCountry) {
                if (cursor.getString(columnIndex) != null) {
                    val value =
                        cursor.getString(columnIndex).lowercase(Locale.getDefault()) + "2x"
                    val id = requireActivity().resources.getIdentifier(
                        value,
                        "drawable",
                        requireActivity().packageName
                    )
                    (view as ImageView).setImageResource(id)
                    Timber.d("im " + cRec.id + " " + cRec.country + " " + cRec.count + " " + cRec.show)
                } else {
                    Timber.d(
                        "im " + cursor.getString(0) + " xx " + cursor.getString(2) + " " + cursor.getString(
                            3
                        )
                    )
                }
                res = true
            } else if (view.id == R.id.chkFilterCountry) {
                val chk = view as CheckBox
                chk.text = ""
                chk.tag = cRec.id // cursor.getLong(cursor.getColumnIndex(Countrycount._ID)));
                chk.isChecked = cursor.getInt(columnIndex) == 1
                chk.setOnClickListener { view1: View ->
                    val chk1 = view1 as CheckBox
                    val rec = CountryRecord.get(view1.getTag().toString().toLong())
                    if (rec != null) {
                        rec.show = if (chk1.isChecked) 1 else 0.toLong()
                        rec.save()
                    }
                    requireActivity().invalidateOptionsMenu()
                }
                res = true
            } else if (view.id == R.id.tvFilterCountryText) {
                val tv = view as TextView
                val loc = Locale(cRec.country, cRec.country)
                tv.text = loc.displayCountry
                res = true
            } else if (view.id == R.id.tvFilterCountryAnz) {
                val tv1 = view as TextView
                tv1.text = String.format(context.getString(R.string.country_anz), cRec.count)
                res = true
            }
            return res
        }
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
        return SQuery.newQuery()
            .createSupportLoader(Countrycount.CONTENT_URI, null, Countrycount.COUNTRY)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        Timber.d("CountryCount " + data.count)
        mAdapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter!!.swapCursor(null)
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        super.onListItemClick(listView, view, position, id)
        val rec = CountryRecord.get(id)
        if (rec != null) {
            rec.show = if (rec.show == 1L) 0 else 1.toLong()
            rec.save()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.activity_filter_country, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings_filter_country) {
            val anz: Int
            val query = SQuery.newQuery()
            anz = if (ausgeblendet == 0) {
                query.expr(MxInfoDBContract.Country.SHOW, SQuery.Op.EQ, 1) // obsolete
                MxInfoDBContract.Country.newBuilder().setShow(0).update(query, true)
            } else {
                query.expr(MxInfoDBContract.Country.SHOW, SQuery.Op.EQ, 0)
                MxInfoDBContract.Country.newBuilder().setShow(1).update(query, true)
            }
            Timber.d("update country %s", anz)
            item.icon = getIcon4SetAllCountry(requireActivity())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings_filter_country).icon =
            getIcon4SetAllCountry(requireActivity())
        super.onPrepareOptionsMenu(menu)
    }

    private fun getIcon4SetAllCountry(context: Context): Drawable? {
        ausgeblendet = SQuery.newQuery().expr(MxInfoDBContract.Country.SHOW, SQuery.Op.EQ, 0)
            .count(MxInfoDBContract.Country.CONTENT_URI)
        val all = SQuery.newQuery().count(MxInfoDBContract.Country.CONTENT_URI)
        val drawable: Drawable?
        drawable = if (ausgeblendet == 0) {
            ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox)
        } else if (ausgeblendet == all) {
            ContextCompat.getDrawable(
                context,
                R.drawable.actionbar_checkbox_empty
            )
        } else {
            ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox_grey)
        }
        return drawable
    }

    companion object {
        private var ausgeblendet = 0
    }
}