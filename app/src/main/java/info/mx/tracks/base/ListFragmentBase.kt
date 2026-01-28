package info.mx.tracks.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.ListFragment
import info.mx.core.MxCoreApplication

open class ListFragmentBase : ListFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MxCoreApplication.trackActivity(javaClass.simpleName)
    }

}
