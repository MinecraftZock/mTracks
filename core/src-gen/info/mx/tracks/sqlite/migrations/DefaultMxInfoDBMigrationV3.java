/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.sqlite.migrations;

import android.database.sqlite.SQLiteDatabase;
import com.robotoworks.mechanoid.db.SQLiteMigration;

public class DefaultMxInfoDBMigrationV3 extends SQLiteMigration {
	@Override
	public void onBeforeUp(SQLiteDatabase db) {}
	
	@Override
	public void up(SQLiteDatabase db) {
		db.execSQL(
			"alter table trackstage add column updated integer default 0 "
		);	
	}
	
	@Override
	public void onAfterUp(SQLiteDatabase db) {}
}