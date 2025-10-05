package info.mx.tracks.trackdetail.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import info.mx.comlib.retrofit.service.data.Data
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.databinding.FragmentRecyclerListBinding
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.trackdetail.detail.TrackDetailViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EventFragment : FragmentUpDown() {

    private lateinit var adapter: EventAdapter

    private var localTrackId = 0L

    private var _binding: FragmentRecyclerListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModel()
    private val trackDetailViewModel: TrackDetailViewModel by viewModel()

    val mxDatabase: MxDatabase by inject()

    @SuppressLint("HardwareIds")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentRecyclerListBinding.inflate(inflater, container, false)
        val view = binding.root
        // keyboard hide
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val layoutManager = LinearLayoutManager(context)
        binding.listRecyclerEntries.layoutManager = layoutManager
        binding.listRecyclerEntries.setEmptyView(binding.txtNoEntry)
        binding.listRecyclerEntries.isLongClickable = true

        adapter = EventAdapter(this.requireContext())

        binding.listRecyclerEntries.adapter = adapter

        localTrackId = requireArguments().getLong(RECORD_ID_LOCAL)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        fillMask(localTrackId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this activity, the Up button is shown.
                // Use NavUtils to allow users to navigate up one level in the application structure.
                // For more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(requireActivity())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun fillMask(newId: Long) {
        requireArguments().putLong(RECORD_ID_LOCAL, newId)

        trackDetailViewModel.getTrackById(newId).observe(viewLifecycleOwner) { track ->

            eventViewModel.allEvents(track.restId).observe(viewLifecycleOwner) { events ->
                adapter.setData(Data.db(events))
            }

            eventViewModel.getNewRemoteData(track.restId)
        }
    }
}
