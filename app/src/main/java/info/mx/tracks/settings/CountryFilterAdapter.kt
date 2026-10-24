package info.mx.tracks.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import info.mx.tracks.R
import info.mx.tracks.room.entity.CountryCount
import info.mx.tracks.util.getDrawableIdentifier
import java.util.Locale

class CountryFilterAdapter(
    private val context: Context,
    private val onCountryToggle: (Long, Boolean) -> Unit,
    private val onInvalidateMenu: () -> Unit
) : RecyclerView.Adapter<CountryFilterAdapter.ViewHolder>() {

    private var countries: List<CountryCount> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryImage: ImageView = view.findViewById(R.id.imFilterCountry)
        val countryCheckbox: CheckBox = view.findViewById(R.id.chkFilterCountry)
        val countryText: TextView = view.findViewById(R.id.tvFilterCountryText)
        val countryCount: TextView = view.findViewById(R.id.tvFilterCountryAnz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val countryCount = countries[position]

        // Set country flag image
        val value = countryCount.country.lowercase(Locale.getDefault()) + "2x"
        val id = context.resources.getDrawableIdentifier(value, context.packageName)
        holder.countryImage.setImageResource(id)

        // Set country name
        val loc = Locale.Builder().setRegion(countryCount.country).build()
        holder.countryText.text = loc.displayCountry

        // Set checkbox
        holder.countryCheckbox.text = ""
        holder.countryCheckbox.tag = countryCount.id
        holder.countryCheckbox.isChecked = countryCount.show == 1
        holder.countryCheckbox.setOnClickListener { view ->
            val checkbox = view as CheckBox
            val id = view.tag as Long
            onCountryToggle(id, checkbox.isChecked)
            onInvalidateMenu()
        }

        // Set track count
        holder.countryCount.text = String.format(context.getString(R.string.country_anz), countryCount.count)

        // Handle item click to toggle checkbox
        holder.itemView.setOnClickListener {
            holder.countryCheckbox.isChecked = !holder.countryCheckbox.isChecked
            onCountryToggle(countryCount.id, holder.countryCheckbox.isChecked)
            onInvalidateMenu()
        }
    }

    override fun getItemCount(): Int = countries.size

    fun updateCountries(newCountries: List<CountryCount>) {
        val diffCallback = CountryDiffCallback(countries, newCountries)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        countries = newCountries
        diffResult.dispatchUpdatesTo(this)
    }

    private class CountryDiffCallback(
        private val oldList: List<CountryCount>,
        private val newList: List<CountryCount>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

