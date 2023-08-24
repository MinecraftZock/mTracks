package info.mx.tracks.common;

import android.content.Context;

import info.mx.tracks.R;

public class StatusHelper {

    public static CharSequence getLongStatusText(Context context, String trackstatus) {
        if (trackstatus == null) {
            return "";
        } else if (trackstatus.equals("O")) {
            return context.getText(R.string.open);
        } else if (trackstatus.equals("CS")) {
            return context.getText(R.string.closed_season);
        } else if (trackstatus.equals("NY")) {
            return context.getText(R.string.closed_notyet);
        } else if (trackstatus.equals("C")) {
            return context.getText(R.string.closed);
        } else if (trackstatus.equals("CT")) {
            return context.getText(R.string.closed_temp);
        } else {
            return "";
        }
    }

    public static CharSequence getShortStatusText(Context context, String trackstatus) {
        if (trackstatus == null) {
            return "";
        } else if (trackstatus.contentEquals(context.getText(R.string.open))) {
            return "O";
        } else if (trackstatus.contentEquals(context.getText(R.string.closed_season))) {
            return "CS";
        } else if (trackstatus.contentEquals(context.getText(R.string.closed_notyet))) {
            return "NY";
        } else if (trackstatus.contentEquals(context.getText(R.string.closed))) {
            return "C";
        } else if (trackstatus.contentEquals(context.getText(R.string.closed_temp))) {
            return "CT";
        } else {
            return "";
        }
    }
}
