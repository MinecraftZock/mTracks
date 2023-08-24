package info.mx.tracks.common;

import java.util.Locale;

import info.mx.tracks.R;

public class TrackHelper {

    public static int getEventDrawableID(long predicate, String country) {
        int res = 0;
        switch ((int) predicate) {
            case 1:
                res = R.drawable.mx1_logo;
                break;
            case 2:
                res = R.drawable.mx1_logo;
                break;
            case 3:
                res = R.drawable.mx1_logo;
                break;
            case 4:
                res = R.drawable.adac_masters;
                break;
            case 5:
                if (country.toLowerCase(Locale.getDefault()).equals("de")) {
                    res = R.drawable.xcc_logo_de;
                } else if (country.toLowerCase(Locale.getDefault()).equals("at")) {
                    res = R.drawable.xcc_logo_at;
                } else if (country.toLowerCase(Locale.getDefault()).equals("it")) {
                    res = R.drawable.xcc_logo_it;
                }
                break;
            default:
                res = 0;
        }
        return res;
    }
}
