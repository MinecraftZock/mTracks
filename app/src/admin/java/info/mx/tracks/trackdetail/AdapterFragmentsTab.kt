package info.mx.tracks.trackdetail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import info.mx.tracks.MxApplication

import info.mx.tracks.R

class AdapterFragmentsTab(context: Context, fm: FragmentManager, arguments: Bundle) : BaseAdapterFragmentTab(
    context,
    fm,
    arguments
) {
    override fun addAdditionalTabs(tabs: MutableList<TabFragmentInfo>) {
        if (!MxApplication.isGoogleTests) {
            tabs.add(TabFragmentInfo(FragmentStage::class.java, R.string.stage, R.drawable.actionbar_checkbox_question))
            tabs.add(TabFragmentInfo(FragmentChanges::class.java, R.string.archiv, R.drawable.actionbar_checkbox_question))
        }
    }
}
