package info.mx.tracks.common;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class On3StateClickListener implements OnClickListener {

    protected int level;

    @Override
    public void onClick(View view) {
        level = ((ImageView) view).getDrawable().getLevel();
        level++;
        if (level == 3) {
            level = 0;
        }
        ((ImageView) view).setImageLevel(level);
    }

}
