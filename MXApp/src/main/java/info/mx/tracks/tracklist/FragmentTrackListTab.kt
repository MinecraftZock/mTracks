package info.mx.tracks.tracklist

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.databinding.ContentActivityTracksTabsBinding
import info.mx.tracks.service.RecalculateDistance
import info.mx.tracks.sqlite.MxInfoDBContract.Tracksges
import timber.log.Timber

/**
 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
 */
class FragmentTrackListTab : FragmentBase() {
    private var callbacks: CallbacksTabs? = null

    private val registerDistanceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (binding.viewPager.adapter?.itemCount != getTabCount()) {
                Timber.d("Refresh tabCount ${getTabCount()}")
                binding.viewPager.adapter?.notifyItemChanged(getTabCount())
            } else {
                Timber.d("Refresh no need tabCount ${getTabCount()}")
            }
        }
    }

    private var _binding: ContentActivityTracksTabsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ContentActivityTracksTabsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.progressimportText.visibility = View.GONE
        binding.progressImport.visibility = View.GONE

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                val bundle = Bundle()

                return when (position) {
                    0 -> FragmentTrackList().apply {
                        bundle.putString(FragmentUpDown.ORDER, Tracksges.TRACKNAME)
                        arguments = bundle
                    }
                    1 -> FragmentTrackList().apply {
                        bundle.putString(FragmentUpDown.ORDER, FragmentTrackList.IS_FAVORITE)

                        arguments = bundle
                    }
                    2 -> if (MxCoreApplication.isAdmin)
                        FragmentTrackList().apply {
                            bundle.putString(FragmentUpDown.ORDER, Tracksges.APPROVED)
                            bundle.putBoolean(FragmentTrackList.ONLY_FOREIGN, false)
                            arguments = bundle
                        }
                    else
                        getDistanceFragment()
                    3 -> FragmentTrackList().apply {
                        bundle.putString(FragmentUpDown.ORDER, Tracksges.APPROVED)
                        bundle.putBoolean(FragmentTrackList.ONLY_FOREIGN, true)
                        arguments = bundle
                    }
                    else -> getDistanceFragment() // optional distance
                }
            }

            override fun getItemCount(): Int {
                return getTabCount()
            }
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.by_name)
                1 -> "" // ""♥" //ContextCompat.getDrawable(requireActivity(), android.R.drawable.star_off)
                2 -> if (MxCoreApplication.isAdmin)
                    "Stage+"
                else
                    getString(R.string.by_distance)
                3 -> "Stage"
                else -> getString(R.string.by_distance)
            }

            if (position == 1)
                tab.setIcon(android.R.drawable.star_off)
        }.attach()

//        tabsAdapter.setOnCallbacksTabsChange(object : TabsAdapter.CallbacksTabsChange {
//            override fun onTabPageSelected(id: Long) {
//                callbacks?.onTabPageSelected(id)
//            }
//        })

//        if (permissionHelper.hasLocationPermission()) {
//            addDistanceFragment()
//        } else {
//            // remove the last if it is distance tab
//            if (isDistanceFragmentAvailable) {
//                binding.tabhost.tabWidget.removeView(binding.tabhost.tabWidget.getChildTabViewAt(binding.tabhost.tabWidget.tabCount - 1))
//            }
//        }
        return view
    }

    private fun getTabCount() = when (MxCoreApplication.isAdmin) {
        true -> 4
        false -> 2
    } + if (permissionHelper.hasLocationPermission()) 1 else 0

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Activities containing this fragment must implement its callbacks.
        check(context is CallbacksTabs) { "Activity $context must implement fragment's CallbacksTabs" }

        callbacks = context
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()

        val filterIUpdate = IntentFilter(RecalculateDistance.DISTANCE_NEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(registerDistanceReceiver, filterIUpdate, Context.RECEIVER_NOT_EXPORTED)
        } else
            requireActivity().registerReceiver(registerDistanceReceiver, filterIUpdate)
    }

    override fun onPause() {
        try {
            requireActivity().unregisterReceiver(registerDistanceReceiver)
        } catch (ignored: Exception) {
        }

        super.onPause()
    }

    private fun getDistanceFragment(): FragmentTrackList {
        val bundle = Bundle()
        return FragmentTrackList().apply {
            bundle.putString(FragmentUpDown.ORDER, Tracksges.DISTANCE2LOCATION)
            arguments = bundle
        }
    }

    companion object {
        const val TAG = "FragmentTrackListTab"
    }

}
