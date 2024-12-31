package info.mx.tracks.sqlite;

import info.mx.tracks.BuildConfig;

/**
 * using SQLite form library needs an own authority, otherwise both apps with same library causes an security exception
 */
public class MxInfoDBContentProviderAuthority {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".sqlite.mxinfodb";
}
