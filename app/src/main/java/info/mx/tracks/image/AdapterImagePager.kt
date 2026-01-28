package info.mx.tracks.image

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import info.mx.core_generated.sqlite.MxInfoDBContract
import java.util.*

/**
 * Adapter for image pager using modern ViewPager2 with FragmentStateAdapter.
 *
 * Migrated from deprecated FragmentStatePagerAdapter to FragmentStateAdapter for
 * better performance and modern Android architecture.
 */
class AdapterImagePager(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val cachedFragments: MutableMap<Long, FragmentImage> = HashMap()
    private var imageIds: List<Long> = emptyList()

    override fun createFragment(position: Int): Fragment {
        val imageRestId = imageIds[position]
        val fragment = FragmentImage.newInstance(imageRestId)
        cachedFragments[imageRestId] = fragment
        return fragment
    }

    override fun getItemCount(): Int = imageIds.size

    override fun getItemId(position: Int): Long {
        return imageIds[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return imageIds.contains(itemId)
    }

    /**
     * Get page title for the given position
     */
    fun getPageTitle(position: Int): CharSequence {
        return position.toString()
    }

    /**
     * Reset the image zoom to default value for each cached fragment
     */
    fun resetZoom() {
        for (fragmentImage in cachedFragments.values) {
            fragmentImage.binding.imageFullSize.resetZoom()
        }
    }

    /**
     * Set Cursor and update the adapter with new data
     *
     * @param cursor Cursor containing image data
     * @return return TRUE if no cursor was set previously, necessary to prevent flickering
     */
    @SuppressLint("Range")
    fun setCursor(cursor: Cursor?): Boolean {
        val initial = imageIds.isEmpty()

        val newImageIds = mutableListOf<Long>()
        cursor?.let {
            if (!it.isClosed && it.moveToFirst()) {
                do {
                    val imageRestId = it.getLong(it.getColumnIndex(MxInfoDBContract.Pictures.REST_ID))
                    newImageIds.add(imageRestId)
                } while (it.moveToNext())
            }
        }

        // Use DiffUtil for efficient updates
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = imageIds.size
            override fun getNewListSize(): Int = newImageIds.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return imageIds[oldItemPosition] == newImageIds[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return imageIds[oldItemPosition] == newImageIds[newItemPosition]
            }
        })

        imageIds = newImageIds
        diffResult.dispatchUpdatesTo(this)

        return initial
    }

    /**
     * Clear cached fragments when they're no longer needed
     */
    fun clearCache() {
        cachedFragments.clear()
    }

}
