/*
 * Generated by Robotoworks Mechanoid
 */
package info.hannes.mechadmin_gen.sqlite.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.robotoworks.mechanoid.db.SQLiteMigration;

public class DefaultMxCalMigrationV3 extends SQLiteMigration {
	@Override
	public void onBeforeUp(SQLiteDatabase db) {}
	
	@Override
	public void up(SQLiteDatabase db) {
		db.execSQL(
			"drop view QuellFileSmall "
		);	
		db.execSQL(
			"create view QuellFileSmall as " +
			"select _id as _id, " +
			"restId as restId, " +
			"url as url, " +
			"createdate as createdate, " +
			"updatedCount as updatedCount, " +
			"substr(content,1, 10) as content " +
			"from QuellFile "
		);	
	}
	
	@Override
	public void onAfterUp(SQLiteDatabase db) {}
}
