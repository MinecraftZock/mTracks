package info.hannes.mxadmin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.comlib.retrofit.service.data.Data
import info.mx.comlib.retrofit.service.data.DataSingleObserver
import info.mx.comlib.retrofit.service.data.RxNetworkProblem
import info.mx.tracks.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class NetworkProblemAdapter(private val context: Context) : RecyclerView.Adapter<NetworkProblemAdapter.ViewHolder>(), KoinComponent {
    private val listNP: MutableList<RxNetworkProblem> = ArrayList<RxNetworkProblem>()
    private val sdf = SimpleDateFormat("yyyy.MM.dd  HH:mm:ss", Locale.getDefault())

    private val dataManagerAdmin: DataManagerAdmin by inject()

    init {
        requestData(System.currentTimeMillis())
    }

    private fun requestData(time: Long) {
        dataManagerAdmin.getNetworkProblems(time / 1000, true, BLOCK_SIZE)
            .subscribe(object : DataSingleObserver<List<RxNetworkProblem>>(context) {
                override fun onSuccess(listData: Data<List<RxNetworkProblem>>) {
                    if (listData.data.isNotEmpty()) {
                        listNP.addAll(listData.data)
                        notifyDataSetChanged()
                    }
                }

                override fun onNext(data: List<RxNetworkProblem>, source: Data.Source) {}
            })
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_network_problem, parent, false)
        // set the view's size, margins, padding's and layout parameters
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val networkProblem: RxNetworkProblem = listNP[position]
        holder.textTime.text = sdf.format(
            Date(java.lang.Long.valueOf(networkProblem.getChanged().toLong()) * 1000)
        )
        holder.textCount.text = position.toString() + "/" + networkProblem.getTracks()
        holder.textReason.setText(networkProblem.getReason())
        if (position == listNP.size - 1) {
            requestData(java.lang.Long.valueOf(listNP[listNP.size - 1].getChanged().toLong()) * 1000)
        }
    }

    // Return the size of your dataSetNames (invoked by the layout manager)
    override fun getItemCount(): Int {
        return listNP.size
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val textTime: TextView = viewItem.findViewById(R.id.textNPtime)
        val textCount: TextView = viewItem.findViewById(R.id.textNPcount)
        val textReason: TextView = viewItem.findViewById(R.id.textNPreason)
    }

    companion object {
        private const val BLOCK_SIZE = 30
    }

}
