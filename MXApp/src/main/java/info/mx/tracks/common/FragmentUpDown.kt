package info.mx.tracks.common

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op

import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import info.mx.tracks.prefs.MxPreferences
import timber.log.Timber

abstract class FragmentUpDown : FragmentBase() {
    private var contentUri: String? = null
    var recordId: Long = 0
        protected set
    protected var prevId = 0L
    protected var nextId = 0L

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        MxPreferences.getInstance().edit().putRestoreID(recordId).putRestoreContentUri(contentUri).commit()
        savedInstanceState.putLong(RECORD_ID_LOCAL, recordId)
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
            recordId = requireArguments().getLong(RECORD_ID_LOCAL)
        } else {
            throw IllegalArgumentException("RECORD_ID_LOCAL missing ")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            Timber.d(savedInstanceState.toString())
            recordId = savedInstanceState.getLong(RECORD_ID_LOCAL)
            contentUri = savedInstanceState.getString(CONTENT_URI)
        }
    }

    protected open fun fillNextPrevId(newId: Long) {
        nextId = SQuery.newQuery().expr("_id", Op.GT, newId).firstLong(Uri.parse(contentUri), "_id", "_id asc")
        prevId = SQuery.newQuery().expr("_id", Op.LT, newId).firstLong(Uri.parse(contentUri), "_id", "_id desc")
    }

    fun moveUp() {
        if (prevId > 0) {
            val x = prevId
            requireArguments().putInt(CURSOR_POSITION, requireArguments().getInt(CURSOR_POSITION) - 1)
            fillMask(prevId)
            recordId = x
            requireArguments().putLong(RECORD_ID_LOCAL, recordId)
            MxPreferences.getInstance().edit().putRestoreID(recordId).commit()
        } else {
            Timber.w(getString(R.string.empty))
            Toast.makeText(activity, getString(R.string.empty), Toast.LENGTH_SHORT).show()
        }
    }

    fun moveDown() {
        if (nextId > 0) {
            val x = nextId
            requireArguments().putInt(CURSOR_POSITION, requireArguments().getInt(CURSOR_POSITION) + 1)
            fillMask(nextId)
            recordId = x
            requireArguments().putLong(RECORD_ID_LOCAL, recordId)
            MxPreferences.getInstance().edit().putRestoreID(recordId).commit()
        } else {
            Timber.w(getString(R.string.empty))
            Toast.makeText(activity, getString(R.string.empty), Toast.LENGTH_SHORT).show()
        }
    }

    abstract fun fillMask(newId: Long)

    companion object {

        const val RECORD_ID_LOCAL = "recordIdLocal"
        const val CONTENT_URI = "contentUri"
        const val CURSOR_POSITION = "cursor_pos"
        const val FILTER = "filter"
        const val ORDER = "sort"
    }

}
