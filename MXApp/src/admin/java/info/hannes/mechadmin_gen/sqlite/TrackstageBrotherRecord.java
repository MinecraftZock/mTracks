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

import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.TrackstageBrother;
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.TrackstageBrother.Builder;

public class TrackstageBrotherRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<TrackstageBrotherRecord> sFactory = new ActiveRecordFactory<TrackstageBrotherRecord>() {
		@Override
		public TrackstageBrotherRecord create(Cursor c) {
			return fromCursor(c);
		}

		@Override
		public Uri getContentUri() {
			return TrackstageBrother.CONTENT_URI;
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}
	};
	
	public static ActiveRecordFactory<TrackstageBrotherRecord> getFactory() {
		return sFactory;
	}

    public static final Creator<TrackstageBrotherRecord> CREATOR
    	= new Creator<TrackstageBrotherRecord>() {
        public TrackstageBrotherRecord createFromParcel(Parcel in) {
            return new TrackstageBrotherRecord(in);
        }

        public TrackstageBrotherRecord[] newArray(int size) {
            return new TrackstageBrotherRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	TrackstageBrother._ID,
    	TrackstageBrother.REST_ID,
    	TrackstageBrother.TRACK_REST_ID,
    	TrackstageBrother.CREATED,
    	TrackstageBrother.TRACKNAME,
    	TrackstageBrother.LONGITUDE,
    	TrackstageBrother.LATITUDE,
    	TrackstageBrother.COUNTRY,
    	TrackstageBrother.ANDROIDID,
    	TrackstageBrother.URL_DETAIL_XML,
    	TrackstageBrother.CONTENT_DETAIL_XML,
    	TrackstageBrother.URL_PHOTO,
    	TrackstageBrother.CONTENT_PHOTO,
    	TrackstageBrother.URL,
    	TrackstageBrother.PHONE,
    	TrackstageBrother.NOTES,
    	TrackstageBrother.VOTES,
    	TrackstageBrother.OPENMONDAYS,
    	TrackstageBrother.OPENTUESDAYS,
    	TrackstageBrother.OPENWEDNESDAY,
    	TrackstageBrother.OPENTHURSDAY,
    	TrackstageBrother.OPENFRIDAY,
    	TrackstageBrother.OPENSATURDAY,
    	TrackstageBrother.OPENSUNDAY,
    	TrackstageBrother.HOURSMONDAY,
    	TrackstageBrother.HOURSTUESDAY,
    	TrackstageBrother.HOURSWEDNESDAY,
    	TrackstageBrother.HOURSTHURSDAY,
    	TrackstageBrother.HOURSFRIDAY,
    	TrackstageBrother.HOURSSATURDAY,
    	TrackstageBrother.HOURSSUNDAY,
    	TrackstageBrother.TRACKACCESS
    };
    
    public interface Indices {
    	int _ID = 0;
    	int REST_ID = 1;
    	int TRACK_REST_ID = 2;
    	int CREATED = 3;
    	int TRACKNAME = 4;
    	int LONGITUDE = 5;
    	int LATITUDE = 6;
    	int COUNTRY = 7;
    	int ANDROIDID = 8;
    	int URL_DETAIL_XML = 9;
    	int CONTENT_DETAIL_XML = 10;
    	int URL_PHOTO = 11;
    	int CONTENT_PHOTO = 12;
    	int URL = 13;
    	int PHONE = 14;
    	int NOTES = 15;
    	int VOTES = 16;
    	int OPENMONDAYS = 17;
    	int OPENTUESDAYS = 18;
    	int OPENWEDNESDAY = 19;
    	int OPENTHURSDAY = 20;
    	int OPENFRIDAY = 21;
    	int OPENSATURDAY = 22;
    	int OPENSUNDAY = 23;
    	int HOURSMONDAY = 24;
    	int HOURSTUESDAY = 25;
    	int HOURSWEDNESDAY = 26;
    	int HOURSTHURSDAY = 27;
    	int HOURSFRIDAY = 28;
    	int HOURSSATURDAY = 29;
    	int HOURSSUNDAY = 30;
    	int TRACKACCESS = 31;
    }
    
    private long mRestId;
    private boolean mRestIdDirty;
    private long mTrackRestId;
    private boolean mTrackRestIdDirty;
    private long mCreated;
    private boolean mCreatedDirty;
    private String mTrackname;
    private boolean mTracknameDirty;
    private double mLongitude;
    private boolean mLongitudeDirty;
    private double mLatitude;
    private boolean mLatitudeDirty;
    private String mCountry;
    private boolean mCountryDirty;
    private String mAndroidid;
    private boolean mAndroididDirty;
    private String mUrlDetailXml;
    private boolean mUrlDetailXmlDirty;
    private String mContentDetailXml;
    private boolean mContentDetailXmlDirty;
    private String mUrlPhoto;
    private boolean mUrlPhotoDirty;
    private String mContentPhoto;
    private boolean mContentPhotoDirty;
    private String mUrl;
    private boolean mUrlDirty;
    private String mPhone;
    private boolean mPhoneDirty;
    private String mNotes;
    private boolean mNotesDirty;
    private String mVotes;
    private boolean mVotesDirty;
    private long mOpenmondays;
    private boolean mOpenmondaysDirty;
    private long mOpentuesdays;
    private boolean mOpentuesdaysDirty;
    private long mOpenwednesday;
    private boolean mOpenwednesdayDirty;
    private long mOpenthursday;
    private boolean mOpenthursdayDirty;
    private long mOpenfriday;
    private boolean mOpenfridayDirty;
    private long mOpensaturday;
    private boolean mOpensaturdayDirty;
    private long mOpensunday;
    private boolean mOpensundayDirty;
    private String mHoursmonday;
    private boolean mHoursmondayDirty;
    private String mHourstuesday;
    private boolean mHourstuesdayDirty;
    private String mHourswednesday;
    private boolean mHourswednesdayDirty;
    private String mHoursthursday;
    private boolean mHoursthursdayDirty;
    private String mHoursfriday;
    private boolean mHoursfridayDirty;
    private String mHourssaturday;
    private boolean mHourssaturdayDirty;
    private String mHourssunday;
    private boolean mHourssundayDirty;
    private String mTrackaccess;
    private boolean mTrackaccessDirty;
    
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
    
    public void setTrackRestId(long trackRestId) {
    	mTrackRestId = trackRestId;
    	mTrackRestIdDirty = true;
    }
    
    public long getTrackRestId() {
    	return mTrackRestId;
    }
    
    public void setCreated(long created) {
    	mCreated = created;
    	mCreatedDirty = true;
    }
    
    public long getCreated() {
    	return mCreated;
    }
    
    public void setTrackname(String trackname) {
    	mTrackname = trackname;
    	mTracknameDirty = true;
    }
    
    public String getTrackname() {
    	return mTrackname;
    }
    
    public void setLongitude(double longitude) {
    	mLongitude = longitude;
    	mLongitudeDirty = true;
    }
    
    public double getLongitude() {
    	return mLongitude;
    }
    
    public void setLatitude(double latitude) {
    	mLatitude = latitude;
    	mLatitudeDirty = true;
    }
    
    public double getLatitude() {
    	return mLatitude;
    }
    
    public void setCountry(String country) {
    	mCountry = country;
    	mCountryDirty = true;
    }
    
    public String getCountry() {
    	return mCountry;
    }
    
    public void setAndroidid(String androidid) {
    	mAndroidid = androidid;
    	mAndroididDirty = true;
    }
    
    public String getAndroidid() {
    	return mAndroidid;
    }
    
    public void setUrlDetailXml(String urlDetailXml) {
    	mUrlDetailXml = urlDetailXml;
    	mUrlDetailXmlDirty = true;
    }
    
    public String getUrlDetailXml() {
    	return mUrlDetailXml;
    }
    
    public void setContentDetailXml(String contentDetailXml) {
    	mContentDetailXml = contentDetailXml;
    	mContentDetailXmlDirty = true;
    }
    
    public String getContentDetailXml() {
    	return mContentDetailXml;
    }
    
    public void setUrlPhoto(String urlPhoto) {
    	mUrlPhoto = urlPhoto;
    	mUrlPhotoDirty = true;
    }
    
    public String getUrlPhoto() {
    	return mUrlPhoto;
    }
    
    public void setContentPhoto(String contentPhoto) {
    	mContentPhoto = contentPhoto;
    	mContentPhotoDirty = true;
    }
    
    public String getContentPhoto() {
    	return mContentPhoto;
    }
    
    public void setUrl(String url) {
    	mUrl = url;
    	mUrlDirty = true;
    }
    
    public String getUrl() {
    	return mUrl;
    }
    
    public void setPhone(String phone) {
    	mPhone = phone;
    	mPhoneDirty = true;
    }
    
    public String getPhone() {
    	return mPhone;
    }
    
    public void setNotes(String notes) {
    	mNotes = notes;
    	mNotesDirty = true;
    }
    
    public String getNotes() {
    	return mNotes;
    }
    
    public void setVotes(String votes) {
    	mVotes = votes;
    	mVotesDirty = true;
    }
    
    public String getVotes() {
    	return mVotes;
    }
    
    public void setOpenmondays(long openmondays) {
    	mOpenmondays = openmondays;
    	mOpenmondaysDirty = true;
    }
    
    public long getOpenmondays() {
    	return mOpenmondays;
    }
    
    public void setOpentuesdays(long opentuesdays) {
    	mOpentuesdays = opentuesdays;
    	mOpentuesdaysDirty = true;
    }
    
    public long getOpentuesdays() {
    	return mOpentuesdays;
    }
    
    public void setOpenwednesday(long openwednesday) {
    	mOpenwednesday = openwednesday;
    	mOpenwednesdayDirty = true;
    }
    
    public long getOpenwednesday() {
    	return mOpenwednesday;
    }
    
    public void setOpenthursday(long openthursday) {
    	mOpenthursday = openthursday;
    	mOpenthursdayDirty = true;
    }
    
    public long getOpenthursday() {
    	return mOpenthursday;
    }
    
    public void setOpenfriday(long openfriday) {
    	mOpenfriday = openfriday;
    	mOpenfridayDirty = true;
    }
    
    public long getOpenfriday() {
    	return mOpenfriday;
    }
    
    public void setOpensaturday(long opensaturday) {
    	mOpensaturday = opensaturday;
    	mOpensaturdayDirty = true;
    }
    
    public long getOpensaturday() {
    	return mOpensaturday;
    }
    
    public void setOpensunday(long opensunday) {
    	mOpensunday = opensunday;
    	mOpensundayDirty = true;
    }
    
    public long getOpensunday() {
    	return mOpensunday;
    }
    
    public void setHoursmonday(String hoursmonday) {
    	mHoursmonday = hoursmonday;
    	mHoursmondayDirty = true;
    }
    
    public String getHoursmonday() {
    	return mHoursmonday;
    }
    
    public void setHourstuesday(String hourstuesday) {
    	mHourstuesday = hourstuesday;
    	mHourstuesdayDirty = true;
    }
    
    public String getHourstuesday() {
    	return mHourstuesday;
    }
    
    public void setHourswednesday(String hourswednesday) {
    	mHourswednesday = hourswednesday;
    	mHourswednesdayDirty = true;
    }
    
    public String getHourswednesday() {
    	return mHourswednesday;
    }
    
    public void setHoursthursday(String hoursthursday) {
    	mHoursthursday = hoursthursday;
    	mHoursthursdayDirty = true;
    }
    
    public String getHoursthursday() {
    	return mHoursthursday;
    }
    
    public void setHoursfriday(String hoursfriday) {
    	mHoursfriday = hoursfriday;
    	mHoursfridayDirty = true;
    }
    
    public String getHoursfriday() {
    	return mHoursfriday;
    }
    
    public void setHourssaturday(String hourssaturday) {
    	mHourssaturday = hourssaturday;
    	mHourssaturdayDirty = true;
    }
    
    public String getHourssaturday() {
    	return mHourssaturday;
    }
    
    public void setHourssunday(String hourssunday) {
    	mHourssunday = hourssunday;
    	mHourssundayDirty = true;
    }
    
    public String getHourssunday() {
    	return mHourssunday;
    }
    
    public void setTrackaccess(String trackaccess) {
    	mTrackaccess = trackaccess;
    	mTrackaccessDirty = true;
    }
    
    public String getTrackaccess() {
    	return mTrackaccess;
    }
    
    
    public TrackstageBrotherRecord() {
    	super(TrackstageBrother.CONTENT_URI);
	}
	
	private TrackstageBrotherRecord(Parcel in) {
    	super(TrackstageBrother.CONTENT_URI);
    	
		setId(in.readLong());
		
		mRestId = in.readLong();
		mTrackRestId = in.readLong();
		mCreated = in.readLong();
		mTrackname = in.readString();
		mLongitude = in.readDouble();
		mLatitude = in.readDouble();
		mCountry = in.readString();
		mAndroidid = in.readString();
		mUrlDetailXml = in.readString();
		mContentDetailXml = in.readString();
		mUrlPhoto = in.readString();
		mContentPhoto = in.readString();
		mUrl = in.readString();
		mPhone = in.readString();
		mNotes = in.readString();
		mVotes = in.readString();
		mOpenmondays = in.readLong();
		mOpentuesdays = in.readLong();
		mOpenwednesday = in.readLong();
		mOpenthursday = in.readLong();
		mOpenfriday = in.readLong();
		mOpensaturday = in.readLong();
		mOpensunday = in.readLong();
		mHoursmonday = in.readString();
		mHourstuesday = in.readString();
		mHourswednesday = in.readString();
		mHoursthursday = in.readString();
		mHoursfriday = in.readString();
		mHourssaturday = in.readString();
		mHourssunday = in.readString();
		mTrackaccess = in.readString();
		
		boolean[] dirtyFlags = new boolean[31];
		in.readBooleanArray(dirtyFlags);
		mRestIdDirty = dirtyFlags[0];
		mTrackRestIdDirty = dirtyFlags[1];
		mCreatedDirty = dirtyFlags[2];
		mTracknameDirty = dirtyFlags[3];
		mLongitudeDirty = dirtyFlags[4];
		mLatitudeDirty = dirtyFlags[5];
		mCountryDirty = dirtyFlags[6];
		mAndroididDirty = dirtyFlags[7];
		mUrlDetailXmlDirty = dirtyFlags[8];
		mContentDetailXmlDirty = dirtyFlags[9];
		mUrlPhotoDirty = dirtyFlags[10];
		mContentPhotoDirty = dirtyFlags[11];
		mUrlDirty = dirtyFlags[12];
		mPhoneDirty = dirtyFlags[13];
		mNotesDirty = dirtyFlags[14];
		mVotesDirty = dirtyFlags[15];
		mOpenmondaysDirty = dirtyFlags[16];
		mOpentuesdaysDirty = dirtyFlags[17];
		mOpenwednesdayDirty = dirtyFlags[18];
		mOpenthursdayDirty = dirtyFlags[19];
		mOpenfridayDirty = dirtyFlags[20];
		mOpensaturdayDirty = dirtyFlags[21];
		mOpensundayDirty = dirtyFlags[22];
		mHoursmondayDirty = dirtyFlags[23];
		mHourstuesdayDirty = dirtyFlags[24];
		mHourswednesdayDirty = dirtyFlags[25];
		mHoursthursdayDirty = dirtyFlags[26];
		mHoursfridayDirty = dirtyFlags[27];
		mHourssaturdayDirty = dirtyFlags[28];
		mHourssundayDirty = dirtyFlags[29];
		mTrackaccessDirty = dirtyFlags[30];
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeLong(mRestId);
		dest.writeLong(mTrackRestId);
		dest.writeLong(mCreated);
		dest.writeString(mTrackname);
		dest.writeDouble(mLongitude);
		dest.writeDouble(mLatitude);
		dest.writeString(mCountry);
		dest.writeString(mAndroidid);
		dest.writeString(mUrlDetailXml);
		dest.writeString(mContentDetailXml);
		dest.writeString(mUrlPhoto);
		dest.writeString(mContentPhoto);
		dest.writeString(mUrl);
		dest.writeString(mPhone);
		dest.writeString(mNotes);
		dest.writeString(mVotes);
		dest.writeLong(mOpenmondays);
		dest.writeLong(mOpentuesdays);
		dest.writeLong(mOpenwednesday);
		dest.writeLong(mOpenthursday);
		dest.writeLong(mOpenfriday);
		dest.writeLong(mOpensaturday);
		dest.writeLong(mOpensunday);
		dest.writeString(mHoursmonday);
		dest.writeString(mHourstuesday);
		dest.writeString(mHourswednesday);
		dest.writeString(mHoursthursday);
		dest.writeString(mHoursfriday);
		dest.writeString(mHourssaturday);
		dest.writeString(mHourssunday);
		dest.writeString(mTrackaccess);
		dest.writeBooleanArray(new boolean[] {
			mRestIdDirty,
			mTrackRestIdDirty,
			mCreatedDirty,
			mTracknameDirty,
			mLongitudeDirty,
			mLatitudeDirty,
			mCountryDirty,
			mAndroididDirty,
			mUrlDetailXmlDirty,
			mContentDetailXmlDirty,
			mUrlPhotoDirty,
			mContentPhotoDirty,
			mUrlDirty,
			mPhoneDirty,
			mNotesDirty,
			mVotesDirty,
			mOpenmondaysDirty,
			mOpentuesdaysDirty,
			mOpenwednesdayDirty,
			mOpenthursdayDirty,
			mOpenfridayDirty,
			mOpensaturdayDirty,
			mOpensundayDirty,
			mHoursmondayDirty,
			mHourstuesdayDirty,
			mHourswednesdayDirty,
			mHoursthursdayDirty,
			mHoursfridayDirty,
			mHourssaturdayDirty,
			mHourssundayDirty,
			mTrackaccessDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = TrackstageBrother.newBuilder();

		if(mRestIdDirty) {
			builder.setRestId(mRestId);
		}
		if(mTrackRestIdDirty) {
			builder.setTrackRestId(mTrackRestId);
		}
		if(mCreatedDirty) {
			builder.setCreated(mCreated);
		}
		if(mTracknameDirty) {
			builder.setTrackname(mTrackname);
		}
		if(mLongitudeDirty) {
			builder.setLongitude(mLongitude);
		}
		if(mLatitudeDirty) {
			builder.setLatitude(mLatitude);
		}
		if(mCountryDirty) {
			builder.setCountry(mCountry);
		}
		if(mAndroididDirty) {
			builder.setAndroidid(mAndroidid);
		}
		if(mUrlDetailXmlDirty) {
			builder.setUrlDetailXml(mUrlDetailXml);
		}
		if(mContentDetailXmlDirty) {
			builder.setContentDetailXml(mContentDetailXml);
		}
		if(mUrlPhotoDirty) {
			builder.setUrlPhoto(mUrlPhoto);
		}
		if(mContentPhotoDirty) {
			builder.setContentPhoto(mContentPhoto);
		}
		if(mUrlDirty) {
			builder.setUrl(mUrl);
		}
		if(mPhoneDirty) {
			builder.setPhone(mPhone);
		}
		if(mNotesDirty) {
			builder.setNotes(mNotes);
		}
		if(mVotesDirty) {
			builder.setVotes(mVotes);
		}
		if(mOpenmondaysDirty) {
			builder.setOpenmondays(mOpenmondays);
		}
		if(mOpentuesdaysDirty) {
			builder.setOpentuesdays(mOpentuesdays);
		}
		if(mOpenwednesdayDirty) {
			builder.setOpenwednesday(mOpenwednesday);
		}
		if(mOpenthursdayDirty) {
			builder.setOpenthursday(mOpenthursday);
		}
		if(mOpenfridayDirty) {
			builder.setOpenfriday(mOpenfriday);
		}
		if(mOpensaturdayDirty) {
			builder.setOpensaturday(mOpensaturday);
		}
		if(mOpensundayDirty) {
			builder.setOpensunday(mOpensunday);
		}
		if(mHoursmondayDirty) {
			builder.setHoursmonday(mHoursmonday);
		}
		if(mHourstuesdayDirty) {
			builder.setHourstuesday(mHourstuesday);
		}
		if(mHourswednesdayDirty) {
			builder.setHourswednesday(mHourswednesday);
		}
		if(mHoursthursdayDirty) {
			builder.setHoursthursday(mHoursthursday);
		}
		if(mHoursfridayDirty) {
			builder.setHoursfriday(mHoursfriday);
		}
		if(mHourssaturdayDirty) {
			builder.setHourssaturday(mHourssaturday);
		}
		if(mHourssundayDirty) {
			builder.setHourssunday(mHourssunday);
		}
		if(mTrackaccessDirty) {
			builder.setTrackaccess(mTrackaccess);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mRestIdDirty = dirty;
		mTrackRestIdDirty = dirty;
		mCreatedDirty = dirty;
		mTracknameDirty = dirty;
		mLongitudeDirty = dirty;
		mLatitudeDirty = dirty;
		mCountryDirty = dirty;
		mAndroididDirty = dirty;
		mUrlDetailXmlDirty = dirty;
		mContentDetailXmlDirty = dirty;
		mUrlPhotoDirty = dirty;
		mContentPhotoDirty = dirty;
		mUrlDirty = dirty;
		mPhoneDirty = dirty;
		mNotesDirty = dirty;
		mVotesDirty = dirty;
		mOpenmondaysDirty = dirty;
		mOpentuesdaysDirty = dirty;
		mOpenwednesdayDirty = dirty;
		mOpenthursdayDirty = dirty;
		mOpenfridayDirty = dirty;
		mOpensaturdayDirty = dirty;
		mOpensundayDirty = dirty;
		mHoursmondayDirty = dirty;
		mHourstuesdayDirty = dirty;
		mHourswednesdayDirty = dirty;
		mHoursthursdayDirty = dirty;
		mHoursfridayDirty = dirty;
		mHourssaturdayDirty = dirty;
		mHourssundayDirty = dirty;
		mTrackaccessDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setRestId(c.getLong(Indices.REST_ID));
		setTrackRestId(c.getLong(Indices.TRACK_REST_ID));
		setCreated(c.getLong(Indices.CREATED));
		setTrackname(c.getString(Indices.TRACKNAME));
		setLongitude(c.getDouble(Indices.LONGITUDE));
		setLatitude(c.getDouble(Indices.LATITUDE));
		setCountry(c.getString(Indices.COUNTRY));
		setAndroidid(c.getString(Indices.ANDROIDID));
		setUrlDetailXml(c.getString(Indices.URL_DETAIL_XML));
		setContentDetailXml(c.getString(Indices.CONTENT_DETAIL_XML));
		setUrlPhoto(c.getString(Indices.URL_PHOTO));
		setContentPhoto(c.getString(Indices.CONTENT_PHOTO));
		setUrl(c.getString(Indices.URL));
		setPhone(c.getString(Indices.PHONE));
		setNotes(c.getString(Indices.NOTES));
		setVotes(c.getString(Indices.VOTES));
		setOpenmondays(c.getLong(Indices.OPENMONDAYS));
		setOpentuesdays(c.getLong(Indices.OPENTUESDAYS));
		setOpenwednesday(c.getLong(Indices.OPENWEDNESDAY));
		setOpenthursday(c.getLong(Indices.OPENTHURSDAY));
		setOpenfriday(c.getLong(Indices.OPENFRIDAY));
		setOpensaturday(c.getLong(Indices.OPENSATURDAY));
		setOpensunday(c.getLong(Indices.OPENSUNDAY));
		setHoursmonday(c.getString(Indices.HOURSMONDAY));
		setHourstuesday(c.getString(Indices.HOURSTUESDAY));
		setHourswednesday(c.getString(Indices.HOURSWEDNESDAY));
		setHoursthursday(c.getString(Indices.HOURSTHURSDAY));
		setHoursfriday(c.getString(Indices.HOURSFRIDAY));
		setHourssaturday(c.getString(Indices.HOURSSATURDAY));
		setHourssunday(c.getString(Indices.HOURSSUNDAY));
		setTrackaccess(c.getString(Indices.TRACKACCESS));
	}
	
	public static TrackstageBrotherRecord fromCursor(Cursor c) {
	    TrackstageBrotherRecord item = new TrackstageBrotherRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static TrackstageBrotherRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(TrackstageBrotherRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static TrackstageBrotherRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(TrackstageBrother.CONTENT_URI.buildUpon()
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
