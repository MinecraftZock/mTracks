/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.sqlite.migrations;

import android.database.sqlite.SQLiteDatabase;
import com.robotoworks.mechanoid.db.SQLiteMigration;

public class DefaultMxInfoDBMigrationV4 extends SQLiteMigration {
	@Override
	public void onBeforeUp(SQLiteDatabase db) {}
	
	@Override
	public void up(SQLiteDatabase db) {
		db.execSQL(
			"create table weather ( " +
			"_id integer primary key autoincrement, " +
			"restId integer, " +
			"trackClientId integer, " +
			"type text default \"D\", " +
			"content text, " +
			"dt integer, " +
			"created integer default ( strftime ( '%s' , 'now' ) ) " +
			") "
		);	
		db.execSQL(
			"create table message ( " +
			"_id integer primary key autoincrement, " +
			"restId integer, " +
			"changed integer, " +
			"androidid text, " +
			"read integer default 0, " +
			"msg text " +
			") "
		);	
	}
	
	@Override
	public void onAfterUp(SQLiteDatabase db) {}
}
