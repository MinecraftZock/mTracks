/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.sqlite.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.robotoworks.mechanoid.db.SQLiteMigration;

public class DefaultMxAdminDBMigrationV7 extends SQLiteMigration {
	@Override
	public void onBeforeUp(SQLiteDatabase db) {}
	
	@Override
	public void up(SQLiteDatabase db) {
		db.execSQL(
			"alter table pictures add column trackRestId integer "
		);	
		db.execSQL(
			"alter table videos add column trackRestId integer "
		);	
	}
	
	@Override
	public void onAfterUp(SQLiteDatabase db) {}
}
