/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.sqlite;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.db.AbstractValuesBuilder;
import com.robotoworks.mechanoid.db.ActiveRecord;
import com.robotoworks.mechanoid.db.ActiveRecordFactory;
import com.robotoworks.mechanoid.util.Closeables;

import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.TrackstageRid;
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.TrackstageRid.Builder;

public class TrackstageRidRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<TrackstageRidRecord> sFactory = new ActiveRecordFactory<TrackstageRidRecord>() {
		@Override
		public TrackstageRidRecord create(Cursor c) {
			return fromCursor(c);
		}

		@Override
		public Uri getContentUri() {
			return TrackstageRid.CONTENT_URI;
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}
	};
	
	public static ActiveRecordFactory<TrackstageRidRecord> getFactory() {
		return sFactory;
	}

    public static final Creator<TrackstageRidRecord> CREATOR
    	= new Creator<TrackstageRidRecord>() {
        public TrackstageRidRecord createFromParcel(Parcel in) {
            return new TrackstageRidRecord(in);
        }

        public TrackstageRidRecord[] newArray(int size) {
            return new TrackstageRidRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	TrackstageRid._ID,
    	TrackstageRid.REST_ID,
    	TrackstageRid.CREATED,
    	TrackstageRid.URL_MAP_POINTS_JSON,
    	TrackstageRid.URL_DETAIL_XML,
    	TrackstageRid.CONTENT_MAP_POINTS_JSON,
    	TrackstageRid.CONTENT_DETAIL_XML
    };
    
    public interface Indices {
    	int _ID = 0;
    	int REST_ID = 1;
    	int CREATED = 2;
    	int URL_MAP_POINTS_JSON = 3;
    	int URL_DETAIL_XML = 4;
    	int CONTENT_MAP_POINTS_JSON = 5;
    	int CONTENT_DETAIL_XML = 6;
    }
    
    private long mRestId;
    private boolean mRestIdDirty;
    private long mCreated;
    private boolean mCreatedDirty;
    private String mUrlMapPointsJson;
    private boolean mUrlMapPointsJsonDirty;
    private String mUrlDetailXml;
    private boolean mUrlDetailXmlDirty;
    private String mContentMapPointsJson;
    private boolean mContentMapPointsJsonDirty;
    private String mContentDetailXml;
    private boolean mContentDetailXmlDirty;
    
    @Override
    protected String[] _getProjection() {
    	return PROJECTION;
    }
    
    public void setRestId(long restId) {
    	mRestId = restId;
    	mRestIdDirty = true;
    }
    
    public long getRestId() {
    	return mRestId;
    }
    
    public void setCreated(long created) {
    	mCreated = created;
    	mCreatedDirty = true;
    }
    
    public long getCreated() {
    	return mCreated;
    }
    
    public void setUrlMapPointsJson(String urlMapPointsJson) {
    	mUrlMapPointsJson = urlMapPointsJson;
    	mUrlMapPointsJsonDirty = true;
    }
    
    public String getUrlMapPointsJson() {
    	return mUrlMapPointsJson;
    }
    
    public void setUrlDetailXml(String urlDetailXml) {
    	mUrlDetailXml = urlDetailXml;
    	mUrlDetailXmlDirty = true;
    }
    
    public String getUrlDetailXml() {
    	return mUrlDetailXml;
    }
    
    public void setContentMapPointsJson(String contentMapPointsJson) {
    	mContentMapPointsJson = contentMapPointsJson;
    	mContentMapPointsJsonDirty = true;
    }
    
    public String getContentMapPointsJson() {
    	return mContentMapPointsJson;
    }
    
    public void setContentDetailXml(String contentDetailXml) {
    	mContentDetailXml = contentDetailXml;
    	mContentDetailXmlDirty = true;
    }
    
    public String getContentDetailXml() {
    	return mContentDetailXml;
    }
    
    
    public TrackstageRidRecord() {
    	super(TrackstageRid.CONTENT_URI);
	}
	
	private TrackstageRidRecord(Parcel in) {
    	super(TrackstageRid.CONTENT_URI);
    	
		setId(in.readLong());
		
		mRestId = in.readLong();
		mCreated = in.readLong();
		mUrlMapPointsJson = in.readString();
		mUrlDetailXml = in.readString();
		mContentMapPointsJson = in.readString();
		mContentDetailXml = in.readString();
		
		boolean[] dirtyFlags = new boolean[6];
		in.readBooleanArray(dirtyFlags);
		mRestIdDirty = dirtyFlags[0];
		mCreatedDirty = dirtyFlags[1];
		mUrlMapPointsJsonDirty = dirtyFlags[2];
		mUrlDetailXmlDirty = dirtyFlags[3];
		mContentMapPointsJsonDirty = dirtyFlags[4];
		mContentDetailXmlDirty = dirtyFlags[5];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeLong(mRestId);
		dest.writeLong(mCreated);
		dest.writeString(mUrlMapPointsJson);
		dest.writeString(mUrlDetailXml);
		dest.writeString(mContentMapPointsJson);
		dest.writeString(mContentDetailXml);
		dest.writeBooleanArray(new boolean[] {
			mRestIdDirty,
			mCreatedDirty,
			mUrlMapPointsJsonDirty,
			mUrlDetailXmlDirty,
			mContentMapPointsJsonDirty,
			mContentDetailXmlDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = TrackstageRid.newBuilder();

		if(mRestIdDirty) {
			builder.setRestId(mRestId);
		}
		if(mCreatedDirty) {
			builder.setCreated(mCreated);
		}
		if(mUrlMapPointsJsonDirty) {
			builder.setUrlMapPointsJson(mUrlMapPointsJson);
		}
		if(mUrlDetailXmlDirty) {
			builder.setUrlDetailXml(mUrlDetailXml);
		}
		if(mContentMapPointsJsonDirty) {
			builder.setContentMapPointsJson(mContentMapPointsJson);
		}
		if(mContentDetailXmlDirty) {
			builder.setContentDetailXml(mContentDetailXml);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mRestIdDirty = dirty;
		mCreatedDirty = dirty;
		mUrlMapPointsJsonDirty = dirty;
		mUrlDetailXmlDirty = dirty;
		mContentMapPointsJsonDirty = dirty;
		mContentDetailXmlDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setRestId(c.getLong(Indices.REST_ID));
		setCreated(c.getLong(Indices.CREATED));
		setUrlMapPointsJson(c.getString(Indices.URL_MAP_POINTS_JSON));
		setUrlDetailXml(c.getString(Indices.URL_DETAIL_XML));
		setContentMapPointsJson(c.getString(Indices.CONTENT_MAP_POINTS_JSON));
		setContentDetailXml(c.getString(Indices.CONTENT_DETAIL_XML));
	}
	
	public static TrackstageRidRecord fromCursor(Cursor c) {
	    TrackstageRidRecord item = new TrackstageRidRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static TrackstageRidRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(TrackstageRidRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static TrackstageRidRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(TrackstageRid.CONTENT_URI.buildUpon()
			.appendPath(String.valueOf(id)).build(), PROJECTION, null, null, null);
	        
	        if(!c.moveToFirst()) {
	            return null;
	        }
	        
	        return fromCursor(c);
	    } finally {
	        Closeables.closeSilently(c);
	    }
	}
}
