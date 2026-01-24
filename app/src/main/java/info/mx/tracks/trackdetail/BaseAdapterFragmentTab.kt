package info.mx.tracks.trackdetail

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.trackdetail.comment.FragmentComment
import info.mx.tracks.trackdetail.event.FragmentEvent
import timber.log.Timber
import java.util.Locale

/**
 * Base adapter for track detail tabs using modern ViewPager2 with FragmentStateAdapter.
 * Migrated from deprecated FragmentPagerAdapter.
 */
abstract class BaseAdapterFragmentTab internal constructor(
    private val context: Context,
    fragmentActivity: FragmentActivity,
    private val fragmentArguments: Bundle
) : FragmentStateAdapter(fragmentActivity) {
    private val tabInfoList: List<TabFragmentInfo>
    private val fragments: MutableList<FragmentUpDown> = arrayListOf()

    init {
        tabInfoList = tabsInfo
        tabInfoList.forEach {
            fragments.add(getFragment(it.fragmentClass)!!)
        }
    }

    private val tabsInfo: List<TabFragmentInfo>
        get() {
            val tabs: MutableList<TabFragmentInfo> =
                ArrayList()
            tabs.add(
                TabFragmentInfo(
                    FragmentComment::class.java,
                    R.string.title_activity_comment,
                    R.drawable.ic_comment_white_24px
                )
            )
            tabs.add(
                TabFragmentInfo(
                    FragmentTrackDetail::class.java,
                    R.string.info,
                    R.drawable.ic_info
                )
            )
            tabs.add(
                TabFragmentInfo(
                    FragmentEvent::class.java,
                    R.string.events,
                    R.drawable.ic_event_note_white_24px
                )
            )
            addAdditionalTabs(tabs)
            return tabs
        }

    abstract fun addAdditionalTabs(tabs: MutableList<TabFragmentInfo>)

    private fun <T : FragmentUpDown?> getFragment(fragmentClass: Class<T>): T? {
        var fragmentUpDown: T? = null
        try {
            fragmentUpDown = fragmentClass.getDeclaredConstructor().newInstance()
            fragmentUpDown!!.arguments = fragmentArguments
        } catch (e: InstantiationException) {
            Timber.e(e)
        } catch (e: IllegalAccessException) {
            Timber.e(e)
        } catch (e: NoSuchMethodException) {
            Timber.e(e)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            Timber.e(e)
        }
        return fragmentUpDown
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return tabInfoList.size
    }

    /**
     * Get page title for the given position
     * Note: ViewPager2 doesn't have built-in tab titles.
     * Use TabLayout with TabLayoutMediator for tab titles with icons.
     */
    fun getPageTitle(position: Int): CharSequence? {
        val title = "  " + context.getString(tabInfoList[position].captionRes)
        // space added before text for convenience
        val sb = SpannableStringBuilder(title.uppercase(Locale.getDefault()))
        val myDrawable = ContextCompat.getDrawable(context, tabInfoList[position].iconRes)
        if (myDrawable != null) {
            myDrawable.setBounds(
                0,
                0,
                myDrawable.intrinsicWidth / 2,
                myDrawable.intrinsicHeight / 2
            )
            val span = ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE)
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return sb
    }

    fun getTabIcon(position: Int): Int {
        return tabInfoList[position].iconRes
    }

    fun getTabTitle(position: Int): Int {
        return tabInfoList[position].captionRes
    }
}
