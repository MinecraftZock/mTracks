package info.mx.tracks.recyclerview;

import android.database.Cursor;

public class MyListItem {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MyListItem fromCursor(Cursor cursor) {
        //TODO return your MyListItem from cursor.
        return null;
    }
}
