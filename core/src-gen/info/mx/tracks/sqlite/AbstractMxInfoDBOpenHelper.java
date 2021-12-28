/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.robotoworks.mechanoid.db.MechanoidSQLiteOpenHelper;
import com.robotoworks.mechanoid.db.SQLiteMigration;

import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV1;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV2;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV3;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV4;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV5;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV6;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV7;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV8;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV9;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV10;
import info.mx.tracks.sqlite.migrations.DefaultMxInfoDBMigrationV11;

public abstract class AbstractMxInfoDBOpenHelper extends MechanoidSQLiteOpenHelper {
	private static final String DATABASE_NAME = "MxInfoDB.db";

	public static final int VERSION = 11;

	public interface Sources {
		String TRACKS = "tracks";
		String TRACKSTAGE = "trackstage";
		String NOTES = "notes";
		String FAVORITS = "favorits";
		String COUNTRY = "country";
		String PICTURES = "pictures";
		String SERIES = "series";
		String IMPORTSTATUS = "importstatus";
		String WEATHER = "weather";
		String ROUTE = "route";
		String NETWORK = "network";
		String COUNTRYSUM = "countrysum";
		String PICTURESUM = "picturesum";
		String COUNTRYCOUNT = "countrycount";
		String USER_ACTIVITY = "userActivity";
		String TRACKSGES = "tracksges";
		String TRACKS_GES_SUM = "tracksGesSum";
	}

	public AbstractMxInfoDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	public AbstractMxInfoDBOpenHelper(Context context, String name) {
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
				return createMxInfoDBMigrationV1();
			case 1:
				return createMxInfoDBMigrationV2();
			case 2:
				return createMxInfoDBMigrationV3();
			case 3:
				return createMxInfoDBMigrationV4();
			case 4:
				return createMxInfoDBMigrationV5();
			case 5:
				return createMxInfoDBMigrationV6();
			case 6:
				return createMxInfoDBMigrationV7();
			case 7:
				return createMxInfoDBMigrationV8();
			case 8:
				return createMxInfoDBMigrationV9();
			case 9:
				return createMxInfoDBMigrationV10();
			case 10:
				return createMxInfoDBMigrationV11();
			default:
				throw new IllegalStateException("No migration for version " + version);
		}
	}
	
	protected SQLiteMigration createMxInfoDBMigrationV1() {
		return new DefaultMxInfoDBMigrationV1();
	}
	protected SQLiteMigration createMxInfoDBMigrationV2() {
		return new DefaultMxInfoDBMigrationV2();
	}
	protected SQLiteMigration createMxInfoDBMigrationV3() {
		return new DefaultMxInfoDBMigrationV3();
	}
	protected SQLiteMigration createMxInfoDBMigrationV4() {
		return new DefaultMxInfoDBMigrationV4();
	}
	protected SQLiteMigration createMxInfoDBMigrationV5() {
		return new DefaultMxInfoDBMigrationV5();
	}
	protected SQLiteMigration createMxInfoDBMigrationV6() {
		return new DefaultMxInfoDBMigrationV6();
	}
	protected SQLiteMigration createMxInfoDBMigrationV7() {
		return new DefaultMxInfoDBMigrationV7();
	}
	protected SQLiteMigration createMxInfoDBMigrationV8() {
		return new DefaultMxInfoDBMigrationV8();
	}
	protected SQLiteMigration createMxInfoDBMigrationV9() {
		return new DefaultMxInfoDBMigrationV9();
	}
	protected SQLiteMigration createMxInfoDBMigrationV10() {
		return new DefaultMxInfoDBMigrationV10();
	}
	protected SQLiteMigration createMxInfoDBMigrationV11() {
		return new DefaultMxInfoDBMigrationV11();
	}
}
