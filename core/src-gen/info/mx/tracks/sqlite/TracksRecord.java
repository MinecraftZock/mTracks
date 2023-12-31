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
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks;
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks.Builder;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.db.ActiveRecord;
import com.robotoworks.mechanoid.db.ActiveRecordFactory;
import com.robotoworks.mechanoid.Mechanoid;
import com.robotoworks.mechanoid.db.AbstractValuesBuilder;

public class TracksRecord extends ActiveRecord implements Parcelable {

	private static ActiveRecordFactory<TracksRecord> sFactory = new ActiveRecordFactory<TracksRecord>() {
		@Override
		public TracksRecord create(Cursor c) {
			return fromCursor(c);
		}
		
		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

        @Override
                    public Uri getContentUri() {
                        return Tracks.CONTENT_URI;
                    }
                };

    			public static ActiveRecordFactory<TracksRecord> getFactory() {
		return sFactory;
	}

    public static final Parcelable.Creator<TracksRecord> CREATOR 
    	= new Parcelable.Creator<TracksRecord>() {
        public TracksRecord createFromParcel(Parcel in) {
            return new TracksRecord(in);
        }

        public TracksRecord[] newArray(int size) {
            return new TracksRecord[size];
        }
    };
    
    public static String[] PROJECTION = {
    	Tracks._ID,
    	Tracks.REST_ID,
    	Tracks.CHANGED,
    	Tracks.TRACKNAME,
    	Tracks.LONGITUDE,
    	Tracks.LATITUDE,
    	Tracks.APPROVED,
    	Tracks.COUNTRY,
    	Tracks.URL,
    	Tracks.FEES,
    	Tracks.PHONE,
    	Tracks.NOTES,
    	Tracks.CONTACT,
    	Tracks.NOTES_EN,
    	Tracks.METATEXT,
    	Tracks.LICENCE,
    	Tracks.KIDSTRACK,
    	Tracks.OPENMONDAYS,
    	Tracks.OPENTUESDAYS,
    	Tracks.OPENWEDNESDAY,
    	Tracks.OPENTHURSDAY,
    	Tracks.OPENFRIDAY,
    	Tracks.OPENSATURDAY,
    	Tracks.OPENSUNDAY,
    	Tracks.HOURSMONDAY,
    	Tracks.HOURSTUESDAY,
    	Tracks.HOURSWEDNESDAY,
    	Tracks.HOURSTHURSDAY,
    	Tracks.HOURSFRIDAY,
    	Tracks.HOURSSATURDAY,
    	Tracks.HOURSSUNDAY,
    	Tracks.TRACKLENGTH,
    	Tracks.SOILTYPE,
    	Tracks.CAMPING,
    	Tracks.SHOWER,
    	Tracks.CLEANING,
    	Tracks.ELECTRICITY,
    	Tracks.DISTANCE2LOCATION,
    	Tracks.SUPERCROSS,
    	Tracks.TRACKACCESS,
    	Tracks.LOGO_U_R_L,
    	Tracks.SHOWROOM,
    	Tracks.WORKSHOP,
    	Tracks.VALIDUNTIL,
    	Tracks.BRANDS,
    	Tracks.NU_EVENTS,
    	Tracks.FACEBOOK,
    	Tracks.ADRESS,
    	Tracks.FEESCAMPING,
    	Tracks.DAYSOPEN,
    	Tracks.NOISELIMIT,
    	Tracks.CAMPINGRVRVHOOKUP,
    	Tracks.SINGLETRACKS,
    	Tracks.MXTRACK,
    	Tracks.A4X4,
    	Tracks.UTV,
    	Tracks.QUAD,
    	Tracks.TRACKSTATUS,
    	Tracks.AREATYPE,
    	Tracks.SCHWIERIGKEIT,
    	Tracks.LAST_ASKED,
    	Tracks.INDOOR,
    	Tracks.ENDURO
    };
    
    public interface Indices {
    	int _ID = 0;
    	int REST_ID = 1;
    	int CHANGED = 2;
    	int TRACKNAME = 3;
    	int LONGITUDE = 4;
    	int LATITUDE = 5;
    	int APPROVED = 6;
    	int COUNTRY = 7;
    	int URL = 8;
    	int FEES = 9;
    	int PHONE = 10;
    	int NOTES = 11;
    	int CONTACT = 12;
    	int NOTES_EN = 13;
    	int METATEXT = 14;
    	int LICENCE = 15;
    	int KIDSTRACK = 16;
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
    	int TRACKLENGTH = 31;
    	int SOILTYPE = 32;
    	int CAMPING = 33;
    	int SHOWER = 34;
    	int CLEANING = 35;
    	int ELECTRICITY = 36;
    	int DISTANCE2LOCATION = 37;
    	int SUPERCROSS = 38;
    	int TRACKACCESS = 39;
    	int LOGO_U_R_L = 40;
    	int SHOWROOM = 41;
    	int WORKSHOP = 42;
    	int VALIDUNTIL = 43;
    	int BRANDS = 44;
    	int NU_EVENTS = 45;
    	int FACEBOOK = 46;
    	int ADRESS = 47;
    	int FEESCAMPING = 48;
    	int DAYSOPEN = 49;
    	int NOISELIMIT = 50;
    	int CAMPINGRVRVHOOKUP = 51;
    	int SINGLETRACKS = 52;
    	int MXTRACK = 53;
    	int A4X4 = 54;
    	int UTV = 55;
    	int QUAD = 56;
    	int TRACKSTATUS = 57;
    	int AREATYPE = 58;
    	int SCHWIERIGKEIT = 59;
    	int LAST_ASKED = 60;
    	int INDOOR = 61;
    	int ENDURO = 62;
    }
    
    private long mRestId;
    private boolean mRestIdDirty;
    private long mChanged;
    private boolean mChangedDirty;
    private String mTrackname;
    private boolean mTracknameDirty;
    private double mLongitude;
    private boolean mLongitudeDirty;
    private double mLatitude;
    private boolean mLatitudeDirty;
    private long mApproved;
    private boolean mApprovedDirty;
    private String mCountry;
    private boolean mCountryDirty;
    private String mUrl;
    private boolean mUrlDirty;
    private String mFees;
    private boolean mFeesDirty;
    private String mPhone;
    private boolean mPhoneDirty;
    private String mNotes;
    private boolean mNotesDirty;
    private String mContact;
    private boolean mContactDirty;
    private String mNotesEn;
    private boolean mNotesEnDirty;
    private String mMetatext;
    private boolean mMetatextDirty;
    private String mLicence;
    private boolean mLicenceDirty;
    private long mKidstrack;
    private boolean mKidstrackDirty;
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
    private long mTracklength;
    private boolean mTracklengthDirty;
    private long mSoiltype;
    private boolean mSoiltypeDirty;
    private long mCamping;
    private boolean mCampingDirty;
    private long mShower;
    private boolean mShowerDirty;
    private long mCleaning;
    private boolean mCleaningDirty;
    private long mElectricity;
    private boolean mElectricityDirty;
    private long mDistance2location;
    private boolean mDistance2locationDirty;
    private long mSupercross;
    private boolean mSupercrossDirty;
    private String mTrackaccess;
    private boolean mTrackaccessDirty;
    private String mLogoURL;
    private boolean mLogoURLDirty;
    private long mShowroom;
    private boolean mShowroomDirty;
    private long mWorkshop;
    private boolean mWorkshopDirty;
    private long mValiduntil;
    private boolean mValiduntilDirty;
    private String mBrands;
    private boolean mBrandsDirty;
    private String mNuEvents;
    private boolean mNuEventsDirty;
    private String mFacebook;
    private boolean mFacebookDirty;
    private String mAdress;
    private boolean mAdressDirty;
    private String mFeescamping;
    private boolean mFeescampingDirty;
    private String mDaysopen;
    private boolean mDaysopenDirty;
    private String mNoiselimit;
    private boolean mNoiselimitDirty;
    private long mCampingrvrvhookup;
    private boolean mCampingrvrvhookupDirty;
    private long mSingletracks;
    private boolean mSingletracksDirty;
    private long mMxtrack;
    private boolean mMxtrackDirty;
    private long mA4x4;
    private boolean mA4x4Dirty;
    private long mUtv;
    private boolean mUtvDirty;
    private long mQuad;
    private boolean mQuadDirty;
    private String mTrackstatus;
    private boolean mTrackstatusDirty;
    private String mAreatype;
    private boolean mAreatypeDirty;
    private long mSchwierigkeit;
    private boolean mSchwierigkeitDirty;
    private long mLastAsked;
    private boolean mLastAskedDirty;
    private long mIndoor;
    private boolean mIndoorDirty;
    private long mEnduro;
    private boolean mEnduroDirty;
    
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
    
    public void setApproved(long approved) {
    	mApproved = approved;
    	mApprovedDirty = true;
    }
    
    public long getApproved() {
    	return mApproved;
    }
    
    public void setCountry(String country) {
    	mCountry = country;
    	mCountryDirty = true;
    }
    
    public String getCountry() {
    	return mCountry;
    }
    
    public void setUrl(String url) {
    	mUrl = url;
    	mUrlDirty = true;
    }
    
    public String getUrl() {
    	return mUrl;
    }
    
    public void setFees(String fees) {
    	mFees = fees;
    	mFeesDirty = true;
    }
    
    public String getFees() {
    	return mFees;
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
    
    public void setContact(String contact) {
    	mContact = contact;
    	mContactDirty = true;
    }
    
    public String getContact() {
    	return mContact;
    }
    
    public void setNotesEn(String notesEn) {
    	mNotesEn = notesEn;
    	mNotesEnDirty = true;
    }
    
    public String getNotesEn() {
    	return mNotesEn;
    }
    
    public void setMetatext(String metatext) {
    	mMetatext = metatext;
    	mMetatextDirty = true;
    }
    
    public String getMetatext() {
    	return mMetatext;
    }
    
    public void setLicence(String licence) {
    	mLicence = licence;
    	mLicenceDirty = true;
    }
    
    public String getLicence() {
    	return mLicence;
    }
    
    public void setKidstrack(long kidstrack) {
    	mKidstrack = kidstrack;
    	mKidstrackDirty = true;
    }
    
    public long getKidstrack() {
    	return mKidstrack;
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
    
    public void setTracklength(long tracklength) {
    	mTracklength = tracklength;
    	mTracklengthDirty = true;
    }
    
    public long getTracklength() {
    	return mTracklength;
    }
    
    public void setSoiltype(long soiltype) {
    	mSoiltype = soiltype;
    	mSoiltypeDirty = true;
    }
    
    public long getSoiltype() {
    	return mSoiltype;
    }
    
    public void setCamping(long camping) {
    	mCamping = camping;
    	mCampingDirty = true;
    }
    
    public long getCamping() {
    	return mCamping;
    }
    
    public void setShower(long shower) {
    	mShower = shower;
    	mShowerDirty = true;
    }
    
    public long getShower() {
    	return mShower;
    }
    
    public void setCleaning(long cleaning) {
    	mCleaning = cleaning;
    	mCleaningDirty = true;
    }
    
    public long getCleaning() {
    	return mCleaning;
    }
    
    public void setElectricity(long electricity) {
    	mElectricity = electricity;
    	mElectricityDirty = true;
    }
    
    public long getElectricity() {
    	return mElectricity;
    }
    
    public void setDistance2location(long distance2location) {
    	mDistance2location = distance2location;
    	mDistance2locationDirty = true;
    }
    
    public long getDistance2location() {
    	return mDistance2location;
    }
    
    public void setSupercross(long supercross) {
    	mSupercross = supercross;
    	mSupercrossDirty = true;
    }
    
    public long getSupercross() {
    	return mSupercross;
    }
    
    public void setTrackaccess(String trackaccess) {
    	mTrackaccess = trackaccess;
    	mTrackaccessDirty = true;
    }
    
    public String getTrackaccess() {
    	return mTrackaccess;
    }
    
    public void setLogoURL(String logoURL) {
    	mLogoURL = logoURL;
    	mLogoURLDirty = true;
    }
    
    public String getLogoURL() {
    	return mLogoURL;
    }
    
    public void setShowroom(long showroom) {
    	mShowroom = showroom;
    	mShowroomDirty = true;
    }
    
    public long getShowroom() {
    	return mShowroom;
    }
    
    public void setWorkshop(long workshop) {
    	mWorkshop = workshop;
    	mWorkshopDirty = true;
    }
    
    public long getWorkshop() {
    	return mWorkshop;
    }
    
    public void setValiduntil(long validuntil) {
    	mValiduntil = validuntil;
    	mValiduntilDirty = true;
    }
    
    public long getValiduntil() {
    	return mValiduntil;
    }
    
    public void setBrands(String brands) {
    	mBrands = brands;
    	mBrandsDirty = true;
    }
    
    public String getBrands() {
    	return mBrands;
    }
    
    public void setNuEvents(String nuEvents) {
    	mNuEvents = nuEvents;
    	mNuEventsDirty = true;
    }
    
    public String getNuEvents() {
    	return mNuEvents;
    }
    
    public void setFacebook(String facebook) {
    	mFacebook = facebook;
    	mFacebookDirty = true;
    }
    
    public String getFacebook() {
    	return mFacebook;
    }
    
    public void setAdress(String adress) {
    	mAdress = adress;
    	mAdressDirty = true;
    }
    
    public String getAdress() {
    	return mAdress;
    }
    
    public void setFeescamping(String feescamping) {
    	mFeescamping = feescamping;
    	mFeescampingDirty = true;
    }
    
    public String getFeescamping() {
    	return mFeescamping;
    }
    
    public void setDaysopen(String daysopen) {
    	mDaysopen = daysopen;
    	mDaysopenDirty = true;
    }
    
    public String getDaysopen() {
    	return mDaysopen;
    }
    
    public void setNoiselimit(String noiselimit) {
    	mNoiselimit = noiselimit;
    	mNoiselimitDirty = true;
    }
    
    public String getNoiselimit() {
    	return mNoiselimit;
    }
    
    public void setCampingrvrvhookup(long campingrvrvhookup) {
    	mCampingrvrvhookup = campingrvrvhookup;
    	mCampingrvrvhookupDirty = true;
    }
    
    public long getCampingrvrvhookup() {
    	return mCampingrvrvhookup;
    }
    
    public void setSingletracks(long singletracks) {
    	mSingletracks = singletracks;
    	mSingletracksDirty = true;
    }
    
    public long getSingletracks() {
    	return mSingletracks;
    }
    
    public void setMxtrack(long mxtrack) {
    	mMxtrack = mxtrack;
    	mMxtrackDirty = true;
    }
    
    public long getMxtrack() {
    	return mMxtrack;
    }
    
    public void setA4x4(long a4x4) {
    	mA4x4 = a4x4;
    	mA4x4Dirty = true;
    }
    
    public long getA4x4() {
    	return mA4x4;
    }
    
    public void setUtv(long utv) {
    	mUtv = utv;
    	mUtvDirty = true;
    }
    
    public long getUtv() {
    	return mUtv;
    }
    
    public void setQuad(long quad) {
    	mQuad = quad;
    	mQuadDirty = true;
    }
    
    public long getQuad() {
    	return mQuad;
    }
    
    public void setTrackstatus(String trackstatus) {
    	mTrackstatus = trackstatus;
    	mTrackstatusDirty = true;
    }
    
    public String getTrackstatus() {
    	return mTrackstatus;
    }
    
    public void setAreatype(String areatype) {
    	mAreatype = areatype;
    	mAreatypeDirty = true;
    }
    
    public String getAreatype() {
    	return mAreatype;
    }
    
    public void setSchwierigkeit(long schwierigkeit) {
    	mSchwierigkeit = schwierigkeit;
    	mSchwierigkeitDirty = true;
    }
    
    public long getSchwierigkeit() {
    	return mSchwierigkeit;
    }
    
    public void setLastAsked(long lastAsked) {
    	mLastAsked = lastAsked;
    	mLastAskedDirty = true;
    }
    
    public long getLastAsked() {
    	return mLastAsked;
    }
    
    public void setIndoor(long indoor) {
    	mIndoor = indoor;
    	mIndoorDirty = true;
    }
    
    public long getIndoor() {
    	return mIndoor;
    }
    
    public void setEnduro(long enduro) {
    	mEnduro = enduro;
    	mEnduroDirty = true;
    }
    
    public long getEnduro() {
    	return mEnduro;
    }
    
    
    public TracksRecord() {
    	super(Tracks.CONTENT_URI);
	}
	
	private TracksRecord(Parcel in) {
    	super(Tracks.CONTENT_URI);
    	
		setId(in.readLong());
		
		mRestId = in.readLong();
		mChanged = in.readLong();
		mTrackname = in.readString();
		mLongitude = in.readDouble();
		mLatitude = in.readDouble();
		mApproved = in.readLong();
		mCountry = in.readString();
		mUrl = in.readString();
		mFees = in.readString();
		mPhone = in.readString();
		mNotes = in.readString();
		mContact = in.readString();
		mNotesEn = in.readString();
		mMetatext = in.readString();
		mLicence = in.readString();
		mKidstrack = in.readLong();
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
		mTracklength = in.readLong();
		mSoiltype = in.readLong();
		mCamping = in.readLong();
		mShower = in.readLong();
		mCleaning = in.readLong();
		mElectricity = in.readLong();
		mDistance2location = in.readLong();
		mSupercross = in.readLong();
		mTrackaccess = in.readString();
		mLogoURL = in.readString();
		mShowroom = in.readLong();
		mWorkshop = in.readLong();
		mValiduntil = in.readLong();
		mBrands = in.readString();
		mNuEvents = in.readString();
		mFacebook = in.readString();
		mAdress = in.readString();
		mFeescamping = in.readString();
		mDaysopen = in.readString();
		mNoiselimit = in.readString();
		mCampingrvrvhookup = in.readLong();
		mSingletracks = in.readLong();
		mMxtrack = in.readLong();
		mA4x4 = in.readLong();
		mUtv = in.readLong();
		mQuad = in.readLong();
		mTrackstatus = in.readString();
		mAreatype = in.readString();
		mSchwierigkeit = in.readLong();
		mLastAsked = in.readLong();
		mIndoor = in.readLong();
		mEnduro = in.readLong();
		
		boolean[] dirtyFlags = new boolean[62];
		in.readBooleanArray(dirtyFlags);
		mRestIdDirty = dirtyFlags[0];
		mChangedDirty = dirtyFlags[1];
		mTracknameDirty = dirtyFlags[2];
		mLongitudeDirty = dirtyFlags[3];
		mLatitudeDirty = dirtyFlags[4];
		mApprovedDirty = dirtyFlags[5];
		mCountryDirty = dirtyFlags[6];
		mUrlDirty = dirtyFlags[7];
		mFeesDirty = dirtyFlags[8];
		mPhoneDirty = dirtyFlags[9];
		mNotesDirty = dirtyFlags[10];
		mContactDirty = dirtyFlags[11];
		mNotesEnDirty = dirtyFlags[12];
		mMetatextDirty = dirtyFlags[13];
		mLicenceDirty = dirtyFlags[14];
		mKidstrackDirty = dirtyFlags[15];
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
		mTracklengthDirty = dirtyFlags[30];
		mSoiltypeDirty = dirtyFlags[31];
		mCampingDirty = dirtyFlags[32];
		mShowerDirty = dirtyFlags[33];
		mCleaningDirty = dirtyFlags[34];
		mElectricityDirty = dirtyFlags[35];
		mDistance2locationDirty = dirtyFlags[36];
		mSupercrossDirty = dirtyFlags[37];
		mTrackaccessDirty = dirtyFlags[38];
		mLogoURLDirty = dirtyFlags[39];
		mShowroomDirty = dirtyFlags[40];
		mWorkshopDirty = dirtyFlags[41];
		mValiduntilDirty = dirtyFlags[42];
		mBrandsDirty = dirtyFlags[43];
		mNuEventsDirty = dirtyFlags[44];
		mFacebookDirty = dirtyFlags[45];
		mAdressDirty = dirtyFlags[46];
		mFeescampingDirty = dirtyFlags[47];
		mDaysopenDirty = dirtyFlags[48];
		mNoiselimitDirty = dirtyFlags[49];
		mCampingrvrvhookupDirty = dirtyFlags[50];
		mSingletracksDirty = dirtyFlags[51];
		mMxtrackDirty = dirtyFlags[52];
		mA4x4Dirty = dirtyFlags[53];
		mUtvDirty = dirtyFlags[54];
		mQuadDirty = dirtyFlags[55];
		mTrackstatusDirty = dirtyFlags[56];
		mAreatypeDirty = dirtyFlags[57];
		mSchwierigkeitDirty = dirtyFlags[58];
		mLastAskedDirty = dirtyFlags[59];
		mIndoorDirty = dirtyFlags[60];
		mEnduroDirty = dirtyFlags[61];
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
		dest.writeString(mTrackname);
		dest.writeDouble(mLongitude);
		dest.writeDouble(mLatitude);
		dest.writeLong(mApproved);
		dest.writeString(mCountry);
		dest.writeString(mUrl);
		dest.writeString(mFees);
		dest.writeString(mPhone);
		dest.writeString(mNotes);
		dest.writeString(mContact);
		dest.writeString(mNotesEn);
		dest.writeString(mMetatext);
		dest.writeString(mLicence);
		dest.writeLong(mKidstrack);
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
		dest.writeLong(mTracklength);
		dest.writeLong(mSoiltype);
		dest.writeLong(mCamping);
		dest.writeLong(mShower);
		dest.writeLong(mCleaning);
		dest.writeLong(mElectricity);
		dest.writeLong(mDistance2location);
		dest.writeLong(mSupercross);
		dest.writeString(mTrackaccess);
		dest.writeString(mLogoURL);
		dest.writeLong(mShowroom);
		dest.writeLong(mWorkshop);
		dest.writeLong(mValiduntil);
		dest.writeString(mBrands);
		dest.writeString(mNuEvents);
		dest.writeString(mFacebook);
		dest.writeString(mAdress);
		dest.writeString(mFeescamping);
		dest.writeString(mDaysopen);
		dest.writeString(mNoiselimit);
		dest.writeLong(mCampingrvrvhookup);
		dest.writeLong(mSingletracks);
		dest.writeLong(mMxtrack);
		dest.writeLong(mA4x4);
		dest.writeLong(mUtv);
		dest.writeLong(mQuad);
		dest.writeString(mTrackstatus);
		dest.writeString(mAreatype);
		dest.writeLong(mSchwierigkeit);
		dest.writeLong(mLastAsked);
		dest.writeLong(mIndoor);
		dest.writeLong(mEnduro);
		dest.writeBooleanArray(new boolean[] {
			mRestIdDirty,
			mChangedDirty,
			mTracknameDirty,
			mLongitudeDirty,
			mLatitudeDirty,
			mApprovedDirty,
			mCountryDirty,
			mUrlDirty,
			mFeesDirty,
			mPhoneDirty,
			mNotesDirty,
			mContactDirty,
			mNotesEnDirty,
			mMetatextDirty,
			mLicenceDirty,
			mKidstrackDirty,
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
			mTracklengthDirty,
			mSoiltypeDirty,
			mCampingDirty,
			mShowerDirty,
			mCleaningDirty,
			mElectricityDirty,
			mDistance2locationDirty,
			mSupercrossDirty,
			mTrackaccessDirty,
			mLogoURLDirty,
			mShowroomDirty,
			mWorkshopDirty,
			mValiduntilDirty,
			mBrandsDirty,
			mNuEventsDirty,
			mFacebookDirty,
			mAdressDirty,
			mFeescampingDirty,
			mDaysopenDirty,
			mNoiselimitDirty,
			mCampingrvrvhookupDirty,
			mSingletracksDirty,
			mMxtrackDirty,
			mA4x4Dirty,
			mUtvDirty,
			mQuadDirty,
			mTrackstatusDirty,
			mAreatypeDirty,
			mSchwierigkeitDirty,
			mLastAskedDirty,
			mIndoorDirty,
			mEnduroDirty
		});
	}
	
	@Override
	protected AbstractValuesBuilder createBuilder() {
		Builder builder = Tracks.newBuilder();

		if(mRestIdDirty) {
			builder.setRestId(mRestId);
		}
		if(mChangedDirty) {
			builder.setChanged(mChanged);
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
		if(mApprovedDirty) {
			builder.setApproved(mApproved);
		}
		if(mCountryDirty) {
			builder.setCountry(mCountry);
		}
		if(mUrlDirty) {
			builder.setUrl(mUrl);
		}
		if(mFeesDirty) {
			builder.setFees(mFees);
		}
		if(mPhoneDirty) {
			builder.setPhone(mPhone);
		}
		if(mNotesDirty) {
			builder.setNotes(mNotes);
		}
		if(mContactDirty) {
			builder.setContact(mContact);
		}
		if(mNotesEnDirty) {
			builder.setNotesEn(mNotesEn);
		}
		if(mMetatextDirty) {
			builder.setMetatext(mMetatext);
		}
		if(mLicenceDirty) {
			builder.setLicence(mLicence);
		}
		if(mKidstrackDirty) {
			builder.setKidstrack(mKidstrack);
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
		if(mTracklengthDirty) {
			builder.setTracklength(mTracklength);
		}
		if(mSoiltypeDirty) {
			builder.setSoiltype(mSoiltype);
		}
		if(mCampingDirty) {
			builder.setCamping(mCamping);
		}
		if(mShowerDirty) {
			builder.setShower(mShower);
		}
		if(mCleaningDirty) {
			builder.setCleaning(mCleaning);
		}
		if(mElectricityDirty) {
			builder.setElectricity(mElectricity);
		}
		if(mDistance2locationDirty) {
			builder.setDistance2location(mDistance2location);
		}
		if(mSupercrossDirty) {
			builder.setSupercross(mSupercross);
		}
		if(mTrackaccessDirty) {
			builder.setTrackaccess(mTrackaccess);
		}
		if(mLogoURLDirty) {
			builder.setLogoURL(mLogoURL);
		}
		if(mShowroomDirty) {
			builder.setShowroom(mShowroom);
		}
		if(mWorkshopDirty) {
			builder.setWorkshop(mWorkshop);
		}
		if(mValiduntilDirty) {
			builder.setValiduntil(mValiduntil);
		}
		if(mBrandsDirty) {
			builder.setBrands(mBrands);
		}
		if(mNuEventsDirty) {
			builder.setNuEvents(mNuEvents);
		}
		if(mFacebookDirty) {
			builder.setFacebook(mFacebook);
		}
		if(mAdressDirty) {
			builder.setAdress(mAdress);
		}
		if(mFeescampingDirty) {
			builder.setFeescamping(mFeescamping);
		}
		if(mDaysopenDirty) {
			builder.setDaysopen(mDaysopen);
		}
		if(mNoiselimitDirty) {
			builder.setNoiselimit(mNoiselimit);
		}
		if(mCampingrvrvhookupDirty) {
			builder.setCampingrvrvhookup(mCampingrvrvhookup);
		}
		if(mSingletracksDirty) {
			builder.setSingletracks(mSingletracks);
		}
		if(mMxtrackDirty) {
			builder.setMxtrack(mMxtrack);
		}
		if(mA4x4Dirty) {
			builder.setA4x4(mA4x4);
		}
		if(mUtvDirty) {
			builder.setUtv(mUtv);
		}
		if(mQuadDirty) {
			builder.setQuad(mQuad);
		}
		if(mTrackstatusDirty) {
			builder.setTrackstatus(mTrackstatus);
		}
		if(mAreatypeDirty) {
			builder.setAreatype(mAreatype);
		}
		if(mSchwierigkeitDirty) {
			builder.setSchwierigkeit(mSchwierigkeit);
		}
		if(mLastAskedDirty) {
			builder.setLastAsked(mLastAsked);
		}
		if(mIndoorDirty) {
			builder.setIndoor(mIndoor);
		}
		if(mEnduroDirty) {
			builder.setEnduro(mEnduro);
		}
		
		return builder;
	}
	
    @Override
	public void makeDirty(boolean dirty){
		mRestIdDirty = dirty;
		mChangedDirty = dirty;
		mTracknameDirty = dirty;
		mLongitudeDirty = dirty;
		mLatitudeDirty = dirty;
		mApprovedDirty = dirty;
		mCountryDirty = dirty;
		mUrlDirty = dirty;
		mFeesDirty = dirty;
		mPhoneDirty = dirty;
		mNotesDirty = dirty;
		mContactDirty = dirty;
		mNotesEnDirty = dirty;
		mMetatextDirty = dirty;
		mLicenceDirty = dirty;
		mKidstrackDirty = dirty;
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
		mTracklengthDirty = dirty;
		mSoiltypeDirty = dirty;
		mCampingDirty = dirty;
		mShowerDirty = dirty;
		mCleaningDirty = dirty;
		mElectricityDirty = dirty;
		mDistance2locationDirty = dirty;
		mSupercrossDirty = dirty;
		mTrackaccessDirty = dirty;
		mLogoURLDirty = dirty;
		mShowroomDirty = dirty;
		mWorkshopDirty = dirty;
		mValiduntilDirty = dirty;
		mBrandsDirty = dirty;
		mNuEventsDirty = dirty;
		mFacebookDirty = dirty;
		mAdressDirty = dirty;
		mFeescampingDirty = dirty;
		mDaysopenDirty = dirty;
		mNoiselimitDirty = dirty;
		mCampingrvrvhookupDirty = dirty;
		mSingletracksDirty = dirty;
		mMxtrackDirty = dirty;
		mA4x4Dirty = dirty;
		mUtvDirty = dirty;
		mQuadDirty = dirty;
		mTrackstatusDirty = dirty;
		mAreatypeDirty = dirty;
		mSchwierigkeitDirty = dirty;
		mLastAskedDirty = dirty;
		mIndoorDirty = dirty;
		mEnduroDirty = dirty;
	}

	@Override
	protected void setPropertiesFromCursor(Cursor c) {
		setId(c.getLong(Indices._ID));
		setRestId(c.getLong(Indices.REST_ID));
		setChanged(c.getLong(Indices.CHANGED));
		setTrackname(c.getString(Indices.TRACKNAME));
		setLongitude(c.getDouble(Indices.LONGITUDE));
		setLatitude(c.getDouble(Indices.LATITUDE));
		setApproved(c.getLong(Indices.APPROVED));
		setCountry(c.getString(Indices.COUNTRY));
		setUrl(c.getString(Indices.URL));
		setFees(c.getString(Indices.FEES));
		setPhone(c.getString(Indices.PHONE));
		setNotes(c.getString(Indices.NOTES));
		setContact(c.getString(Indices.CONTACT));
		setNotesEn(c.getString(Indices.NOTES_EN));
		setMetatext(c.getString(Indices.METATEXT));
		setLicence(c.getString(Indices.LICENCE));
		setKidstrack(c.getLong(Indices.KIDSTRACK));
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
		setTracklength(c.getLong(Indices.TRACKLENGTH));
		setSoiltype(c.getLong(Indices.SOILTYPE));
		setCamping(c.getLong(Indices.CAMPING));
		setShower(c.getLong(Indices.SHOWER));
		setCleaning(c.getLong(Indices.CLEANING));
		setElectricity(c.getLong(Indices.ELECTRICITY));
		setDistance2location(c.getLong(Indices.DISTANCE2LOCATION));
		setSupercross(c.getLong(Indices.SUPERCROSS));
		setTrackaccess(c.getString(Indices.TRACKACCESS));
		setLogoURL(c.getString(Indices.LOGO_U_R_L));
		setShowroom(c.getLong(Indices.SHOWROOM));
		setWorkshop(c.getLong(Indices.WORKSHOP));
		setValiduntil(c.getLong(Indices.VALIDUNTIL));
		setBrands(c.getString(Indices.BRANDS));
		setNuEvents(c.getString(Indices.NU_EVENTS));
		setFacebook(c.getString(Indices.FACEBOOK));
		setAdress(c.getString(Indices.ADRESS));
		setFeescamping(c.getString(Indices.FEESCAMPING));
		setDaysopen(c.getString(Indices.DAYSOPEN));
		setNoiselimit(c.getString(Indices.NOISELIMIT));
		setCampingrvrvhookup(c.getLong(Indices.CAMPINGRVRVHOOKUP));
		setSingletracks(c.getLong(Indices.SINGLETRACKS));
		setMxtrack(c.getLong(Indices.MXTRACK));
		setA4x4(c.getLong(Indices.A4X4));
		setUtv(c.getLong(Indices.UTV));
		setQuad(c.getLong(Indices.QUAD));
		setTrackstatus(c.getString(Indices.TRACKSTATUS));
		setAreatype(c.getString(Indices.AREATYPE));
		setSchwierigkeit(c.getLong(Indices.SCHWIERIGKEIT));
		setLastAsked(c.getLong(Indices.LAST_ASKED));
		setIndoor(c.getLong(Indices.INDOOR));
		setEnduro(c.getLong(Indices.ENDURO));
	}
	
	public static TracksRecord fromCursor(Cursor c) {
	    TracksRecord item = new TracksRecord();
	    
		item.setPropertiesFromCursor(c);
		
		item.makeDirty(false);
		
	    return item;
	}
	
	public static TracksRecord fromBundle(Bundle bundle, String key) {
		bundle.setClassLoader(TracksRecord.class.getClassLoader());
		return bundle.getParcelable(key);
	}
	
	public static TracksRecord get(long id) {
	    Cursor c = null;
	    
	    ContentResolver resolver = Mechanoid.getContentResolver();
	    
	    try {
	        c = resolver.query(Tracks.CONTENT_URI.buildUpon()
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
