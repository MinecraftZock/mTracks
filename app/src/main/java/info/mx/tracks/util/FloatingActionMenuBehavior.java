package info.mx.tracks.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior {
    private float mTranslationY;

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return isSnackbarLayout(dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (child instanceof FloatingActionMenu && isSnackbarLayout(dependency)) {
            this.updateTranslation(parent, child, dependency);
        }

        return false;
    }

    private boolean isSnackbarLayout(View view) {
        // Check if the view is a Snackbar's layout without accessing the restricted SnackbarLayout class
        return view.getClass().getName().equals("com.google.android.material.snackbar.Snackbar$SnackbarLayout");
    }

    private void updateTranslation(CoordinatorLayout parent, View child, View dependency) {
        float translationY = this.getTranslationY(parent, child);
        if (translationY != this.mTranslationY) {
            child.animate().cancel();
            if (Math.abs(translationY - this.mTranslationY) == (float) dependency.getHeight()) {
                child.animate()
                        .translationY(translationY)
                        .setListener(null);
            } else {
                child.setTranslationY(translationY);
            }

            this.mTranslationY = translationY;
        }

    }

    private float getTranslationY(CoordinatorLayout parent, View child) {
        float minOffset = 0.0F;
        List dependencies = parent.getDependencies(child);
        int i = 0;

        for (int z = dependencies.size(); i < z; ++i) {
            View view = (View) dependencies.get(i);
            if (isSnackbarLayout(view) && parent.doViewsOverlap(child, view)) {
                minOffset = Math.min(minOffset, view.getTranslationY() - (float) view.getHeight());
            }
        }

        return minOffset;
    }

}
