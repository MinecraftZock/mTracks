package info.mx.tracks.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import info.mx.tracks.room.DBMigrationUtil
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Comment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class MigrationTest {

    // Helper for creating Room databases and migrations
    @get:Rule
    var migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MxDatabase::class.java
    )

    // Helper for creating SQLite database in version 1
    private lateinit var sqliteTestDbHelper: SqliteTestDbOpenHelper

    private// close the database and release any stream resources when the test finishes
    val migratedRoomDatabase: MxDatabase
        get() {
            val migrationUtil = DBMigrationUtil()
            val database = Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                MxDatabase::class.java, TEST_DB_NAME
            )
                .addMigrations(migrationUtil.provideUpgrade1To2())
                .build()
            migrationTestHelper.closeWhenFinished(database)
            return database
        }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        // To test migrations from version 1 of the database, we need to create the database
        // with version 1 using SQLite API
        sqliteTestDbHelper = SqliteTestDbOpenHelper(InstrumentationRegistry.getInstrumentation().targetContext, TEST_DB_NAME)
        // We're creating the table for every test, to ensure that the table is in the correct state
        SqliteDatabaseTestHelper.createTable(sqliteTestDbHelper)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        // Clear the database after every test
        SqliteDatabaseTestHelper.clearDatabase(sqliteTestDbHelper)
    }

    @Test
    @Throws(IOException::class)
    fun migrationDummy() {
        Unit
    }

//    @Test
//    @Throws(IOException::class)
//    fun migrationFrom1To2_containsCorrectData() {
//        // Create the database with version 3
//        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 1)
//        // insert some data
//        insertComment(COMMENT, db)
//        db.close()
//
//        val migrationUtil = DBMigrationUtil()
//        migrationTestHelper.runMigrationsAndValidate(
//            TEST_DB_NAME, 2, true,
//            migrationUtil.provideUpgrade1To2(),
//        )
//
//        // open the db with Room.
//        val mxDatabase = migratedRoomDatabase
//
//        // verify that the data is correct
//        val (id, _, username, note, country) = mxDatabase.commentDao().all[0]
//        assertEquals(id, 1L)
//        assertEquals(username, COMMENT.username)
//        //        assertEquals(dbComment.getDate(), COMMENT.getDate()); //TODO
//
//        // Insert some data
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        // Create the database with the initial version 1 schema and insert a user
//        SqliteDatabaseTestHelper.insertComment(1L, COMMENT.username, androidId, sqliteTestDbHelper!!)
//
//        insertComment(COMMENT.trackId, COMMENT.username, androidId, db)
//        //Prepare for the next version
//        db.close()
//    }

//    @Test
//    @Throws(IOException::class)
//    fun migrationFrom1To3_containsCorrectData() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        // Create the database with the initial version 1 schema and insert a user
//        SqliteDatabaseTestHelper.insertComment(1L, COMMENT.username, androidId, mSqliteTestDbHelper!!)
//
//        val migrationUtil = DBMigrationUtil()
//        migrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 3, true,
//                migrationUtil.provideUpgrade1To2(),
//                migrationUtil.provideUpgrade2To3()
//        )
//
//        // Get the latest, migrated, version of the database
//        // Check that the correct data is in the database
//        val (trackId, _, username, note, country)  = migratedRoomDatabase.commentDao().all[0]
////        assertEquals(java.util.Optional.ofNullable(id), 1L) TODO
//        assertEquals(username, COMMENT.username)
//        assertEquals(trackId, COMMENT.trackId)
//        // The date was missing in version 2, so it should be null in version 3
//        //        assertEquals(dbComment.getDate(), null); TODO
//    }
//
//    @Test
//    @Throws(IOException::class)
//    fun migrationFrom2To3_containsCorrectData() {
//        // Create the database in version 2
//        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 2)
//        // Insert some data
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        insertComment(COMMENT.trackId, COMMENT.username, androidId, db)
//        //Prepare for the next version
//        db.close()
//
//        val migrationUtil = DBMigrationUtil()
//        migrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 3, true,
//                migrationUtil.provideUpgrade1To2(),
//                migrationUtil.provideUpgrade2To3()
//        )
//
//        // MigrationTestHelper automatically verifies the schema changes, but not the data validity
//        // Validate that the data was migrated properly.
//        val (id, _, username, note, country) = migratedRoomDatabase.commentDao().all[0]
//        assertEquals(id, 1L)
//        assertEquals(username, COMMENT.username)
//        // The date was missing in version 2, so it should be null in version 3
//        //        assertEquals(dbcomment.getDate(), null); //TODO
//    }
//
//    @Test
//    @Throws(IOException::class)
//    fun startInVersion3_containsCorrectData() {
//        // Create the database with version 3
//        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 3)
//        // insert some data
//        insertComment(COMMENT, db)
//        db.close()
//
//        // open the db with Room.
//        val mxDatabase = migratedRoomDatabase
//
//        // verify that the data is correct
//        val (id, _, username, note, country) = mxDatabase.commentDao().all[0]
//        assertEquals(id, 1L)
//        assertEquals(username, COMMENT.username)
//        //        assertEquals(dbComment.getDate(), COMMENT.getDate()); //TODO
//    }

    private fun insertComment(trackId: Long?, username: String, androidId: String, db: SupportSQLiteDatabase) {
        val values = ContentValues()
        values.put("trackId", trackId)
        values.put("username", username)
        values.put("androidid", androidId)
        values.put("changed", 0)
        values.put("deleted", 0)
        values.put("approved", 0)
        values.put("rating", 2)
        values.put("note", "")
        values.put("country", "DE")

        db.insert(DBMigrationUtil.TABLE_COMMENT, SQLiteDatabase.CONFLICT_REPLACE, values)
    }

    private fun insertComment(comment: Comment, db: SupportSQLiteDatabase) {
        val values = ContentValues()
        values.put("trackId", comment.trackId)
        values.put("username", comment.username)
        values.put("androidid", comment.androidid)
        values.put("changed", comment.changed)
        values.put("deleted", comment.deleted)
        values.put("approved", comment.approved)
        values.put("rating", comment.rating)
        values.put("note", comment.note)
        values.put("country", comment.country)

        db.insert(DBMigrationUtil.TABLE_COMMENT, SQLiteDatabase.CONFLICT_REPLACE, values)
    }

    companion object {
        private val TEST_DB_NAME = "test-db"
        private val COMMENT = Comment(id = -1, trackId = 1, username = "username")
    }
}
