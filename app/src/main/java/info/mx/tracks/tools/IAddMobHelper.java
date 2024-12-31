package info.mx.tracks.tools;

import android.app.Activity;

public interface IAddMobHelper {

    void onCreateView(Activity activity);

    void onPause(Activity activity);

    void onResume(Activity activity);

    void onDestroy(Activity activity);

    void showInterstitial(Activity activity);
}
