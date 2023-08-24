/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.sqlite;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import com.robotoworks.mechanoid.db.ContentProviderActions;
import com.robotoworks.mechanoid.db.DefaultContentProviderActions;
import com.robotoworks.mechanoid.db.MechanoidContentProvider;
import com.robotoworks.mechanoid.db.MechanoidSQLiteOpenHelper;

import java.util.Set;

import info.hannes.mechadmin_gen.sqlite.AbstractMxCalOpenHelper.Sources;

public abstract class AbstractMxCalContentProvider extends MechanoidContentProvider {

	protected static final int DOWN_LOAD_SITE = 0;
	protected static final int DOWN_LOAD_SITE_ID = 1;
	protected static final int M_X_SERIE = 2;
	protected static final int M_X_SERIE_ID = 3;
	protected static final int MX_SERIES_TRACK = 4;
	protected static final int MX_SERIES_TRACK_ID = 5;
	protected static final int MX_TRACK = 6;
	protected static final int MX_TRACK_ID = 7;
	protected static final int QUELL_FILE = 8;
	protected static final int QUELL_FILE_ID = 9;
	protected static final int IMPORTSTATUS_CAL = 10;
	protected static final int IMPORTSTATUS_CAL_ID = 11;

	protected static final int QUELL_FILE_SMALL = 12;
	protected static final int QUELL_FILE_SMALL_ID = 13;
	

	
	public static final int NUM_URI_MATCHERS = 14;

	@Override
    protected UriMatcher createUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MxCalContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, "DownLoadSite", DOWN_LOAD_SITE);
		matcher.addURI(authority, "DownLoadSite/#", DOWN_LOAD_SITE_ID);
		matcher.addURI(authority, "MXSerie", M_X_SERIE);
		matcher.addURI(authority, "MXSerie/#", M_X_SERIE_ID);
		matcher.addURI(authority, "MxSeriesTrack", MX_SERIES_TRACK);
		matcher.addURI(authority, "MxSeriesTrack/#", MX_SERIES_TRACK_ID);
		matcher.addURI(authority, "MxTrack", MX_TRACK);
		matcher.addURI(authority, "MxTrack/#", MX_TRACK_ID);
		matcher.addURI(authority, "QuellFile", QUELL_FILE);
		matcher.addURI(authority, "QuellFile/#", QUELL_FILE_ID);
		matcher.addURI(authority, "importstatusCal", IMPORTSTATUS_CAL);
		matcher.addURI(authority, "importstatusCal/#", IMPORTSTATUS_CAL_ID);
		matcher.addURI(authority, "QuellFileSmall", QUELL_FILE_SMALL);
		matcher.addURI(authority, "QuellFileSmall/#", QUELL_FILE_SMALL_ID);

		// User Actions
        return matcher;
    }

    @Override
    protected String[] createContentTypes() {
		String[] contentTypes = new String[NUM_URI_MATCHERS];

		contentTypes[DOWN_LOAD_SITE] = MxCalContract.DownLoadSite.CONTENT_TYPE;
		contentTypes[DOWN_LOAD_SITE_ID] = MxCalContract.DownLoadSite.ITEM_CONTENT_TYPE;
		contentTypes[M_X_SERIE] = MxCalContract.MXSerie.CONTENT_TYPE;
		contentTypes[M_X_SERIE_ID] = MxCalContract.MXSerie.ITEM_CONTENT_TYPE;
		contentTypes[MX_SERIES_TRACK] = MxCalContract.MxSeriesTrack.CONTENT_TYPE;
		contentTypes[MX_SERIES_TRACK_ID] = MxCalContract.MxSeriesTrack.ITEM_CONTENT_TYPE;
		contentTypes[MX_TRACK] = MxCalContract.MxTrack.CONTENT_TYPE;
		contentTypes[MX_TRACK_ID] = MxCalContract.MxTrack.ITEM_CONTENT_TYPE;
		contentTypes[QUELL_FILE] = MxCalContract.QuellFile.CONTENT_TYPE;
		contentTypes[QUELL_FILE_ID] = MxCalContract.QuellFile.ITEM_CONTENT_TYPE;
		contentTypes[IMPORTSTATUS_CAL] = MxCalContract.ImportstatusCal.CONTENT_TYPE;
		contentTypes[IMPORTSTATUS_CAL_ID] = MxCalContract.ImportstatusCal.ITEM_CONTENT_TYPE;
		contentTypes[QUELL_FILE_SMALL] = MxCalContract.QuellFileSmall.CONTENT_TYPE;
		contentTypes[QUELL_FILE_SMALL_ID] = MxCalContract.QuellFileSmall.ITEM_CONTENT_TYPE;

		return contentTypes;
    }

	@Override
	protected MechanoidSQLiteOpenHelper createOpenHelper(Context context) {
        return new MxCalOpenHelper(context);
	}

	@Override
	protected Set<Uri> getRelatedUris(Uri uri) {
		return MxCalContract.REFERENCING_VIEWS.get(uri);
	}

    @Override
    protected ContentProviderActions createActions(int id) {
    	switch(id) {
			case DOWN_LOAD_SITE:
				return createDownLoadSiteActions();
			case DOWN_LOAD_SITE_ID:
				return createDownLoadSiteByIdActions();
			case M_X_SERIE:
				return createMXSerieActions();
			case M_X_SERIE_ID:
				return createMXSerieByIdActions();
			case MX_SERIES_TRACK:
				return createMxSeriesTrackActions();
			case MX_SERIES_TRACK_ID:
				return createMxSeriesTrackByIdActions();
			case MX_TRACK:
				return createMxTrackActions();
			case MX_TRACK_ID:
				return createMxTrackByIdActions();
			case QUELL_FILE:
				return createQuellFileActions();
			case QUELL_FILE_ID:
				return createQuellFileByIdActions();
			case IMPORTSTATUS_CAL:
				return createImportstatusCalActions();
			case IMPORTSTATUS_CAL_ID:
				return createImportstatusCalByIdActions();
			case QUELL_FILE_SMALL:
				return createQuellFileSmallActions();
			case QUELL_FILE_SMALL_ID:
				return createQuellFileSmallByIdActions();
			default:
				throw new UnsupportedOperationException("Unknown id: " + id);
    	}
    }

    protected ContentProviderActions createDownLoadSiteByIdActions() {
    	return new DefaultContentProviderActions(Sources.DOWN_LOAD_SITE, true, DownLoadSiteRecord.getFactory());
    }

    protected ContentProviderActions createDownLoadSiteActions() {
    	return new DefaultContentProviderActions(Sources.DOWN_LOAD_SITE, false, DownLoadSiteRecord.getFactory());
    }

    protected ContentProviderActions createMXSerieByIdActions() {
    	return new DefaultContentProviderActions(Sources.M_X_SERIE, true, MXSerieRecord.getFactory());
    }

    protected ContentProviderActions createMXSerieActions() {
    	return new DefaultContentProviderActions(Sources.M_X_SERIE, false, MXSerieRecord.getFactory());
    }

    protected ContentProviderActions createMxSeriesTrackByIdActions() {
    	return new DefaultContentProviderActions(Sources.MX_SERIES_TRACK, true, MxSeriesTrackRecord.getFactory());
    }

    protected ContentProviderActions createMxSeriesTrackActions() {
    	return new DefaultContentProviderActions(Sources.MX_SERIES_TRACK, false, MxSeriesTrackRecord.getFactory());
    }

    protected ContentProviderActions createMxTrackByIdActions() {
    	return new DefaultContentProviderActions(Sources.MX_TRACK, true, MxTrackRecord.getFactory());
    }

    protected ContentProviderActions createMxTrackActions() {
    	return new DefaultContentProviderActions(Sources.MX_TRACK, false, MxTrackRecord.getFactory());
    }

    protected ContentProviderActions createQuellFileByIdActions() {
    	return new DefaultContentProviderActions(Sources.QUELL_FILE, true, QuellFileRecord.getFactory());
    }

    protected ContentProviderActions createQuellFileActions() {
    	return new DefaultContentProviderActions(Sources.QUELL_FILE, false, QuellFileRecord.getFactory());
    }

    protected ContentProviderActions createImportstatusCalByIdActions() {
    	return new DefaultContentProviderActions(Sources.IMPORTSTATUS_CAL, true, ImportstatusCalRecord.getFactory());
    }

    protected ContentProviderActions createImportstatusCalActions() {
    	return new DefaultContentProviderActions(Sources.IMPORTSTATUS_CAL, false, ImportstatusCalRecord.getFactory());
    }

    protected ContentProviderActions createQuellFileSmallByIdActions() {
    	return new DefaultContentProviderActions(Sources.QUELL_FILE_SMALL, true, QuellFileSmallRecord.getFactory());
    }
    
    protected ContentProviderActions createQuellFileSmallActions() {
    	return new DefaultContentProviderActions(Sources.QUELL_FILE_SMALL, false, QuellFileSmallRecord.getFactory());
    }
    
}
