package info.hannes.mxadmin.download

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.TrackstageBrother

class StepsBrotherAdapter : BaseAdapter() {

    private var steps = arrayOfNulls<Step>(3)

    init {
        steps[0] = Step().apply {
            name = "TrackstageBrother"
            count = SQuery.newQuery().count(TrackstageBrother.CONTENT_URI)
        }

        steps[1] = Step().apply {
            name = "Transferred"
            val countTransferred = SQuery.newQuery()
                .expr(TrackstageBrother.REST_ID, SQuery.Op.GT, 0)
                .count(TrackstageBrother.CONTENT_URI)
            count = countTransferred
        }

        steps[2] = Step().apply {
            name = "Count with XML Content"
            val countWithXMLContent = SQuery.newQuery()
                .expr(TrackstageBrother.CONTENT_DETAIL_XML, SQuery.Op.NEQ, "")
                .count(TrackstageBrother.CONTENT_URI)
            count = countWithXMLContent
        }
    }

    override fun getCount() = steps.size

    override fun getItem(position: Int) = steps[position]!!

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val viewText1: TextView
        val viewText2: TextView
        val viewHolder = convertView.tag as ViewHolderBrotherSteps
        viewText1 = viewHolder.viewText1
        viewText2 = viewHolder.viewText2
        viewText1.text = getItem(position).name
        viewText2.text = "${getItem(position).count}"
        return convertView
    }
}
