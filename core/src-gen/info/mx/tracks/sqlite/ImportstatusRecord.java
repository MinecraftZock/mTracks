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
import info.mx.tracks.sqlite.MxInfoDBContract.Importstatus;
import info.mx.tracks.sqlite.MxInfoDBContract.Importstatus.Builder;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.db.ActiveRecord;
import com.robotoworks.mechanoid.db.ActiveRecordFactory;
import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.db.AbstractValuesBuilder;

public class ImportstatusRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<ImportstatusRecord> sFactory = new ActiveRecordFactory<ImportstatusRecord>() {
		@Override
		public ImportstatusRecord create(Cursor c) {
			return fromCursor(c);
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

        @Override
                    public Uri getContentUri() {
                        return Importstatus.CONTENT_URI;
                    }
                };

    			public static ActiveRecordFactory<ImportstatusRecord> getFactory() {
		return sFactory;
	}

    public static final Parcelable.Creator<ImportstatusRecord> CREATOR 
    	= new Parcelable.Creator<ImportstatusRecord>() {
        public ImportstatusRecord createFromParcel(Parcel in) {
            return new ImportstatusRecord(in);
        }

        public ImportstatusRecord[] newArray(int size) {
            return new ImportstatusRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	Importstatus._ID,
    	Importstatus.MSG,
    	Importstatus.CREATED
    };
    
    public interface Indices {
    	int _ID = 0;
    	int MSG = 1;
    	int CREATED = 2;
    }
    
    private String mMsg;
    private boolean mMsgDirty;
    private long mCreated;
    private boolean mCreatedDirty;
    
    @Override
    protected String[] _getProjection() {
    	return PROJECTION;
    }
    
    public void setMsg(String msg) {
    	mMsg = msg;
    	mMsgDirty = true;
    }
    
    public String getMsg() {
    	return mMsg;
    }
    
    public void setCreated(long created) {
    	mCreated = created;
    	mCreatedDirty = true;
    }
    
    public long getCreated() {
    	return mCreated;
    }
    
    
    public ImportstatusRecord() {
    	super(Importstatus.CONTENT_URI);
	}
	
	private ImportstatusRecord(Parcel in) {
    	super(Importstatus.CONTENT_URI);
    	
		setId(in.readLong());
		
		mMsg = in.readString();
		mCreated = in.readLong();
		
		boolean[] dirtyFlags = new boolean[2];
		in.readBooleanArray(dirtyFlags);
		mMsgDirty = dirtyFlags[0];
		mCreatedDirty = dirtyFlags[1];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeString(mMsg);
		dest.writeLong(mCreated);
		dest.writeBooleanArray(new boolean[] {
			mMsgDirty,
			mCreatedDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = Importstatus.newBuilder();

		if(mMsgDirty) {
			builder.setMsg(mMsg);
		}
		if(mCreatedDirty) {
			builder.setCreated(mCreated);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mMsgDirty = dirty;
		mCreatedDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setMsg(c.getString(Indices.MSG));
		setCreated(c.getLong(Indices.CREATED));
	}
	
	public static ImportstatusRecord fromCursor(Cursor c) {
	    ImportstatusRecord item = new ImportstatusRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static ImportstatusRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(ImportstatusRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static ImportstatusRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(Importstatus.CONTENT_URI.buildUpon()
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
