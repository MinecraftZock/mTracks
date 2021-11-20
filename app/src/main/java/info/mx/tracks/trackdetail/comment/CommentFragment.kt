package info.mx.tracks.trackdetail.comment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import info.mx.comlib.retrofit.service.data.Data
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.databinding.FragmentRecyclerListBinding
import info.mx.tracks.room.MxDatabase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommentFragment : FragmentUpDown() {

    private lateinit var adapter: CommentAdapter

    private var localTrackId = 0L

    private var _binding: FragmentRecyclerListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val commentViewModel: CommentViewModel by viewModel()

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

        adapter = CommentAdapter(this.requireContext())

        binding.listRecyclerEntries.adapter = adapter

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
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(requireActivity())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun fillMask(localId: Long) {
        requireArguments().putLong(RECORD_ID_LOCAL, localId)

        val tracksRecord = TracksRecord.get(localId)

        commentViewModel.allCommentsByTrackId(tracksRecord.restId).observe(viewLifecycleOwner) { comments ->
            adapter.setData(Data.db(comments))
        }

//        commentViewModel.getNewRemoteData(tracksRecord.restId)
    }
}
