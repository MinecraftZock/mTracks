package info.hannes.mechadminGen.sqlite;

import android.content.Context;

public class MxAdminDBOpenHelper extends AbstractMxAdminDBOpenHelper {

    private static final String DBNAME = "MxAdminDB.db";

    public MxAdminDBOpenHelper(Context context) {
        super(context);
    }

}
