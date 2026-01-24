package info.mx.tracks.trackdetail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class AdapterFragmentsTab(context: Context, fragmentActivity: FragmentActivity, arguments: Bundle) : BaseAdapterFragmentTab(context, fragmentActivity, arguments) {
    override fun addAdditionalTabs(tabs: MutableList<TabFragmentInfo>) = Unit
}
