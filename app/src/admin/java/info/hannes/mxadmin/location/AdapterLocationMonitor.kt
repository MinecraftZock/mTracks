package info.hannes.mxadmin.location

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import info.mx.tracks.R
import info.mx.tracks.room.CapturedLatLng
import info.mx.tracks.room.MxDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date

class AdapterLocationMonitor(private val context: Context) : BaseAdapter(), KoinComponent {

    private var locationList: List<CapturedLatLng>? = null

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("MM.dd HH:mm:ss")

    val mxDatabase: MxDatabase by inject()

    fun setLocations(locations: List<CapturedLatLng>) {
        locationList = locations
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (locationList == null) 0 else locationList!!.size
    }

    override fun getItem(position: Int): CapturedLatLng {
        return locationList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("ViewHolder")
        val rowView = inflater.inflate(R.layout.item_capture_latlon, parent, false)
        val text1 = rowView.findViewById<TextView>(R.id.text1_l)
        val text2 = rowView.findViewById<TextView>(R.id.text2_l)
        val text1r = rowView.findViewById<TextView>(R.id.text1_r)
        val text2r = rowView.findViewById<TextView>(R.id.text2_r)

        val (_, _, _, time, distToNearest, distanceToLast, action, extra, trackName) = getItem(position)
        text1.text = sdf.format(Date(time)) + " " + extra
        text2.text = "Action: $action $trackName"
        val dist2Last = (distanceToLast / 1000.0).toString() + " km dist Last"
        val dist2Next = (distToNearest / 1000.0).toString() + " km dist Next"
        text1r.text = dist2Next
        text2r.text = dist2Last

        rowView.tag = getItem(position)
        rowView.setOnLongClickListener { view ->
            val capturedLatLng = view.tag as CapturedLatLng
            mxDatabase.capturedLatLngDao().delete(capturedLatLng)
            false
        }
        return rowView
    }
}
