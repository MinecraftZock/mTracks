/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.sqlite.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.robotoworks.mechanoid.db.SQLiteMigration;

public class DefaultMxAdminDBMigrationV10 extends SQLiteMigration {
	@Override
	public void onBeforeUp(SQLiteDatabase db) {}
	
	@Override
	public void up(SQLiteDatabase db) {
		db.execSQL(
			"alter table PictureStage add column uninteressant integer default 0 "
		);	
	}
	
	@Override
	public void onAfterUp(SQLiteDatabase db) {}
}
