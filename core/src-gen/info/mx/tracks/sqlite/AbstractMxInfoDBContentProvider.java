/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.sqlite;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import com.robotoworks.mechanoid.db.ContentProviderActions;
import com.robotoworks.mechanoid.db.DefaultContentProviderActions;
import com.robotoworks.mechanoid.db.MechanoidContentProvider;
import com.robotoworks.mechanoid.db.MechanoidSQLiteOpenHelper;

import java.util.Set;

import info.mx.tracks.sqlite.AbstractMxInfoDBOpenHelper.Sources;

public abstract class AbstractMxInfoDBContentProvider extends MechanoidContentProvider {

    public static final int TRACKS = 0;
    public static final int TRACKS_ID = 1;
    public static final int TRACKSTAGE = 2;
    public static final int TRACKSTAGE_ID = 3;
    public static final int FAVORITS = 6;
    public static final int FAVORITS_ID = 7;
    public static final int COUNTRY = 8;
    public static final int COUNTRY_ID = 9;
    public static final int PICTURES = 10;
    public static final int PICTURES_ID = 11;
    public static final int  IMPORTSTATUS = 16;
    public static final int IMPORTSTATUS_ID = 17;
    public static final int WEATHER = 18;
    public static final int WEATHER_ID = 19;
    public static final int ROUTE = 20;
    public static final int ROUTE_ID = 21;

    public static final int COUNTRYSUM = 24;
    public static final int COUNTRYSUM_ID = 25;
    public static final int PICTURESUM = 26;
    public static final int PICTURESUM_ID = 27;
    public static final int  COUNTRYCOUNT = 32;
    public static final int COUNTRYCOUNT_ID = 33;
    public static final int USER_ACTIVITY = 34;
    public static final int USER_ACTIVITY_ID = 35;
    public static final int TRACKSGES = 36;
    public static final int TRACKSGES_ID = 37;
    public static final int TRACKS_GES_SUM = 38;
    public static final int TRACKS_GES_SUM_ID = 39;

    public static final int NUM_URI_MATCHERS = 40;

    @Override
    protected UriMatcher createUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MxInfoDBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "tracks", TRACKS);
        matcher.addURI(authority, "tracks/#", TRACKS_ID);
        matcher.addURI(authority, "trackstage", TRACKSTAGE);
        matcher.addURI(authority, "trackstage/#", TRACKSTAGE_ID);
        matcher.addURI(authority, "favorits", FAVORITS);
        matcher.addURI(authority, "favorits/#", FAVORITS_ID);
        matcher.addURI(authority, "country", COUNTRY);
        matcher.addURI(authority, "country/#", COUNTRY_ID);
        matcher.addURI(authority, "pictures", PICTURES);
        matcher.addURI(authority, "pictures/#", PICTURES_ID);
        matcher.addURI(authority, "importstatus", IMPORTSTATUS);
        matcher.addURI(authority, "importstatus/#", IMPORTSTATUS_ID);
        matcher.addURI(authority, "weather", WEATHER);
        matcher.addURI(authority, "weather/#", WEATHER_ID);
        matcher.addURI(authority, "route", ROUTE);
        matcher.addURI(authority, "route/#", ROUTE_ID);
        matcher.addURI(authority, "countrysum", COUNTRYSUM);
        matcher.addURI(authority, "countrysum/#", COUNTRYSUM_ID);
        matcher.addURI(authority, "picturesum", PICTURESUM);
        matcher.addURI(authority, "picturesum/#", PICTURESUM_ID);

        matcher.addURI(authority, "countrycount", COUNTRYCOUNT);
        matcher.addURI(authority, "countrycount/#", COUNTRYCOUNT_ID);
        matcher.addURI(authority, "userActivity", USER_ACTIVITY);
        matcher.addURI(authority, "userActivity/#", USER_ACTIVITY_ID);
        matcher.addURI(authority, "tracksges", TRACKSGES);
        matcher.addURI(authority, "tracksges/#", TRACKSGES_ID);
        matcher.addURI(authority, "tracksGesSum", TRACKS_GES_SUM);
        matcher.addURI(authority, "tracksGesSum/#", TRACKS_GES_SUM_ID);

        // User Actions
        return matcher;
    }

    @Override
    protected String[] createContentTypes() {
        String[] contentTypes = new String[NUM_URI_MATCHERS];

        contentTypes[TRACKS] = MxInfoDBContract.Tracks.CONTENT_TYPE;
        contentTypes[TRACKS_ID] = MxInfoDBContract.Tracks.ITEM_CONTENT_TYPE;
        contentTypes[TRACKSTAGE] = MxInfoDBContract.Trackstage.CONTENT_TYPE;
        contentTypes[TRACKSTAGE_ID] = MxInfoDBContract.Trackstage.ITEM_CONTENT_TYPE;
        contentTypes[FAVORITS_ID] = MxInfoDBContract.Favorits.ITEM_CONTENT_TYPE;
		contentTypes[COUNTRY] = MxInfoDBContract.Country.CONTENT_TYPE;
		contentTypes[COUNTRY_ID] = MxInfoDBContract.Country.ITEM_CONTENT_TYPE;
		contentTypes[PICTURES] = MxInfoDBContract.Pictures.CONTENT_TYPE;
		contentTypes[PICTURES_ID] = MxInfoDBContract.Pictures.ITEM_CONTENT_TYPE;
		contentTypes[IMPORTSTATUS] = MxInfoDBContract.Importstatus.CONTENT_TYPE;
        contentTypes[IMPORTSTATUS_ID] = MxInfoDBContract.Importstatus.ITEM_CONTENT_TYPE;
        contentTypes[WEATHER] = MxInfoDBContract.Weather.CONTENT_TYPE;
        contentTypes[WEATHER_ID] = MxInfoDBContract.Weather.ITEM_CONTENT_TYPE;
        contentTypes[ROUTE] = MxInfoDBContract.Route.CONTENT_TYPE;
        contentTypes[ROUTE_ID] = MxInfoDBContract.Route.ITEM_CONTENT_TYPE;
        contentTypes[COUNTRYSUM] = MxInfoDBContract.Countrysum.CONTENT_TYPE;
        contentTypes[COUNTRYSUM_ID] = MxInfoDBContract.Countrysum.ITEM_CONTENT_TYPE;
        contentTypes[PICTURESUM] = MxInfoDBContract.Picturesum.CONTENT_TYPE;
        contentTypes[PICTURESUM_ID] = MxInfoDBContract.Picturesum.ITEM_CONTENT_TYPE;
        contentTypes[COUNTRYCOUNT] = MxInfoDBContract.Countrycount.CONTENT_TYPE;
        contentTypes[COUNTRYCOUNT_ID] = MxInfoDBContract.Countrycount.ITEM_CONTENT_TYPE;
        contentTypes[USER_ACTIVITY] = MxInfoDBContract.UserActivity.CONTENT_TYPE;
        contentTypes[USER_ACTIVITY_ID] = MxInfoDBContract.UserActivity.ITEM_CONTENT_TYPE;
        contentTypes[TRACKSGES] = MxInfoDBContract.Tracksges.CONTENT_TYPE;
        contentTypes[TRACKSGES_ID] = MxInfoDBContract.Tracksges.ITEM_CONTENT_TYPE;
        contentTypes[TRACKS_GES_SUM] = MxInfoDBContract.TracksGesSum.CONTENT_TYPE;
        contentTypes[TRACKS_GES_SUM_ID] = MxInfoDBContract.TracksGesSum.ITEM_CONTENT_TYPE;

        return contentTypes;
    }

    @Override
    protected MechanoidSQLiteOpenHelper createOpenHelper(Context context) {
        return new MxInfoDBOpenHelper(context);
    }

    @Override
    protected Set<Uri> getRelatedUris(Uri uri) {
        return MxInfoDBContract.REFERENCING_VIEWS.get(uri);
    }

    @Override
    protected ContentProviderActions createActions(int id) {
        switch (id) {
            case TRACKS:
                return createTracksActions();
            case TRACKS_ID:
                return createTracksByIdActions();
            case TRACKSTAGE:
                return createTrackstageActions();
            case TRACKSTAGE_ID:
                return createTrackstageByIdActions();
            case FAVORITS:
                return createFavoritsActions();
            case FAVORITS_ID:
                return createFavoritsByIdActions();
            case COUNTRY:
                return createCountryActions();
            case COUNTRY_ID:
                return createCountryByIdActions();
            case PICTURES:
                return createPicturesActions();
            case PICTURES_ID:
                return createPicturesByIdActions();
            case  IMPORTSTATUS:
                return createImportstatusActions();
            case IMPORTSTATUS_ID:
                return createImportstatusByIdActions();
            case WEATHER:
                return createWeatherActions();
            case WEATHER_ID:
                return createWeatherByIdActions();
            case ROUTE:
                return createRouteActions();
            case ROUTE_ID:
                return createRouteByIdActions();
            case COUNTRYSUM:
                return createCountrysumActions();
            case COUNTRYSUM_ID:
                return createCountrysumByIdActions();
            case PICTURESUM:
                return createPicturesumActions();
            case PICTURESUM_ID:
                return createPicturesumByIdActions();

            case COUNTRYCOUNT:
                return createCountrycountActions();
            case COUNTRYCOUNT_ID:
                return createCountrycountByIdActions();
            case USER_ACTIVITY:
                return createUserActivityActions();
            case USER_ACTIVITY_ID:
                return createUserActivityByIdActions();
            case TRACKSGES:
                return createTracksgesActions();
            case TRACKSGES_ID:
                return createTracksgesByIdActions();
            case TRACKS_GES_SUM:
                return createTracksGesSumActions();
            case TRACKS_GES_SUM_ID:
                return createTracksGesSumByIdActions();
            default:
                throw new UnsupportedOperationException("Unknown id: " + id);
        }
    }

    protected ContentProviderActions createTracksByIdActions() {
        return new DefaultContentProviderActions(Sources.TRACKS, true, TracksRecord.getFactory());
    }

    protected ContentProviderActions createTracksActions() {
        return new DefaultContentProviderActions(Sources.TRACKS, false, TracksRecord.getFactory());
    }

    protected ContentProviderActions createTrackstageByIdActions() {
        return new DefaultContentProviderActions(Sources.TRACKSTAGE, true, TrackstageRecord.getFactory());
    }

    protected ContentProviderActions createTrackstageActions() {
        return new DefaultContentProviderActions(Sources.TRACKSTAGE, false, TrackstageRecord.getFactory());
    }
    protected ContentProviderActions createFavoritsByIdActions() {
        return new DefaultContentProviderActions(Sources.FAVORITS, true, FavoritsRecord.getFactory());
    }

    protected ContentProviderActions createFavoritsActions() {
        return new DefaultContentProviderActions(Sources.FAVORITS, false, FavoritsRecord.getFactory());
    }

    protected ContentProviderActions createCountryByIdActions() {
        return new DefaultContentProviderActions(Sources.COUNTRY, true, CountryRecord.getFactory());
    }

    protected ContentProviderActions createCountryActions() {
        return new DefaultContentProviderActions(Sources.COUNTRY, false, CountryRecord.getFactory());
    }

    protected ContentProviderActions createPicturesByIdActions() {
        return new DefaultContentProviderActions(Sources.PICTURES, true, PicturesRecord.getFactory());
    }

    protected ContentProviderActions createPicturesActions() {
        return new DefaultContentProviderActions(Sources.PICTURES, false, PicturesRecord.getFactory());
    }

    
    protected ContentProviderActions createImportstatusByIdActions() {
        return new DefaultContentProviderActions(Sources.IMPORTSTATUS, true, ImportstatusRecord.getFactory());
    }

    protected ContentProviderActions createImportstatusActions() {
        return new DefaultContentProviderActions(Sources.IMPORTSTATUS, false, ImportstatusRecord.getFactory());
    }

    protected ContentProviderActions createWeatherByIdActions() {
        return new DefaultContentProviderActions(Sources.WEATHER, true, WeatherRecord.getFactory());
    }

    protected ContentProviderActions createWeatherActions() {
        return new DefaultContentProviderActions(Sources.WEATHER, false, WeatherRecord.getFactory());
    }

    protected ContentProviderActions createRouteByIdActions() {
        return new DefaultContentProviderActions(Sources.ROUTE, true, RouteRecord.getFactory());
    }

    protected ContentProviderActions createRouteActions() {
        return new DefaultContentProviderActions(Sources.ROUTE, false, RouteRecord.getFactory());
    }

    protected ContentProviderActions createCountrysumByIdActions() {
        return new DefaultContentProviderActions(Sources.COUNTRYSUM, true, CountrysumRecord.getFactory());
    }

    protected ContentProviderActions createCountrysumActions() {
        return new DefaultContentProviderActions(Sources.COUNTRYSUM, false, CountrysumRecord.getFactory());
    }

    protected ContentProviderActions createPicturesumByIdActions() {
        return new DefaultContentProviderActions(Sources.PICTURESUM, true, PicturesumRecord.getFactory());
    }

    protected ContentProviderActions createPicturesumActions() {
        return new DefaultContentProviderActions(Sources.PICTURESUM, false, PicturesumRecord.getFactory());
    }

    
    protected ContentProviderActions createCountrycountByIdActions() {
        return new DefaultContentProviderActions(Sources.COUNTRYCOUNT, true, CountrycountRecord.getFactory());
    }

    protected ContentProviderActions createCountrycountActions() {
        return new DefaultContentProviderActions(Sources.COUNTRYCOUNT, false, CountrycountRecord.getFactory());
    }

    protected ContentProviderActions createUserActivityByIdActions() {
        return new DefaultContentProviderActions(Sources.USER_ACTIVITY, true, UserActivityRecord.getFactory());
    }

    protected ContentProviderActions createUserActivityActions() {
        return new DefaultContentProviderActions(Sources.USER_ACTIVITY, false, UserActivityRecord.getFactory());
    }

    protected ContentProviderActions createTracksgesByIdActions() {
        return new DefaultContentProviderActions(Sources.TRACKSGES, true, TracksgesRecord.getFactory());
    }

    protected ContentProviderActions createTracksgesActions() {
        return new DefaultContentProviderActions(Sources.TRACKSGES, false, TracksgesRecord.getFactory());
    }

    protected ContentProviderActions createTracksGesSumByIdActions() {
        return new DefaultContentProviderActions(Sources.TRACKS_GES_SUM, true, TracksGesSumRecord.getFactory());
    }

    protected ContentProviderActions createTracksGesSumActions() {
        return new DefaultContentProviderActions(Sources.TRACKS_GES_SUM, false, TracksGesSumRecord.getFactory());
    }

}
