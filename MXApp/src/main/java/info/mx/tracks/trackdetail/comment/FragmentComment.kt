package info.mx.tracks.trackdetail.comment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import info.mx.tracks.BuildConfig
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.databinding.FragmentRecyclerListBinding
import info.mx.tracks.rest.DataManagerApp
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.sqlite.TracksRecord
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class FragmentComment : FragmentUpDown() {

    private val dataManagerApp: DataManagerApp by inject()

    private lateinit var adapter: CommentsAdapter

    private var localTrackId = 0L

    private var _binding: FragmentRecyclerListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

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

        adapter = CommentsAdapter(this.requireContext())

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

        val tracksRecord = TracksRecord.get(newId)

        addDisposable(mxDatabase.commentDao().loadByTrackId(tracksRecord.restId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ comments ->
                adapter.setData(comments.toMutableList())
            },
                { error ->
                    if (BuildConfig.DEBUG) {
                        Timber.e(error)
                        error.message?.let { showSnackbar(it) }
                    }
                }
            )
        )

        addDisposable(dataManagerApp.updateRatingsForTrack(tracksRecord.restId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
//                    adapter.setData(comments)
            },
                { error ->
                    if (BuildConfig.DEBUG) {
                        Timber.e(error)
                        error.message?.let { showSnackbar(it) }
                    }
                }
            )
        )
    }
}

