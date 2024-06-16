package info.mx.tracks.common;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import info.mx.tracks.R;

public class LayoutHelper {

    public static void setDayLayout(Context context, View view, boolean value) {
        if (!value) {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_edges_grey));
            ((TextView) view).setTextAppearance(context, R.style.textAppearanceSmall);
            ((TextView) view).setTextColor(Color.BLACK);
        } else {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_edges_blue));
            ((TextView) view).setTextAppearance(context, R.style.textAppearanceSmall);
            ((TextView) view).setTextColor(Color.WHITE);
        }
    }

}
