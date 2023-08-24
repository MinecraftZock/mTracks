package info.mx.tracks.common;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import info.mx.tracks.R;

public class LayoutHelper {

    @SuppressWarnings("deprecation")
    public static void setDayLayout(Context context, View view, boolean value) {
        if (!value) {
            if (Build.VERSION.SDK_INT >= 16) {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_edges_grey));
            } else {
                view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_edges_grey));
            }
            ((TextView) view).setTextAppearance(context, R.style.textAppearanceSmall);
            ((TextView) view).setTextColor(Color.BLACK);
        } else {
            if (Build.VERSION.SDK_INT >= 16) {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_edges_blue));
            } else {
                view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_edges_blue));
            }
            ((TextView) view).setTextColor(Color.WHITE);
        }
    }

}
