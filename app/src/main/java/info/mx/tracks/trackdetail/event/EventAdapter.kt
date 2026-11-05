package info.mx.tracks.trackdetail.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import info.mx.comlib.retrofit.service.data.Data
import info.mx.tracks.databinding.ItemEventBinding
import info.mx.tracks.room.entity.Event
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class EventAdapter(context: Context) : RecyclerView.Adapter<EventAdapter.PostViewHolder>() {

    private var data: MutableList<Event>? = null
    private val layoutInflater: LayoutInflater

    init {
        this.data = ArrayList()
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(data!![position])
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    internal fun setData(newData: Data<List<Event>>) {
        Timber.d("setData ${newData.source.name} ${newData.data.size}")
        setData(newData.data.toMutableList())
    }

    private fun setData(newData: MutableList<Event>) {
        if (data != null) {
            val eventDiffCallback = EventDiffCallback(data!!, newData)
            val diffResult = DiffUtil.calculateDiff(eventDiffCallback)

            data!!.clear()
            data!!.addAll(newData)
            diffResult.dispatchUpdatesTo(this)
        } else {
            // first initialization
            data = newData
        }
        notifyDataSetChanged()
    }

    internal class PostViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        fun bind(event: Event) {
            binding.textDatum.text = sdf.format(Date(event.eventDate * 1000))
            binding.textSerie.text = event.seriesRestId.toString() // TODO get series name
            binding.textSerie.visibility = if (event.seriesRestId == 0L) View.GONE else View.VISIBLE
            binding.textKommentar.text = event.comment
        }
    }

    internal class EventDiffCallback(private val oldEvent: List<Event>, private val newEvent: List<Event>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldEvent.size
        }

        override fun getNewListSize(): Int {
            return newEvent.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldEvent[oldItemPosition].id == newEvent[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldEvent[oldItemPosition] == newEvent[newItemPosition]
        }
    }
}
