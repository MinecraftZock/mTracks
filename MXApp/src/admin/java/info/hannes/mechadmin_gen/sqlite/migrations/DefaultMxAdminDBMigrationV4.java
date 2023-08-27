/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.sqlite.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.robotoworks.mechanoid.db.SQLiteMigration;

public class DefaultMxAdminDBMigrationV4 extends SQLiteMigration {
	@Override
	public void onBeforeUp(SQLiteDatabase db) {}
	
	@Override
	public void up(SQLiteDatabase db) {
		db.execSQL(
			"alter table trackstageBrother add column url text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Phone text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Notes text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Votes text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Openmondays integer "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Opentuesdays integer "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Openwednesday integer "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Openthursday integer "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Openfriday integer "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Opensaturday integer "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Opensunday integer "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Hoursmonday text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Hourstuesday text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Hourswednesday text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Hoursthursday text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Hoursfriday text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Hourssaturday text "
		);	
		db.execSQL(
			"alter table trackstageBrother add column Hourssunday text "
		);	
		db.execSQL(
			"create table pictures ( " +
			"_id integer primary key autoincrement, " +
			"restId integer, " +
			"trackId integer, " +
			"changed integer, " +
			"www text default \"\" " +
			") "
		);	
		db.execSQL(
			"create table videos ( " +
			"_id integer primary key autoincrement, " +
			"restId integer, " +
			"trackId integer, " +
			"changed integer, " +
			"www text default \"\" " +
			") "
		);	
	}
	
	@Override
	public void onAfterUp(SQLiteDatabase db) {}
}