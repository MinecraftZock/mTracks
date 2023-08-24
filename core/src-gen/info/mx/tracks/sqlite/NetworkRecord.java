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
import info.mx.tracks.sqlite.MxInfoDBContract.Network;
import info.mx.tracks.sqlite.MxInfoDBContract.Network.Builder;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.db.ActiveRecord;
import com.robotoworks.mechanoid.db.ActiveRecordFactory;
import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.db.AbstractValuesBuilder;

public class NetworkRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<NetworkRecord> sFactory = new ActiveRecordFactory<NetworkRecord>() {
		@Override
		public NetworkRecord create(Cursor c) {
			return fromCursor(c);
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

        @Override
                    public Uri getContentUri() {
                        return Network.CONTENT_URI;
                    }
                };

    			public static ActiveRecordFactory<NetworkRecord> getFactory() {
		return sFactory;
	}

    public static final Parcelable.Creator<NetworkRecord> CREATOR 
    	= new Parcelable.Creator<NetworkRecord>() {
        public NetworkRecord createFromParcel(Parcel in) {
            return new NetworkRecord(in);
        }

        public NetworkRecord[] newArray(int size) {
            return new NetworkRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	Network._ID,
    	Network.REASON,
    	Network.TRACKS,
    	Network.CREATED
    };
    
    public interface Indices {
    	int _ID = 0;
    	int REASON = 1;
    	int TRACKS = 2;
    	int CREATED = 3;
    }
    
    private String mReason;
    private boolean mReasonDirty;
    private long mTracks;
    private boolean mTracksDirty;
    private long mCreated;
    private boolean mCreatedDirty;
    
    @Override
    protected String[] _getProjection() {
    	return PROJECTION;
    }
    
    public void setReason(String reason) {
    	mReason = reason;
    	mReasonDirty = true;
    }
    
    public String getReason() {
    	return mReason;
    }
    
    public void setTracks(long tracks) {
    	mTracks = tracks;
    	mTracksDirty = true;
    }
    
    public long getTracks() {
    	return mTracks;
    }
    
    public void setCreated(long created) {
    	mCreated = created;
    	mCreatedDirty = true;
    }
    
    public long getCreated() {
    	return mCreated;
    }
    
    
    public NetworkRecord() {
    	super(Network.CONTENT_URI);
	}
	
	private NetworkRecord(Parcel in) {
    	super(Network.CONTENT_URI);
    	
		setId(in.readLong());
		
		mReason = in.readString();
		mTracks = in.readLong();
		mCreated = in.readLong();
		
		boolean[] dirtyFlags = new boolean[3];
		in.readBooleanArray(dirtyFlags);
		mReasonDirty = dirtyFlags[0];
		mTracksDirty = dirtyFlags[1];
		mCreatedDirty = dirtyFlags[2];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeString(mReason);
		dest.writeLong(mTracks);
		dest.writeLong(mCreated);
		dest.writeBooleanArray(new boolean[] {
			mReasonDirty,
			mTracksDirty,
			mCreatedDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = Network.newBuilder();

		if(mReasonDirty) {
			builder.setReason(mReason);
		}
		if(mTracksDirty) {
			builder.setTracks(mTracks);
		}
		if(mCreatedDirty) {
			builder.setCreated(mCreated);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mReasonDirty = dirty;
		mTracksDirty = dirty;
		mCreatedDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setReason(c.getString(Indices.REASON));
		setTracks(c.getLong(Indices.TRACKS));
		setCreated(c.getLong(Indices.CREATED));
	}
	
	public static NetworkRecord fromCursor(Cursor c) {
	    NetworkRecord item = new NetworkRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static NetworkRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(NetworkRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static NetworkRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(Network.CONTENT_URI.buildUpon()
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
