package info.hannes.mxadmin.picture

import android.database.Cursor
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import info.hannes.mechadminGen.sqlite.MxAdminDBContract
import java.util.*

/**
 * Adapter for stage image pager using FragmentStatePagerAdapter.
 *
 * Note: FragmentStatePagerAdapter is deprecated in favor of FragmentStateAdapter with ViewPager2.
 * This adapter continues to use the legacy API for compatibility with the existing ViewPager
 * implementation. Migration to ViewPager2 would require updating layouts and activities.
 */
@Suppress("DEPRECATION")
class AdapterImageStagePager(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val cachedFragments: MutableMap<Int, FragmentImageStage> = HashMap()
    private var cursor: Cursor? = null

    override fun getItem(i: Int): Fragment {
        cursor!!.moveToPosition(i)
        val imageClientId = cursor!!.getLong(cursor!!.getColumnIndex(MxAdminDBContract.PictureStage._ID))
        return FragmentImageStage.newInstance(imageClientId)
    }

    override fun getCount(): Int {
        return if (cursor == null) 0 else cursor!!.count
    }

    override fun getPageTitle(position: Int): CharSequence {
        cursor!!.move(position)
        return "" + position
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        cachedFragments[position] = fragment as FragmentImageStage
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
            fragmentImage.imageView!!.resetZoom()
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
