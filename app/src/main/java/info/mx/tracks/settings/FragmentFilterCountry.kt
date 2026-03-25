package info.mx.tracks.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.mx.tracks.R
import timber.log.Timber

class FragmentFilterCountry : Fragment() {

    private val viewModel: CountryFilterViewModel by viewModels()
    private lateinit var adapter: CountryFilterAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter_country, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCountries)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CountryFilterAdapter(
            requireContext(),
            onCountryToggle = { id, show ->
                viewModel.updateCountryShow(id, show)
            },
            onInvalidateMenu = {
                requireActivity().invalidateOptionsMenu()
            }
        )

        recyclerView.adapter = adapter

        // Observe country data
        viewModel.allCountryCount.observe(viewLifecycleOwner) { countries ->
            Timber.d("CountryCount ${countries.size}")
            adapter.updateCountries(countries)
        }

        // Setup menu using MenuProvider
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.activity_filter_country, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return handleMenuItemSelected(menuItem)
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.action_settings_filter_country).icon = getIcon4SetAllCountry(requireActivity())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun handleMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings_filter_country) {
            val allCountries = viewModel.getAllCountries()
            val hided = allCountries.filter { it.show == 0 }.size

            val show = hided != 0
            viewModel.updateAllCountriesShow(show)

            Timber.d("update all countries show=$show")
            item.icon = getIcon4SetAllCountry(requireActivity())
        }
        return true
    }

    private fun getIcon4SetAllCountry(context: Context): Drawable? {
        val allCountries = viewModel.getAllCountries()
        val hided = allCountries.filter { it.show == 0 }.size
        val all = allCountries.size
        val drawable: Drawable? = when (hided) {
            0 -> ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox)
            all -> ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox_empty)
            else -> ContextCompat.getDrawable(context, R.drawable.actionbar_checkbox_grey)
        }
        return drawable
    }
}
