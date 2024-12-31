package info.mx.tracks.trackdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mxadmin.adapter.ChangeAdapter
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TracksRecord

class FragmentChanges : FragmentUpDown() {

    private var changeAdapter: ChangeAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.content_activity_network_problems, null)

        val changesListView = view.findViewById<RecyclerView>(R.id.listNetworkProblems)

        changeAdapter = ChangeAdapter(requireContext())

        val mLayoutManager = LinearLayoutManager(context)
        changesListView.layoutManager = mLayoutManager
        changesListView.adapter = changeAdapter

        recordLocalId = 0
        arguments?.let {
            if (it.containsKey(RECORD_ID_LOCAL)) {
                recordLocalId = it.getLong(RECORD_ID_LOCAL)
                fillMask(recordLocalId)
            }
        }

        return view
    }

    override fun fillMask(localId: Long) {
        arguments?.let {
            it.putLong(RECORD_ID_LOCAL, localId)

            val track = SQuery.newQuery()
                .expr(MxInfoDBContract.Tracks._ID, SQuery.Op.EQ, localId).selectFirst<TracksRecord>(MxInfoDBContract.Tracks.CONTENT_URI)

            changeAdapter!!.requestData(track.restId)
        }
    }

}
