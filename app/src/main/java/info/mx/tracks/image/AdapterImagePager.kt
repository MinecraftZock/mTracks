package info.mx.tracks.image

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import info.mx.tracks.sqlite.MxInfoDBContract
import java.util.*

class AdapterImagePager(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val cachedFragments: MutableMap<Int, FragmentImage> = HashMap()
    private var cursor: Cursor? = null

    @SuppressLint("Range")
    override fun getItem(i: Int): Fragment {
        var imageRestId: Long = 0
        cursor?.let {
            if (!it.isClosed) {
                it.moveToPosition(i)
                imageRestId = it.getLong(it.getColumnIndex(MxInfoDBContract.Pictures.REST_ID))
            }
        }

        return FragmentImage.newInstance(imageRestId)
    }

    override fun getCount(): Int {
        return if (cursor == null)
            0
        else
            cursor!!.count
    }

    override fun getPageTitle(position: Int): CharSequence {
        cursor?.move(position)
        return "" + position
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        cachedFragments[position] = fragment as FragmentImage
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        cachedFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    /**
     * Reset the image zoom to default value for each CachedFragments
     */
    fun resetZoom() {
        for (fragmentImage in cachedFragments.values) {
            fragmentImage.binding.imageFullSize.resetZoom()
        }
    }

    /**
     * Set Cursor
     *
     * @param cursor Cursor
     * @return return TRUE if no cursor was set previous, necessary to prevent flickering
     */
    fun setCursor(cursor: Cursor): Boolean {
        val initial = this.cursor == null
        this.cursor = cursor
        notifyDataSetChanged()
        return initial
    }

}
