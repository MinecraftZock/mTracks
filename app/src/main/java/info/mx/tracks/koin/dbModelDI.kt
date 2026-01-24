@file:Suppress("ktlint:standard:filename")
package info.mx.tracks.koin

import androidx.room.Room
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.memory.MxMemDatabase
import org.koin.dsl.module

val dbModule = module {

    single {
        Room.databaseBuilder(get(), MxDatabase::class.java, MxDatabase.ROOM_DATABASE_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigrationFrom(true, 1, 2, 3, 4)
            .build()
    }

    single {
        Room.inMemoryDatabaseBuilder(get(), MxMemDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    single { get<MxDatabase>().capturedLatLngDao() }
}
