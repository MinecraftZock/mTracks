package info.mx.tracks.common

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import info.mx.core_generated.prefs.MxPreferences
import timber.log.Timber

abstract class FragmentUpDown : FragmentBase() {
    private var contentUri: String? = null
    var recordLocalId: Long = 0
        protected set
    protected var prevLocalId = 0L
    protected var nextLocalId = 0L

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        MxPreferences.getInstance().edit().putRestoreID(recordLocalId).putRestoreContentUri(contentUri).commit()
        savedInstanceState.putLong(RECORD_ID_LOCAL, recordLocalId)
        savedInstanceState.putString(CONTENT_URI, contentUri)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contentUri = arguments?.getString(CONTENT_URI)
        if (contentUri == null) {
            throw IllegalArgumentException("CONTENT_URI missing " + arguments?.toString())
        }
        if (requireArguments().containsKey(RECORD_ID_LOCAL)) {
            recordLocalId = requireArguments().getLong(RECORD_ID_LOCAL)
        } else {
            throw IllegalArgumentException("RECORD_ID_LOCAL missing ")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            Timber.d(savedInstanceState.toString())
            recordLocalId = savedInstanceState.getLong(RECORD_ID_LOCAL)
            contentUri = savedInstanceState.getString(CONTENT_URI)
        }
    }

    protected open fun fillNextPrevId(localId: Long) {
        nextLocalId = SQuery.newQuery().expr("_id", Op.GT, localId).firstLong(contentUri?.toUri(), "_id", "_id asc")
        prevLocalId = SQuery.newQuery().expr("_id", Op.LT, localId).firstLong(contentUri?.toUri(), "_id", "_id desc")
    }

    fun moveUp() {
        if (prevLocalId > 0) {
            val x = prevLocalId
            requireArguments().putInt(CURSOR_POSITION, requireArguments().getInt(CURSOR_POSITION) - 1)
            fillMask(prevLocalId)
            recordLocalId = x
            requireArguments().putLong(RECORD_ID_LOCAL, recordLocalId)
            MxPreferences.getInstance().edit().putRestoreID(recordLocalId).commit()
        } else {
            Timber.w(getString(R.string.empty))
            Toast.makeText(activity, getString(R.string.empty), Toast.LENGTH_SHORT).show()
        }
    }

    fun moveDown() {
        if (nextLocalId > 0) {
            val x = nextLocalId
            requireArguments().putInt(CURSOR_POSITION, requireArguments().getInt(CURSOR_POSITION) + 1)
            fillMask(nextLocalId)
            recordLocalId = x
            requireArguments().putLong(RECORD_ID_LOCAL, recordLocalId)
            MxPreferences.getInstance().edit().putRestoreID(recordLocalId).commit()
        } else {
            Timber.w(getString(R.string.empty))
            Toast.makeText(activity, getString(R.string.empty), Toast.LENGTH_SHORT).show()
        }
    }

    abstract fun fillMask(localId: Long)

    companion object {

        const val RECORD_ID_LOCAL = "recordIdLocal"
        const val CONTENT_URI = "contentUri"
        const val CURSOR_POSITION = "cursor_pos"
        const val FILTER = "filter"
        const val ORDER = "sort"
    }

}
