package info.hannes.mxadmin.adapter

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mxadmin.adapter.ChangeAdapter.ViewHolderArchive
import info.hannes.mxadmin.service.DataManagerAdmin
import info.hannes.retrofit.service.model.Tracksarchiv
import info.mx.comlib.retrofit.service.data.Data
import info.mx.comlib.retrofit.service.data.DataSingleObserver
import info.mx.tracks.R
import info.mx.tracks.common.SecHelper
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.core_generated.sqlite.TracksRecord
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChangeAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolderArchive>(), KoinComponent {
    private val listTracksArchive: MutableList<Tracksarchiv> = ArrayList()
    private val sdf = SimpleDateFormat("yyyy.MM.dd  HH:mm:ss", Locale.getDefault())

    private val dataManagerAdmin: DataManagerAdmin by inject()

    fun requestData(trackRestId: Long?) {
        listTracksArchive.clear()
        notifyDataSetChanged()
        dataManagerAdmin.getTracksArchive(trackRestId!!, false)
            .subscribe(object : DataSingleObserver<List<Tracksarchiv>>(context) {
                override fun onSuccess(listData: Data<List<Tracksarchiv>>) {
                    if (listData.data.isNotEmpty()) {
                        listTracksArchive.addAll(listData.data)
                        notifyDataSetChanged()
                    }
                }

                override fun onNext(data: List<Tracksarchiv>, source: Data.Source) = Unit
            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderArchive {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_archiv, parent, false)
        // set the view's size, margins, padding's and layout parameters
        return ViewHolderArchive(view)
    }

    override fun onBindViewHolder(holder: ViewHolderArchive, position: Int) {
        val tracksarchiv = listTracksArchive[position]
        val track = SQuery.newQuery()
            .expr(MxInfoDBContract.Tracks.REST_ID, SQuery.Op.EQ, tracksarchiv.getId()!!)
            .selectFirst<TracksRecord>(MxInfoDBContract.Tracks.CONTENT_URI)
        holder.textWhen.text =
            sdf.format(Date(java.lang.Long.valueOf(tracksarchiv.getArchDate()!!) * 1000))
        holder.textLeft.text = Html.fromHtml(getFields(tracksarchiv, track), Html.FROM_HTML_MODE_LEGACY)
    }

    private fun getFields(obj: Any, track: TracksRecord): String {
        val res = StringBuilder()
        val methodsTrack = track.javaClass.declaredMethods
        for (method in obj.javaClass.declaredMethods) {
            if (method.name.startsWith("get") &&
                method.name != "getId" &&
                !method.name.startsWith("getArch") &&
                !method.name.startsWith("getChanged") &&
                !method.name.startsWith("getLatitude") &&
                !method.name.startsWith("getLongitude")
            ) {
                try {
                    if (method.invoke(obj) != null) {
                        val methodTrack = getMethod(method.name, methodsTrack)
                        var color: Int
                        var valueOriginal: Any = ""
                        if (methodTrack == null) {
                            color = Color.RED
                            res.append(getColoredSpanned(method.name + "=" + method.invoke(obj), color)).append(valueOriginal).append("<br>")
                        } else {
                            val trackValue = methodTrack.invoke(track)
                            if (method.name.startsWith("getPhone") || method.name.startsWith("getUrl")) {
                                val trackValueS = SecHelper.decryptB64(trackValue as String)
                                if (trackValueS != method.invoke(obj)) {
                                    valueOriginal = " ($trackValueS)"
                                    color = Color.WHITE
                                    res.append(getColoredSpanned(method.name + "=" + method.invoke(obj), color))
                                        .append(getColoredSpanned(valueOriginal, Color.GRAY))
                                        .append("<br>")
                                }
                            } else if (methodTrack.invoke(track) != null &&
                                methodTrack.invoke(track) == method.invoke(obj) &&
                                !method.name.startsWith("getApproved")
                            ) {
                                // nothing
                            } else {
                                valueOriginal = " (" + methodTrack.invoke(track) + ")"
                                color = Color.WHITE
                                res.append(getColoredSpanned(method.name + "=" + method.invoke(obj), color))
                                    .append(getColoredSpanned(valueOriginal, Color.GRAY))
                                    .append("<br>")
                            }
                        }
                    }
                } catch (_: IllegalAccessException) {
                    res.append(method.name).append("=IllegalAccess").append("<br>")
                } catch (_: InvocationTargetException) {
                    res.append(method.name).append("=InvocationTarget").append("<br>")
                }
            }
        }
        return res.toString()
    }

    private fun getColoredSpanned(text: String, intColor: Int): String {
        val hexColor = String.format("#%06X", 0xFFFFFF and intColor)
        return "<font color=$hexColor>$text</font>"
    }

    private fun getMethod(name: String, methodsTrack: Array<Method>): Method? {
        for (method in methodsTrack) {
            if (method.name == name) {
                return method
            }
        }
        return null
    }

    override fun getItemCount(): Int {
        return listTracksArchive.size
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolderArchive(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val textLeft: TextView = viewItem.findViewById(R.id.textLeft)
        val textWhen: TextView = viewItem.findViewById(R.id.textWhen)
    }

}
