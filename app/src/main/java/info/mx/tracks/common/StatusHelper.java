package info.mx.tracks.common;

import android.content.Context;

import info.mx.tracks.R;

public class StatusHelper {

    public static CharSequence getLongStatusText(Context context, String trackStatus) {
        if (trackStatus == null) {
            return "";
        } else if (trackStatus.equals("O")) {
            return context.getText(R.string.open);
        } else if (trackStatus.equals("CS")) {
            return context.getText(R.string.closed_season);
        } else if (trackStatus.equals("NY")) {
            return context.getText(R.string.closed_notyet);
        } else if (trackStatus.equals("C")) {
            return context.getText(R.string.closed);
        } else if (trackStatus.equals("CT")) {
            return context.getText(R.string.closed_temp);
        } else {
            return "";
        }
    }

    public static CharSequence getShortStatusText(Context context, String trackStatus) {
        if (trackStatus == null) {
            return "";
        } else if (trackStatus.contentEquals(context.getText(R.string.open))) {
            return "O";
        } else if (trackStatus.contentEquals(context.getText(R.string.closed_season))) {
            return "CS";
        } else if (trackStatus.contentEquals(context.getText(R.string.closed_notyet))) {
            return "NY";
        } else if (trackStatus.contentEquals(context.getText(R.string.closed))) {
            return "C";
        } else if (trackStatus.contentEquals(context.getText(R.string.closed_temp))) {
            return "CT";
        } else {
            return "";
        }
    }
}
