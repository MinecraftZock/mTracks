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
import info.mx.tracks.sqlite.MxInfoDBContract.Events;
import info.mx.tracks.sqlite.MxInfoDBContract.Events.Builder;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.db.ActiveRecord;
import com.robotoworks.mechanoid.db.ActiveRecordFactory;
import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.db.AbstractValuesBuilder;

public class EventsRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<EventsRecord> sFactory = new ActiveRecordFactory<EventsRecord>() {
		@Override
		public EventsRecord create(Cursor c) {
			return fromCursor(c);
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

        @Override
                    public Uri getContentUri() {
                        return Events.CONTENT_URI;
                    }
                };

    			public static ActiveRecordFactory<EventsRecord> getFactory() {
		return sFactory;
	}

    public static final Parcelable.Creator<EventsRecord> CREATOR 
    	= new Parcelable.Creator<EventsRecord>() {
        public EventsRecord createFromParcel(Parcel in) {
            return new EventsRecord(in);
        }

        public EventsRecord[] newArray(int size) {
            return new EventsRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	Events._ID,
    	Events.REST_ID,
    	Events.CHANGED,
    	Events.TRACK_REST_ID,
    	Events.SERIES_REST_ID,
    	Events.COMMENT,
    	Events.APPROVED,
    	Events.EVENT_DATE
    };
    
    public interface Indices {
    	int _ID = 0;
    	int REST_ID = 1;
    	int CHANGED = 2;
    	int TRACK_REST_ID = 3;
    	int SERIES_REST_ID = 4;
    	int COMMENT = 5;
    	int APPROVED = 6;
    	int EVENT_DATE = 7;
    }
    
    private long mRestId;
    private boolean mRestIdDirty;
    private long mChanged;
    private boolean mChangedDirty;
    private long mTrackRestId;
    private boolean mTrackRestIdDirty;
    private long mSeriesRestId;
    private boolean mSeriesRestIdDirty;
    private String mComment;
    private boolean mCommentDirty;
    private long mApproved;
    private boolean mApprovedDirty;
    private long mEventDate;
    private boolean mEventDateDirty;
    
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
    
    public void setChanged(long changed) {
    	mChanged = changed;
    	mChangedDirty = true;
    }
    
    public long getChanged() {
    	return mChanged;
    }
    
    public void setTrackRestId(long trackRestId) {
    	mTrackRestId = trackRestId;
    	mTrackRestIdDirty = true;
    }
    
    public long getTrackRestId() {
    	return mTrackRestId;
    }
    
    public void setSeriesRestId(long seriesRestId) {
    	mSeriesRestId = seriesRestId;
    	mSeriesRestIdDirty = true;
    }
    
    public long getSeriesRestId() {
    	return mSeriesRestId;
    }
    
    public void setComment(String comment) {
    	mComment = comment;
    	mCommentDirty = true;
    }
    
    public String getComment() {
    	return mComment;
    }
    
    public void setApproved(long approved) {
    	mApproved = approved;
    	mApprovedDirty = true;
    }
    
    public long getApproved() {
    	return mApproved;
    }
    
    public void setEventDate(long eventDate) {
    	mEventDate = eventDate;
    	mEventDateDirty = true;
    }
    
    public long getEventDate() {
    	return mEventDate;
    }
    
    
    public EventsRecord() {
    	super(Events.CONTENT_URI);
	}
	
	private EventsRecord(Parcel in) {
    	super(Events.CONTENT_URI);
    	
		setId(in.readLong());
		
		mRestId = in.readLong();
		mChanged = in.readLong();
		mTrackRestId = in.readLong();
		mSeriesRestId = in.readLong();
		mComment = in.readString();
		mApproved = in.readLong();
		mEventDate = in.readLong();
		
		boolean[] dirtyFlags = new boolean[7];
		in.readBooleanArray(dirtyFlags);
		mRestIdDirty = dirtyFlags[0];
		mChangedDirty = dirtyFlags[1];
		mTrackRestIdDirty = dirtyFlags[2];
		mSeriesRestIdDirty = dirtyFlags[3];
		mCommentDirty = dirtyFlags[4];
		mApprovedDirty = dirtyFlags[5];
		mEventDateDirty = dirtyFlags[6];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeLong(mRestId);
		dest.writeLong(mChanged);
		dest.writeLong(mTrackRestId);
		dest.writeLong(mSeriesRestId);
		dest.writeString(mComment);
		dest.writeLong(mApproved);
		dest.writeLong(mEventDate);
		dest.writeBooleanArray(new boolean[] {
			mRestIdDirty,
			mChangedDirty,
			mTrackRestIdDirty,
			mSeriesRestIdDirty,
			mCommentDirty,
			mApprovedDirty,
			mEventDateDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = Events.newBuilder();

		if(mRestIdDirty) {
			builder.setRestId(mRestId);
		}
		if(mChangedDirty) {
			builder.setChanged(mChanged);
		}
		if(mTrackRestIdDirty) {
			builder.setTrackRestId(mTrackRestId);
		}
		if(mSeriesRestIdDirty) {
			builder.setSeriesRestId(mSeriesRestId);
		}
		if(mCommentDirty) {
			builder.setComment(mComment);
		}
		if(mApprovedDirty) {
			builder.setApproved(mApproved);
		}
		if(mEventDateDirty) {
			builder.setEventDate(mEventDate);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mRestIdDirty = dirty;
		mChangedDirty = dirty;
		mTrackRestIdDirty = dirty;
		mSeriesRestIdDirty = dirty;
		mCommentDirty = dirty;
		mApprovedDirty = dirty;
		mEventDateDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setRestId(c.getLong(Indices.REST_ID));
		setChanged(c.getLong(Indices.CHANGED));
		setTrackRestId(c.getLong(Indices.TRACK_REST_ID));
		setSeriesRestId(c.getLong(Indices.SERIES_REST_ID));
		setComment(c.getString(Indices.COMMENT));
		setApproved(c.getLong(Indices.APPROVED));
		setEventDate(c.getLong(Indices.EVENT_DATE));
	}
	
	public static EventsRecord fromCursor(Cursor c) {
	    EventsRecord item = new EventsRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static EventsRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(EventsRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static EventsRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(Events.CONTENT_URI.buildUpon()
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
