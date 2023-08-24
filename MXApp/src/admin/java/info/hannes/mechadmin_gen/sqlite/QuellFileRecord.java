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

import info.hannes.mechadmin_gen.sqlite.MxCalContract.QuellFile;
import info.hannes.mechadmin_gen.sqlite.MxCalContract.QuellFile.Builder;

public class QuellFileRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<QuellFileRecord> sFactory = new ActiveRecordFactory<QuellFileRecord>() {
		@Override
		public QuellFileRecord create(Cursor c) {
			return fromCursor(c);
		}

		@Override
		public Uri getContentUri() {
			return QuellFile.CONTENT_URI;
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}
	};
	
	public static ActiveRecordFactory<QuellFileRecord> getFactory() {
		return sFactory;
	}

    public static final Creator<QuellFileRecord> CREATOR
    	= new Creator<QuellFileRecord>() {
        public QuellFileRecord createFromParcel(Parcel in) {
            return new QuellFileRecord(in);
        }

        public QuellFileRecord[] newArray(int size) {
            return new QuellFileRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	QuellFile._ID,
    	QuellFile.REST_ID,
    	QuellFile.DOWNLOAD_SITE_ID,
    	QuellFile.CREATEDATE,
    	QuellFile.UPDATED_COUNT,
    	QuellFile.CONTENT,
    	QuellFile.LOG,
    	QuellFile.URL
    };
    
    public interface Indices {
    	int _ID = 0;
    	int REST_ID = 1;
    	int DOWNLOAD_SITE_ID = 2;
    	int CREATEDATE = 3;
    	int UPDATED_COUNT = 4;
    	int CONTENT = 5;
    	int LOG = 6;
    	int URL = 7;
    }
    
    private long mRestId;
    private boolean mRestIdDirty;
    private long mDownloadSiteId;
    private boolean mDownloadSiteIdDirty;
    private long mCreatedate;
    private boolean mCreatedateDirty;
    private long mUpdatedCount;
    private boolean mUpdatedCountDirty;
    private String mContent;
    private boolean mContentDirty;
    private String mLog;
    private boolean mLogDirty;
    private String mUrl;
    private boolean mUrlDirty;
    
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
    
    public void setDownloadSiteId(long downloadSiteId) {
    	mDownloadSiteId = downloadSiteId;
    	mDownloadSiteIdDirty = true;
    }
    
    public long getDownloadSiteId() {
    	return mDownloadSiteId;
    }
    
    public void setCreatedate(long createdate) {
    	mCreatedate = createdate;
    	mCreatedateDirty = true;
    }
    
    public long getCreatedate() {
    	return mCreatedate;
    }
    
    public void setUpdatedCount(long updatedCount) {
    	mUpdatedCount = updatedCount;
    	mUpdatedCountDirty = true;
    }
    
    public long getUpdatedCount() {
    	return mUpdatedCount;
    }
    
    public void setContent(String content) {
    	mContent = content;
    	mContentDirty = true;
    }
    
    public String getContent() {
    	return mContent;
    }
    
    public void setLog(String log) {
    	mLog = log;
    	mLogDirty = true;
    }
    
    public String getLog() {
    	return mLog;
    }
    
    public void setUrl(String url) {
    	mUrl = url;
    	mUrlDirty = true;
    }
    
    public String getUrl() {
    	return mUrl;
    }
    
    
    public QuellFileRecord() {
    	super(QuellFile.CONTENT_URI);
	}
	
	private QuellFileRecord(Parcel in) {
    	super(QuellFile.CONTENT_URI);
    	
		setId(in.readLong());
		
		mRestId = in.readLong();
		mDownloadSiteId = in.readLong();
		mCreatedate = in.readLong();
		mUpdatedCount = in.readLong();
		mContent = in.readString();
		mLog = in.readString();
		mUrl = in.readString();
		
		boolean[] dirtyFlags = new boolean[7];
		in.readBooleanArray(dirtyFlags);
		mRestIdDirty = dirtyFlags[0];
		mDownloadSiteIdDirty = dirtyFlags[1];
		mCreatedateDirty = dirtyFlags[2];
		mUpdatedCountDirty = dirtyFlags[3];
		mContentDirty = dirtyFlags[4];
		mLogDirty = dirtyFlags[5];
		mUrlDirty = dirtyFlags[6];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeLong(mRestId);
		dest.writeLong(mDownloadSiteId);
		dest.writeLong(mCreatedate);
		dest.writeLong(mUpdatedCount);
		dest.writeString(mContent);
		dest.writeString(mLog);
		dest.writeString(mUrl);
		dest.writeBooleanArray(new boolean[] {
			mRestIdDirty,
			mDownloadSiteIdDirty,
			mCreatedateDirty,
			mUpdatedCountDirty,
			mContentDirty,
			mLogDirty,
			mUrlDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = QuellFile.newBuilder();

		if(mRestIdDirty) {
			builder.setRestId(mRestId);
		}
		if(mDownloadSiteIdDirty) {
			builder.setDownloadSiteId(mDownloadSiteId);
		}
		if(mCreatedateDirty) {
			builder.setCreatedate(mCreatedate);
		}
		if(mUpdatedCountDirty) {
			builder.setUpdatedCount(mUpdatedCount);
		}
		if(mContentDirty) {
			builder.setContent(mContent);
		}
		if(mLogDirty) {
			builder.setLog(mLog);
		}
		if(mUrlDirty) {
			builder.setUrl(mUrl);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mRestIdDirty = dirty;
		mDownloadSiteIdDirty = dirty;
		mCreatedateDirty = dirty;
		mUpdatedCountDirty = dirty;
		mContentDirty = dirty;
		mLogDirty = dirty;
		mUrlDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setRestId(c.getLong(Indices.REST_ID));
		setDownloadSiteId(c.getLong(Indices.DOWNLOAD_SITE_ID));
		setCreatedate(c.getLong(Indices.CREATEDATE));
		setUpdatedCount(c.getLong(Indices.UPDATED_COUNT));
		setContent(c.getString(Indices.CONTENT));
		setLog(c.getString(Indices.LOG));
		setUrl(c.getString(Indices.URL));
	}
	
	public static QuellFileRecord fromCursor(Cursor c) {
	    QuellFileRecord item = new QuellFileRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static QuellFileRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(QuellFileRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static QuellFileRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(QuellFile.CONTENT_URI.buildUpon()
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
