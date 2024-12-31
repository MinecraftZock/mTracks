package info.mx.tracks.map

import android.os.Bundle
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.stage.FragmentStageNewDetail
import info.mx.tracks.trackdetail.FragmentTrackDetail

class FragmentMap : BaseFragmentMap() {

    override fun openStageInSlider(id: Long) {
        super.openStageInSlider(id)

        val arguments = Bundle()
        arguments.putLong(FragmentUpDown.RECORD_ID_LOCAL, id)
        arguments.putString(FragmentUpDown.CONTENT_URI, MxInfoDBContract.Trackstage.CONTENT_URI.toString())
        arguments.putBoolean(FragmentTrackDetail.IN_SLIDER, true)
        val fragmentStage = FragmentStageNewDetail()
        fragmentStage.arguments = arguments

        // Replace in the fragment_container_map view with this fragment,
        // add the transaction to the back stack so the user can navigate back
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_map, fragmentStage)
            .addToBackStack(null)
            .commit()

        slidingDrawer!!.scrollableView = fragmentStage.binding.scrollContent
    }
}
