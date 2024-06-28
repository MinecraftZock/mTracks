package info.mx.tracks.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EmptyRecyclerView : RecyclerView {
    private var emptyView: View? = null

    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            initEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            initEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            initEmptyView()
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private fun initEmptyView() {
        if (emptyView != null) {
            emptyView!!.visibility = if (adapter == null || adapter!!.itemCount == 0) View.VISIBLE else View.GONE
            this@EmptyRecyclerView.visibility = if (adapter == null || adapter!!.itemCount == 0) View.GONE else View.VISIBLE
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        super.setAdapter(adapter)

        oldAdapter?.unregisterAdapterDataObserver(observer)

        adapter?.registerAdapterDataObserver(observer)
    }

    fun setEmptyView(view: View) {
        this.emptyView = view
        initEmptyView()
    }
}