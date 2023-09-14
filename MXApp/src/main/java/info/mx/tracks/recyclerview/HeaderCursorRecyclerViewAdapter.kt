package info.mx.tracks.recyclerview

import android.database.Cursor
import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * [Gist](https://gist.github.com/tuanchauict/c6c1eda617523de224c5)
 */
abstract class HeaderCursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(cursor: Cursor?, sections: List<Section>?) :
    CursorRecyclerViewAdapter<VH>(cursor) {

    class Section(var itemCount: Int, var text: String)

    init {
        convertSectionList(sections)
    }

    private val sectionsIndexer: SparseArray<String> = SparseArray()
    private fun convertSectionList(sections: List<Section>?) {
        sectionsIndexer.clear()
        if (sections != null) {
            var count = 0
            for (section in sections) {
                sectionsIndexer.put(count, section.text)
                count += section.itemCount + 1
            }
        }
    }

    fun changeCursor(cursor: Cursor?, sections: List<Section>?) {
        super.changeCursor(cursor!!)
        convertSectionList(sections)
    }

    fun setSections(sections: List<Section>?) {
        convertSectionList(sections)
    }

    /**
     * If you have to custom this function, remember to avoid return the value of TYPE_HEADER (-1) for none header position.
     */
    override fun getItemViewType(position: Int): Int {
        return if (sectionsIndexer.indexOfKey(position) >= 0) {
            TYPE_HEADER
        } else {
            TYPE_NORMAL_ITEM
        }
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        if (viewHolder.itemViewType == TYPE_HEADER) {
            onBindSectionHeaderViewHolder(viewHolder, sectionsIndexer[position])
        } else {
            cursor!!.moveToPosition(position - countNumberSectionsBefore(position))
            onBindItemViewHolder(viewHolder, cursor)
        }
    }

    private fun countNumberSectionsBefore(position: Int): Int {
        var count = 0
        for (i in 0 until sectionsIndexer.size()) {
            if (position > sectionsIndexer.keyAt(i)) {
                count++
            }
        }
        return count
    }

    abstract fun onBindSectionHeaderViewHolder(headerHolder: VH, header: String?)
    abstract fun onBindItemViewHolder(itemHolder: VH, cursor: Cursor?)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return if (viewType == TYPE_HEADER) {
            onCreateSectionHeaderViewHolder(parent)
        } else {
            onCreateItemViewHolder(parent, viewType)
        }
    }

    abstract fun onCreateSectionHeaderViewHolder(parent: ViewGroup?): VH
    abstract fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): VH

    override fun onBindViewHolder(viewHolder: VH, cursor: Cursor?) {
        //do nothing
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        return if (count > 0) {
            count + sectionsIndexer.size()
        } else {
            0
        }
    }

    companion object {
        const val TYPE_HEADER = -1
        const val TYPE_NORMAL_ITEM = 0
    }

}
