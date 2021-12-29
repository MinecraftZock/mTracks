package info.mx.tracks.trackdetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import info.mx.tracks.R;
import info.mx.tracks.common.FragmentUpDown;
import info.mx.tracks.trackdetail.comment.CommentFragment;
import info.mx.tracks.trackdetail.event.EventFragment;
import timber.log.Timber;

public abstract class BaseAdapterFragmentTab extends FragmentPagerAdapter {

    private final Bundle bundle;
    private final Context context;
    private final List<TabFragmentInfo> tabInfos;
    private final FragmentUpDown[] fragments;

    protected List<TabFragmentInfo> getTabsInfo() {
        List<TabFragmentInfo> tabs = new ArrayList<>();
        tabs.add(new TabFragmentInfo(CommentFragment.class, R.string.title_activity_comment, R.drawable.ic_comment_white_24px));
        tabs.add(new TabFragmentInfo(FragmentTrackDetail.class, R.string.info, R.drawable.ic_info));
        tabs.add(new TabFragmentInfo(EventFragment.class, R.string.events, R.drawable.ic_event_note_white_24px));
        return tabs;
    }

    BaseAdapterFragmentTab(Context context, FragmentManager fm, Bundle arguments) {
        super(fm);
        this.bundle = arguments;
        this.context = context;
        tabInfos = getTabsInfo();
        fragments = new FragmentUpDown[tabInfos.size()];
    }

    private <T extends FragmentUpDown> T getFragment(Class<T> fragmentClass) {
        T fragmentUpDown = null;
        try {
            fragmentUpDown = fragmentClass.newInstance();
            fragmentUpDown.setArguments(bundle);
        } catch (InstantiationException | IllegalAccessException e) {
            Timber.e(e);
        }
        return fragmentUpDown;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            fragments[position] = getFragment(getTabsInfo().get(position).getFragmentClass());
        }
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String title = "  " + context.getString(tabInfos.get(position).getCaptionRes());
        SpannableStringBuilder sb = new SpannableStringBuilder(title.toUpperCase()); // space added before text for convenience

        Drawable myDrawable = ContextCompat.getDrawable(context, tabInfos.get(position).getIconRes());
        if (myDrawable != null) {
            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth() / 2, myDrawable.getIntrinsicHeight() / 2);
            ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return sb;
    }

    @Override
    public int getCount() {
        return tabInfos.size();
    }

}
