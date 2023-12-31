/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.sqlite;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.net.Uri;
import info.mx.tracks.sqlite.MxInfoDBContract.Countrycount;
import info.mx.tracks.sqlite.MxInfoDBContract.Countrycount.Builder;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.db.ActiveRecord;
import com.robotoworks.mechanoid.db.ActiveRecordFactory;
import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.db.AbstractValuesBuilder;

public class CountrycountRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<CountrycountRecord> sFactory = new ActiveRecordFactory<CountrycountRecord>() {
		@Override
		public CountrycountRecord create(Cursor c) {
			return fromCursor(c);
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

        @Override
                    public Uri getContentUri() {
                        return Countrycount.CONTENT_URI;
                    }
                };

    			public static ActiveRecordFactory<CountrycountRecord> getFactory() {
		return sFactory;
	}

    public static final Parcelable.Creator<CountrycountRecord> CREATOR 
    	= new Parcelable.Creator<CountrycountRecord>() {
        public CountrycountRecord createFromParcel(Parcel in) {
            return new CountrycountRecord(in);
        }

        public CountrycountRecord[] newArray(int size) {
            return new CountrycountRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	Countrycount._ID,
    	Countrycount.COUNTRY,
    	Countrycount.SHOW,
    	Countrycount.COUNT
    };
    
    public interface Indices {
    	int _ID = 0;
    	int COUNTRY = 1;
    	int SHOW = 2;
    	int COUNT = 3;
    }
    
    private String mCountry;
    private boolean mCountryDirty;
    private long mShow;
    private boolean mShowDirty;
    private String mCount;
    private boolean mCountDirty;
    
    @Override
    protected String[] _getProjection() {
    	return PROJECTION;
    }
    
    public void setCountry(String country) {
    	mCountry = country;
    	mCountryDirty = true;
    }
    
    public String getCountry() {
    	return mCountry;
    }
    public void setShow(long show) {
    	mShow = show;
    	mShowDirty = true;
    }
    
    public long getShow() {
    	return mShow;
    }
    public void setCount(String count) {
    	mCount = count;
    	mCountDirty = true;
    }
    
    public String getCount() {
    	return mCount;
    }
    
    public CountrycountRecord() {
    	super(Countrycount.CONTENT_URI);
	}
	
	private CountrycountRecord(Parcel in) {
    	super(Countrycount.CONTENT_URI);
    	
		setId(in.readLong());
		
		mCountry = in.readString();
		mShow = in.readLong();
		mCount = in.readString();
		
		boolean[] dirtyFlags = new boolean[3];
		in.readBooleanArray(dirtyFlags);
		mCountryDirty = dirtyFlags[0];
		mShowDirty = dirtyFlags[1];
		mCountDirty = dirtyFlags[2];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeString(mCountry);
		dest.writeLong(mShow);
		dest.writeString(mCount);
		dest.writeBooleanArray(new boolean[] {
			mCountryDirty,
			mShowDirty,
			mCountDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = Countrycount.newBuilder();

		if(mCountryDirty) {
			builder.setCountry(mCountry);
		}
		if(mShowDirty) {
			builder.setShow(mShow);
		}
		if(mCountDirty) {
			builder.setCount(mCount);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mCountryDirty = dirty;
		mShowDirty = dirty;
		mCountDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setCountry(c.getString(Indices.COUNTRY));
		setShow(c.getLong(Indices.SHOW));
		setCount(c.getString(Indices.COUNT));
	}
	
	public static CountrycountRecord fromCursor(Cursor c) {
	    CountrycountRecord item = new CountrycountRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static CountrycountRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(CountrycountRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static CountrycountRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(Countrycount.CONTENT_URI.buildUpon()
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
