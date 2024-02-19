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

import info.hannes.mechadmin_gen.sqlite.MxCalContract.MXSerie;
import info.hannes.mechadmin_gen.sqlite.MxCalContract.MXSerie.Builder;

public class MXSerieRecord extends ActiveRecord implements Parcelable {

    private static final ActiveRecordFactory<MXSerieRecord> sFactory = new ActiveRecordFactory<>() {
        @Override
        public MXSerieRecord create(Cursor c) {
            return fromCursor(c);
        }

        @Override
        public Uri getContentUri() {
            return MXSerie.CONTENT_URI;
        }

        @Override
        public String[] getProjection() {
            return PROJECTION;
        }
    };

    public static ActiveRecordFactory<MXSerieRecord> getFactory() {
        return sFactory;
    }

    public static final Creator<MXSerieRecord> CREATOR
            = new Creator<>() {
        public MXSerieRecord createFromParcel(Parcel in) {
            return new MXSerieRecord(in);
        }

        public MXSerieRecord[] newArray(int size) {
            return new MXSerieRecord[size];
        }
    };

    public static String[] PROJECTION = {
            MXSerie._ID,
            MXSerie.REST_ID,
            MXSerie.WEB_ID,
            MXSerie.CREATED_AT,
            MXSerie.UPDATED_AT,
            MXSerie.NAME,
            MXSerie.SERIES_URL,
            MXSerie.YEAR,
            MXSerie.QUELLFILE_ID
    };

    public interface Indices {
        int _ID = 0;
        int REST_ID = 1;
        int WEB_ID = 2;
        int CREATED_AT = 3;
        int UPDATED_AT = 4;
        int NAME = 5;
        int SERIES_URL = 6;
        int YEAR = 7;
        int QUELLFILE_ID = 8;
    }

    private long mRestId;
    private boolean mRestIdDirty;
    private long mWebId;
    private boolean mWebIdDirty;
    private long mCreatedAt;
    private boolean mCreatedAtDirty;
    private long mUpdatedAt;
    private boolean mUpdatedAtDirty;
    private String mName;
    private boolean mNameDirty;
    private String mSeriesUrl;
    private boolean mSeriesUrlDirty;
    private long mYear;
    private boolean mYearDirty;
    private long mQuellfileId;
    private boolean mQuellfileIdDirty;

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

    public void setWebId(long webId) {
        mWebId = webId;
        mWebIdDirty = true;
    }

    public long getWebId() {
        return mWebId;
    }

    public void setCreatedAt(long createdAt) {
        mCreatedAt = createdAt;
        mCreatedAtDirty = true;
    }

    public long getCreatedAt() {
        return mCreatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        mUpdatedAt = updatedAt;
        mUpdatedAtDirty = true;
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setName(String name) {
        mName = name;
        mNameDirty = true;
    }

    public String getName() {
        return mName;
    }

    public void setSeriesUrl(String seriesUrl) {
        mSeriesUrl = seriesUrl;
        mSeriesUrlDirty = true;
    }

    public String getSeriesUrl() {
        return mSeriesUrl;
    }

    public void setYear(long year) {
        mYear = year;
        mYearDirty = true;
    }

    public long getYear() {
        return mYear;
    }

    public void setQuellfileId(long quellfileId) {
        mQuellfileId = quellfileId;
        mQuellfileIdDirty = true;
    }

    public long getQuellfileId() {
        return mQuellfileId;
    }


    public MXSerieRecord() {
        super(MXSerie.CONTENT_URI);
    }

    private MXSerieRecord(Parcel in) {
        super(MXSerie.CONTENT_URI);

        setId(in.readLong());

        mRestId = in.readLong();
        mWebId = in.readLong();
        mCreatedAt = in.readLong();
        mUpdatedAt = in.readLong();
        mName = in.readString();
        mSeriesUrl = in.readString();
        mYear = in.readLong();
        mQuellfileId = in.readLong();

        boolean[] dirtyFlags = new boolean[8];
        in.readBooleanArray(dirtyFlags);
        mRestIdDirty = dirtyFlags[0];
        mWebIdDirty = dirtyFlags[1];
        mCreatedAtDirty = dirtyFlags[2];
        mUpdatedAtDirty = dirtyFlags[3];
        mNameDirty = dirtyFlags[4];
        mSeriesUrlDirty = dirtyFlags[5];
        mYearDirty = dirtyFlags[6];
        mQuellfileIdDirty = dirtyFlags[7];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeLong(mRestId);
        dest.writeLong(mWebId);
        dest.writeLong(mCreatedAt);
        dest.writeLong(mUpdatedAt);
        dest.writeString(mName);
        dest.writeString(mSeriesUrl);
        dest.writeLong(mYear);
        dest.writeLong(mQuellfileId);
        dest.writeBooleanArray(new boolean[]{
                mRestIdDirty,
                mWebIdDirty,
                mCreatedAtDirty,
                mUpdatedAtDirty,
                mNameDirty,
                mSeriesUrlDirty,
                mYearDirty,
                mQuellfileIdDirty
        });
    }

    @Override
    protected AbstractValuesBuilder createBuilder() {
        Builder builder = MXSerie.newBuilder();

        if (mRestIdDirty) {
            builder.setRestId(mRestId);
        }
        if (mWebIdDirty) {
            builder.setWebId(mWebId);
        }
        if (mCreatedAtDirty) {
            builder.setCreatedAt(mCreatedAt);
        }
        if (mUpdatedAtDirty) {
            builder.setUpdatedAt(mUpdatedAt);
        }
        if (mNameDirty) {
            builder.setName(mName);
        }
        if (mSeriesUrlDirty) {
            builder.setSeriesUrl(mSeriesUrl);
        }
        if (mYearDirty) {
            builder.setYear(mYear);
        }
        if (mQuellfileIdDirty) {
            builder.setQuellfileId(mQuellfileId);
        }

        return builder;
    }

    @Override
    public void makeDirty(boolean dirty) {
        mRestIdDirty = dirty;
        mWebIdDirty = dirty;
        mCreatedAtDirty = dirty;
        mUpdatedAtDirty = dirty;
        mNameDirty = dirty;
        mSeriesUrlDirty = dirty;
        mYearDirty = dirty;
        mQuellfileIdDirty = dirty;
    }

    @Override
    protected void setPropertiesFromCursor(Cursor c) {
        setId(c.getLong(Indices._ID));
        setRestId(c.getLong(Indices.REST_ID));
        setWebId(c.getLong(Indices.WEB_ID));
        setCreatedAt(c.getLong(Indices.CREATED_AT));
        setUpdatedAt(c.getLong(Indices.UPDATED_AT));
        setName(c.getString(Indices.NAME));
        setSeriesUrl(c.getString(Indices.SERIES_URL));
        setYear(c.getLong(Indices.YEAR));
        setQuellfileId(c.getLong(Indices.QUELLFILE_ID));
    }

    public static MXSerieRecord fromCursor(Cursor c) {
        MXSerieRecord item = new MXSerieRecord();

        item.setPropertiesFromCursor(c);

        item.makeDirty(false);

        return item;
    }

    public static MXSerieRecord fromBundle(Bundle bundle, String key) {
        bundle.setClassLoader(MXSerieRecord.class.getClassLoader());
        return bundle.getParcelable(key);
    }

    public static MXSerieRecord get(long id) {
        Cursor c = null;

        ContentResolver resolver = Mechanoid.getContentResolver();

        try {
            c = resolver.query(MXSerie.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(id)).build(), PROJECTION, null, null, null);

            if (!c.moveToFirst()) {
                return null;
            }

            return fromCursor(c);
        } finally {
            Closeables.closeSilently(c);
        }
    }
}
