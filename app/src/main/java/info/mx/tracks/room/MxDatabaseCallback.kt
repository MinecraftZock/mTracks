package info.mx.tracks.room

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

private class MxDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
//        AppModule.mxDatabase?.let { database ->
//            scope.launch {
//                populateDatabase(database.commentDao())
//            }
//        }
    }

    suspend fun populateDatabase(commentDao: CommentDao) {
        // Delete all content here.
        //commentDao.deleteAll()

        // TODO: Add default data
    }
}
