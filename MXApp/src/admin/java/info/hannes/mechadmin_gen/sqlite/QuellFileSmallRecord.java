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

import info.hannes.mechadmin_gen.sqlite.MxCalContract.QuellFileSmall;
import info.hannes.mechadmin_gen.sqlite.MxCalContract.QuellFileSmall.Builder;

public class QuellFileSmallRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<QuellFileSmallRecord> sFactory = new ActiveRecordFactory<QuellFileSmallRecord>() {
		@Override
		public QuellFileSmallRecord create(Cursor c) {
			return fromCursor(c);
		}

		@Override
		public Uri getContentUri() {
			return QuellFileSmall.CONTENT_URI;
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}
	};
	
	public static ActiveRecordFactory<QuellFileSmallRecord> getFactory() {
		return sFactory;
	}

    public static final Creator<QuellFileSmallRecord> CREATOR
    	= new Creator<QuellFileSmallRecord>() {
        public QuellFileSmallRecord createFromParcel(Parcel in) {
            return new QuellFileSmallRecord(in);
        }

        public QuellFileSmallRecord[] newArray(int size) {
            return new QuellFileSmallRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	QuellFileSmall._ID,
    	QuellFileSmall.REST_ID,
    	QuellFileSmall.URL,
    	QuellFileSmall.CREATEDATE,
    	QuellFileSmall.UPDATED_COUNT,
    	QuellFileSmall.CONTENT
    };
    
    public interface Indices {
    	int _ID = 0;
    	int REST_ID = 1;
    	int URL = 2;
    	int CREATEDATE = 3;
    	int UPDATED_COUNT = 4;
    	int CONTENT = 5;
    }
    
    private long mRestId;
    private boolean mRestIdDirty;
    private String mUrl;
    private boolean mUrlDirty;
    private long mCreatedate;
    private boolean mCreatedateDirty;
    private long mUpdatedCount;
    private boolean mUpdatedCountDirty;
    private String mContent;
    private boolean mContentDirty;
    
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
    public void setUrl(String url) {
    	mUrl = url;
    	mUrlDirty = true;
    }
    
    public String getUrl() {
    	return mUrl;
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
    
    public QuellFileSmallRecord() {
    	super(QuellFileSmall.CONTENT_URI);
	}
	
	private QuellFileSmallRecord(Parcel in) {
    	super(QuellFileSmall.CONTENT_URI);
    	
		setId(in.readLong());
		
		mRestId = in.readLong();
		mUrl = in.readString();
		mCreatedate = in.readLong();
		mUpdatedCount = in.readLong();
		mContent = in.readString();
		
		boolean[] dirtyFlags = new boolean[5];
		in.readBooleanArray(dirtyFlags);
		mRestIdDirty = dirtyFlags[0];
		mUrlDirty = dirtyFlags[1];
		mCreatedateDirty = dirtyFlags[2];
		mUpdatedCountDirty = dirtyFlags[3];
		mContentDirty = dirtyFlags[4];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeLong(mRestId);
		dest.writeString(mUrl);
		dest.writeLong(mCreatedate);
		dest.writeLong(mUpdatedCount);
		dest.writeString(mContent);
		dest.writeBooleanArray(new boolean[] {
			mRestIdDirty,
			mUrlDirty,
			mCreatedateDirty,
			mUpdatedCountDirty,
			mContentDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = QuellFileSmall.newBuilder();

		if(mRestIdDirty) {
			builder.setRestId(mRestId);
		}
		if(mUrlDirty) {
			builder.setUrl(mUrl);
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
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mRestIdDirty = dirty;
		mUrlDirty = dirty;
		mCreatedateDirty = dirty;
		mUpdatedCountDirty = dirty;
		mContentDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setRestId(c.getLong(Indices.REST_ID));
		setUrl(c.getString(Indices.URL));
		setCreatedate(c.getLong(Indices.CREATEDATE));
		setUpdatedCount(c.getLong(Indices.UPDATED_COUNT));
		setContent(c.getString(Indices.CONTENT));
	}
	
	public static QuellFileSmallRecord fromCursor(Cursor c) {
	    QuellFileSmallRecord item = new QuellFileSmallRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static QuellFileSmallRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(QuellFileSmallRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static QuellFileSmallRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(QuellFileSmall.CONTENT_URI.buildUpon()
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
