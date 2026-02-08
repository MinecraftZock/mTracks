package info.mx.core.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.robotoworks.mechanoid.db.SQLiteMigration
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.AbstractMxInfoDBOpenHelper
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.core_generated.sqlite.MxInfoDBContract.Country
import info.mx.core_generated.sqlite.migrations.DefaultMxInfoDBMigrationV1
import info.mx.core_generated.sqlite.migrations.DefaultMxInfoDBMigrationV4
import info.mx.core_generated.sqlite.migrations.DefaultMxInfoDBMigrationV5
import info.mx.tracks.common.SecHelper
import timber.log.Timber
import java.util.Locale

class MxInfoDBOpenHelper(context: Context) : AbstractMxInfoDBOpenHelper(context, getDir(context)) {
    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        this.setDatabase(db)
    }

    override fun createMxInfoDBMigrationV1(): SQLiteMigration {
        return object : DefaultMxInfoDBMigrationV1() {
            override fun onAfterUp(db: SQLiteDatabase) {
                val cvToFilter = Country.newBuilder()
                    .setCountry("zz")
                    .setShow(0)
                    .values
                db.insert(Sources.COUNTRY, null, cvToFilter)
                val country = Locale.getDefault().country
                val defCountry = country.take(if (country.length < 2) country.length else 2)
                val cvToShow = Country.newBuilder()
                    .setCountry(if (defCountry == "") "DE" else defCountry) // TODO
                    .setShow(1)
                    .values
                db.insert(Sources.COUNTRY, null, cvToShow)
                if (defCountry != "DE") {
                    val cvToDE = Country.newBuilder()
                        .setCountry(Locale.GERMANY.country)
                        .setShow(1)
                        .values
                    db.insert(Sources.COUNTRY, null, cvToDE)
                }
            }
        }
    }

    override fun createMxInfoDBMigrationV4(): SQLiteMigration {
        return object : DefaultMxInfoDBMigrationV4() {
            override fun onAfterUp(db: SQLiteDatabase?) {
                // hide closed tracks by default
                MxPreferences.instance.onlyOpen = true
            }
        }
    }

    override fun createMxInfoDBMigrationV5(): SQLiteMigration {
        return object : DefaultMxInfoDBMigrationV5() {
            override fun onBeforeUp(db: SQLiteDatabase) {
                // crypt facebook existing values
                val columns: Array<String> = arrayOf(MxInfoDBContract.Tracks._ID, MxInfoDBContract.Tracks.FACEBOOK)
                val cursor = db
                    .query(
                        Sources.TRACKS,
                        columns,
                        MxInfoDBContract.Tracks.FACEBOOK + " is not null",
                        null,
                        null,
                        null,
                        null
                    )
                while (cursor.moveToNext()) {
                    val facebookCrypt = SecHelper.encryptB64(cursor.getString(1))
                    val values = MxInfoDBContract.Tracks.newBuilder()
                        .setFacebook(facebookCrypt)
                        .values
                    val whereArgs = arrayOf<String?>(cursor.getString(0))
                    Timber.d("facebook crypt:%s", cursor.getString(0))
                    db.update(
                        Sources.TRACKS,
                        values,
                        MxInfoDBContract.Tracks._ID + " = ?",
                        whereArgs
                    )
                }
                cursor.close()
            }
        }
    }

    private fun setDatabase(database: SQLiteDatabase?) {
        Companion.database = database
    }

    companion object {
        private const val DBNAME = "MxInfo.db"
        var database: SQLiteDatabase? = null
            private set

        fun getDir(context: Context): String {
            return context.getDatabasePath(DBNAME).absolutePath
        }
    }
}
