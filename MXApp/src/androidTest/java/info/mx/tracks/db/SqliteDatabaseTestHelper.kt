package info.mx.tracks.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import info.mx.tracks.room.DBMigrationUtil

object SqliteDatabaseTestHelper {

    fun insertComment(trackId: Long, userName: String, androidid: String, helper: SqliteTestDbOpenHelper) {
        val db = helper.writableDatabase
        val values = ContentValues()
        values.put("trackId", trackId)
        values.put("username", userName)
        values.put("androidid", androidid)
        values.put("changed", 0)
        values.put("deleted", 0)
        values.put("approved", 0)
        values.put("rating", 2)
        values.put("note", "")
        values.put("country", "DE")

        db.insertWithOnConflict(DBMigrationUtil.TABLE_COMMENT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun createTable(helper: SqliteTestDbOpenHelper) {
        val db = helper.writableDatabase
        db.execSQL(DBMigrationUtil.CREATE_COMMENT)
        db.close()
    }

    fun clearDatabase(helper: SqliteTestDbOpenHelper) {
        val db = helper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${DBMigrationUtil.TABLE_COMMENT}")
        db.close()
    }
}
