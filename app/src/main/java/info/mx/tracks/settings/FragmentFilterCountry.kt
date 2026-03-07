package info.mx.tracks.settings

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.Lifecycle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import info.mx.core_generated.sqlite.CountrycountRecord
import info.mx.core_generated.sqlite.MxInfoDBContract.Countrycount
import info.mx.tracks.R
import info.mx.tracks.base.ListFragmentBase
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.util.getDrawableIdentifier
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Locale

class FragmentFilterCountry : ListFragmentBase(), LoaderManager.LoaderCallbacks<Cursor> {

    val mxDatabase: MxDatabase by inject()

    private lateinit var adapter: SimpleCursorAdapter
    private val projection = arrayOf(Countrycount.COUNTRY, Countrycount.COUNTRY, Countrycount.SHOW, Countrycount.COUNT)
    private val to = intArrayOf(
        R.id.tvFilterCountryText,
        R.id.imFilterCountry,
        R.id.chkFilterCountry,
        R.id.tvFilterCountryAnz
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = getView()?.findViewById<ListView>(android.R.id.list)
        list!!.isScrollbarFadingEnabled = false
        fillData()

        setEmptyText(getString(R.string.empty))

        // Setup menu using MenuProvider
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.activity_filter_country, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return handleMenuItemSelected(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        loaderManager.initLoader(0, this.arguments, this)
    }

    private fun fillData() {
        // Create an empty adapter we will use to display the loaded data.
        adapter = SimpleCursorAdapter(requireActivity(), R.layout.item_country, null, projection, to, 0)
        adapter.viewBinder = ViewBinderCountry(requireActivity())
        listAdapter = adapter
    }

    private inner class ViewBinderCountry(private val context: Context) :
        SimpleCursorAdapter.ViewBinder {
        override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
            var res = false
            val cRec = CountrycountRecord.fromCursor(cursor)
            if (view.id == R.id.imFilterCountry) {
                if (cursor.getString(columnIndex) != null) {
                    val value = cursor.getString(columnIndex).lowercase(Locale.getDefault()) + "2x"
                    val id = requireActivity().resources.getDrawableIdentifier(
                        value,
                        requireActivity().packageName
                    )
                    (view as ImageView).setImageResource(id)
                    Timber.d("im ${cRec.id} ${cRec.country} ${cRec.count} ${cRec.show}")
                } else {
                    Timber.d("im ${cursor.getString(0)} xx ${cursor.getString(2)} ${cursor.getString(3)}")
                }
                res = true
            } else if (view.id == R.id.chkFilterCountry) {
                val chk = view as CheckBox
                chk.text = ""
                chk.tag = cRec.id // cursor.getLong(cursor.getColumnIndex(Countrycount._ID)));
                chk.isChecked = cursor.getInt(columnIndex) == 1
                chk.setOnClickListener { view1: View ->
                    val chk1 = view1 as CheckBox
                    val country = mxDatabase.countryDao().byId(view1.tag.toString().toLong())
                    if (country != null) {
                        country.show = if (chk1.isChecked) 1 else 0
                        mxDatabase.countryDao().update(country)
                    }
                    requireActivity().invalidateOptionsMenu()
                }
                res = true
            } else if (view.id == R.id.tvFilterCountryText) {
                val tv = view as TextView
                val loc = Locale.Builder().setRegion(cRec.country).build()
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
        Timber.d("CountryCount ${data.count}")
        adapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.swapCursor(null)
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        super.onListItemClick(listView, view, position, id)
        val country = mxDatabase.countryDao().byId(id)
        if (country != null) {
            country.show = if (country.show == 1) 0 else 1
            mxDatabase.countryDao().update(country)
        }
    }

    private fun handleMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings_filter_country) {
            val anz: Int = if (hided == 0) {
                mxDatabase.countryDao().updateShowByCountryCode(false)
            } else {
                mxDatabase.countryDao().updateShowByCountryCode(true)
            }
            Timber.d("update country %s", anz)
            item.icon = getIcon4SetAllCountry(requireActivity())
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings_filter_country).icon = getIcon4SetAllCountry(requireActivity())
        super.onPrepareOptionsMenu(menu)
    }

    private fun getIcon4SetAllCountry(context: Context): Drawable? {
        val allCountries = mxDatabase.countryDao().all
        hided = allCountries.filter { it.show == 0 }.size
        val all = allCountries.size
        val drawable: Drawable? = when (hided) {
            0 -> ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox)
            all -> ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox_empty)
            else -> ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox_grey)
        }
        return drawable
    }

    companion object {
        private var hided = 0
    }
}
