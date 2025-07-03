package info.mx.tracks.room

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MxCoreDatabase::class.java,
        listOf(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            // db has schema version 1. Insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.
            execSQL("INSERT INTO CapturedLatLng VALUES (1, 52.5, 13.4, 1234567890, 100, 50, 'test', 'extra', 'testtrack')")

            // Prepare for the next version.
            close()
        }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MxCoreDatabase.MIGRATION_1_2)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        val cursor = db.query("SELECT * FROM CapturedLatLng")
        cursor.moveToFirst()
        assert(cursor.getDouble(cursor.getColumnIndexOrThrow("lat")) == 52.5)
        assert(cursor.getDouble(cursor.getColumnIndexOrThrow("lon ")) == 13.4)
        cursor.close()

        // Verify that new tables exist
        val tracksCursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='tracks'")
        assert(tracksCursor.count == 1)
        tracksCursor.close()

        val picturesCursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='pictures'")
        assert(picturesCursor.count == 1)
        picturesCursor.close()
    }

    @Test
    fun testDatabaseCreation() {
        // Create the database in the latest version directly
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MxCoreDatabase::class.java
        ).build()

        // Verify we can access all DAOs
        val trackDao = database.trackDao()
        val pictureDao = database.pictureDao()
        val capturedLatLngDao = database.capturedLatLngDao()

        // Basic sanity checks
        assert(trackDao != null)
        assert(pictureDao != null)
        assert(capturedLatLngDao != null)

        database.close()
    }
}