/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.robotoworks.mechanoid.db.MechanoidSQLiteOpenHelper;
import com.robotoworks.mechanoid.db.SQLiteMigration;

import info.hannes.mechadmin_gen.sqlite.migrations.DefaultMxCalMigrationV1;
import info.hannes.mechadmin_gen.sqlite.migrations.DefaultMxCalMigrationV2;
import info.hannes.mechadmin_gen.sqlite.migrations.DefaultMxCalMigrationV3;

public abstract class AbstractMxCalOpenHelper extends MechanoidSQLiteOpenHelper {
	private static final String DATABASE_NAME = "MxCal.db";

	public static final int VERSION = 3;

	public interface Sources {
		String DOWN_LOAD_SITE = "DownLoadSite";
		String M_X_SERIE = "MXSerie";
		String MX_SERIES_TRACK = "MxSeriesTrack";
		String MX_TRACK = "MxTrack";
		String QUELL_FILE = "QuellFile";
		String IMPORTSTATUS_CAL = "importstatusCal";
		String QUELL_FILE_SMALL = "QuellFileSmall";
	}

	public AbstractMxCalOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	public AbstractMxCalOpenHelper(Context context, String name) {
		super(context, name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		applyMigrations(db, 0, VERSION);
	}
	

	@Override
	protected SQLiteMigration createMigration(int version) {
		switch(version) {
			case 0:
				return createMxCalMigrationV1();
			case 1:
				return createMxCalMigrationV2();
			case 2:
				return createMxCalMigrationV3();
			default:
				throw new IllegalStateException("No migration for version " + version);
		}
	}
	
	protected SQLiteMigration createMxCalMigrationV1() {
		return new DefaultMxCalMigrationV1();
	}
	protected SQLiteMigration createMxCalMigrationV2() {
		return new DefaultMxCalMigrationV2();
	}
	protected SQLiteMigration createMxCalMigrationV3() {
		return new DefaultMxCalMigrationV3();
	}
}