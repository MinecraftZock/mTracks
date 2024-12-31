package info.hannes.mechadmin_gen.sqlite;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import timber.log.Timber;

public class MxAdminDBOpenHelper extends AbstractMxAdminDBOpenHelper {

    private static final String DBNAME = "MxAdminDB.db";

    public MxAdminDBOpenHelper(Context context) {
        super(context);
    }

}
