package info.mx.tracks.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

class DBMigrationUtil {

    fun provideUpgrade1To2(): Migration {
        return object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Timber.d("Upgrade database from version 1 to 2.")

                db.execSQL(CREATE_COMMENT)
                db.execSQL("CREATE INDEX IF NOT EXISTS `CommentTrackIdIndex` ON `$TABLE_COMMENT` (`trackId`)")
                db.execSQL(CREATE_COUNTRY)
                db.execSQL(CREATE_EVENT)
                db.execSQL(CREATE_EVENT_I)
                db.execSQL(CREATE_FAVORIT)
                db.execSQL(CREATE_NETWORKT)
                db.execSQL(CREATE_PICTURE)
                db.execSQL(CREATE_PICTURE_I)
                db.execSQL(CREATE_ROUTE)
                db.execSQL(CREATE_ROUTE_I)
                db.execSQL(CREATE_SERIES)
                db.execSQL(CREATE_TRACK)
                db.execSQL(CREATE_TRACK_I)
                db.execSQL(CREATE_TRACKSTAGE)
                db.execSQL(CREATE_TRACKSTAGE_I)
                db.execSQL(CREATE_WEATHER)
                db.execSQL(CREATE_WEATHER_I)
            }
        }
    }

//    fun provideUpgrade2To3(): Migration {
//        return object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                Timber.d("Upgrade database from version 2 to 3.")
//
//                database.execSQL("CREATE INDEX IF NOT EXISTS `CommentTrackIdIndex` ON `$TABLE_COMMENT` (`trackId`)")
//            }
//        }
//    }
//
//    fun provideUpgrade3To4(): Migration {
//        return object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                Timber.d("Upgrade database from version 3 to 4.")
//
//                database.execSQL(CREATE_COUNTRY)
//                database.execSQL(CREATE_EVENT)
//                database.execSQL(CREATE_EVENT_I)
//                database.execSQL(CREATE_FAVORIT)
//                database.execSQL(CREATE_NETWORKT)
//                database.execSQL(CREATE_PICTURE)
//                database.execSQL(CREATE_PICTURE_I)
//                database.execSQL(CREATE_ROUTE)
//                database.execSQL(CREATE_ROUTE_I)
//                database.execSQL(CREATE_SERIES)
//                database.execSQL(CREATE_TRACK)
//                database.execSQL(CREATE_TRACK_I)
//                database.execSQL(CREATE_TRACKSTAGE)
//                database.execSQL(CREATE_TRACKSTAGE_I)
//                database.execSQL(CREATE_WEATHER)
//                database.execSQL(CREATE_WEATHER_I)
//            }
//        }
//    }

    companion object {
        const val TABLE_CAPTUREDLATLNG = "CapturedLatLng"
        const val TABLE_COMMENT = "Comment"
        const val TABLE_COUNTRY = "Country"
        const val TABLE_EVENT = "Event"
        const val TABLE_FAVORIT = "Favorit"
        const val TABLE_NETWORK = "Network"
        const val TABLE_PICTURE = "Picture"
        const val TABLE_ROUTE = "Route"
        const val TABLE_SERIES = "Series"
        const val TABLE_TRACK = "Track"
        const val TABLE_TRACKSTAGE = "TrackStage"
        const val TABLE_WEATHER = "Weather"

        // it's a copy from 2.json
        const val CREATE_COMMENT = "CREATE TABLE IF NOT EXISTS `$TABLE_COMMENT` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL, `trackId` INTEGER NOT NULL, `rating` INTEGER NOT NULL, `username` TEXT NOT NULL, `note` TEXT NOT NULL, `country` TEXT NOT NULL, `deleted` INTEGER NOT NULL, `approved` INTEGER NOT NULL, `androidid` TEXT NOT NULL)"

        // it's a copy from 4.json with changed tablename
        const val CREATE_COUNTRY = "CREATE TABLE IF NOT EXISTS `$TABLE_COUNTRY` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `country` TEXT NOT NULL, `show` INTEGER NOT NULL)"
        const val CREATE_EVENT = "CREATE TABLE IF NOT EXISTS `$TABLE_EVENT` (`trackId` INTEGER NOT NULL, `seriesId` INTEGER NOT NULL, `comment` TEXT NOT NULL, `deleted` INTEGER NOT NULL, `approved` INTEGER NOT NULL, `androidid` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_EVENT_I = "CREATE  INDEX `EventTrackIdIndex` ON `$TABLE_EVENT` (`trackId`)"
        const val CREATE_FAVORIT = "CREATE TABLE IF NOT EXISTS `$TABLE_FAVORIT` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `trackId` INTEGER NOT NULL)"
        const val CREATE_NETWORKT = "CREATE TABLE IF NOT EXISTS `$TABLE_NETWORK` (`tracks` INTEGER NOT NULL, `reason` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_PICTURE = "CREATE TABLE IF NOT EXISTS `$TABLE_PICTURE` (`trackId` INTEGER NOT NULL, `localfile` TEXT NOT NULL, `localthumb` TEXT NOT NULL, `username` TEXT NOT NULL, `deleted` INTEGER NOT NULL, `approved` INTEGER NOT NULL, `androidid` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_PICTURE_I = "CREATE  INDEX `PictureTrackIdIndex` ON `$TABLE_PICTURE` (`trackId`)"
        const val CREATE_ROUTE = "CREATE TABLE IF NOT EXISTS `$TABLE_ROUTE` (`trackId` INTEGER NOT NULL, `content` TEXT NOT NULL, `logitude` REAL NOT NULL, `langitude` REAL NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_ROUTE_I = "CREATE  INDEX `RouteTrackIdIndex` ON `$TABLE_ROUTE` (`trackId`)"
        const val CREATE_SERIES = "CREATE TABLE IF NOT EXISTS `$TABLE_SERIES` (`name` TEXT NOT NULL, `seriesUrl` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_TRACK = "CREATE TABLE IF NOT EXISTS `$TABLE_TRACK` (`trackname` TEXT NOT NULL, `logitude` REAL NOT NULL, `langitude` REAL NOT NULL, `approved` INTEGER NOT NULL, `country` TEXT NOT NULL, `url` TEXT NOT NULL, `fees` TEXT NOT NULL, `phone` TEXT NOT NULL, `contact` TEXT NOT NULL, `metatext` TEXT NOT NULL, `licence` TEXT NOT NULL, `kidstrack` INTEGER NOT NULL, `openmondays` INTEGER NOT NULL, `opentuesdays` INTEGER NOT NULL, `openwednesday` INTEGER NOT NULL, `openthursday` INTEGER NOT NULL, `openfriday` INTEGER NOT NULL, `opensaturday` INTEGER NOT NULL, `opensunday` INTEGER NOT NULL, `hoursmonday` TEXT NOT NULL, `hourstuesday` TEXT NOT NULL, `hourswednesday` TEXT NOT NULL, `hoursthursday` TEXT NOT NULL, `hoursfriday` TEXT NOT NULL, `hourssaturday` TEXT NOT NULL, `hourssunday` TEXT NOT NULL, `tracklength` INTEGER NOT NULL, `soiltype` INTEGER NOT NULL, `camping` INTEGER NOT NULL, `shower` INTEGER NOT NULL, `cleaning` INTEGER NOT NULL, `electricity` INTEGER NOT NULL, `distance2location` INTEGER NOT NULL, `supercross` INTEGER NOT NULL, `showroom` INTEGER NOT NULL, `workshop` INTEGER NOT NULL, `validuntil` INTEGER NOT NULL, `singletracks` INTEGER NOT NULL, `campingrvrvhookup` INTEGER NOT NULL, `mxtrack` INTEGER NOT NULL, `a4x4` INTEGER NOT NULL, `utv` INTEGER NOT NULL, `quad` INTEGER NOT NULL, `areatype` INTEGER NOT NULL, `schwierigkeit` INTEGER NOT NULL, `indoor` INTEGER NOT NULL, `lastAsked` INTEGER NOT NULL, `trackaccess` TEXT NOT NULL, `logoURL` TEXT NOT NULL, `brands` TEXT NOT NULL, `facebook` TEXT NOT NULL, `adress` TEXT NOT NULL, `feescamping` TEXT NOT NULL, `daysopen` TEXT NOT NULL, `noiselimit` TEXT NOT NULL, `trackstatus` TEXT NOT NULL, `andoridid` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_TRACK_I = "CREATE  INDEX `TrackIdIndex` ON `$TABLE_TRACK` (`id`)"
        const val CREATE_TRACKSTAGE = "CREATE TABLE IF NOT EXISTS `$TABLE_TRACKSTAGE` (`trackId` INTEGER NOT NULL, `updated` INTEGER NOT NULL, `trackname` TEXT NOT NULL, `logitude` REAL NOT NULL, `langitude` REAL NOT NULL, `approved` INTEGER NOT NULL, `country` TEXT NOT NULL, `url` TEXT NOT NULL, `fees` TEXT NOT NULL, `phone` TEXT NOT NULL, `contact` TEXT NOT NULL, `metatext` TEXT NOT NULL, `licence` TEXT NOT NULL, `kidstrack` INTEGER NOT NULL, `openmondays` INTEGER NOT NULL, `opentuesdays` INTEGER NOT NULL, `openwednesday` INTEGER NOT NULL, `openthursday` INTEGER NOT NULL, `openfriday` INTEGER NOT NULL, `opensaturday` INTEGER NOT NULL, `opensunday` INTEGER NOT NULL, `hoursmonday` TEXT NOT NULL, `hourstuesday` TEXT NOT NULL, `hourswednesday` TEXT NOT NULL, `hoursthursday` TEXT NOT NULL, `hoursfriday` TEXT NOT NULL, `hourssaturday` TEXT NOT NULL, `hourssunday` TEXT NOT NULL, `tracklength` INTEGER NOT NULL, `soiltype` INTEGER NOT NULL, `camping` INTEGER NOT NULL, `shower` INTEGER NOT NULL, `cleaning` INTEGER NOT NULL, `electricity` INTEGER NOT NULL, `distance2location` INTEGER NOT NULL, `supercross` INTEGER NOT NULL, `showroom` INTEGER NOT NULL, `workshop` INTEGER NOT NULL, `validuntil` INTEGER NOT NULL, `singletracks` INTEGER NOT NULL, `campingrvrvhookup` INTEGER NOT NULL, `mxtrack` INTEGER NOT NULL, `a4x4` INTEGER NOT NULL, `utv` INTEGER NOT NULL, `quad` INTEGER NOT NULL, `areatype` INTEGER NOT NULL, `schwierigkeit` INTEGER NOT NULL, `indoor` INTEGER NOT NULL, `lastAsked` INTEGER NOT NULL, `trackaccess` TEXT NOT NULL, `logoURL` TEXT NOT NULL, `brands` TEXT NOT NULL, `facebook` TEXT NOT NULL, `adress` TEXT NOT NULL, `feescamping` TEXT NOT NULL, `daysopen` TEXT NOT NULL, `noiselimit` TEXT NOT NULL, `trackstatus` TEXT NOT NULL, `andoridid` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_TRACKSTAGE_I = "CREATE  INDEX `TrackStageTrackIdIndex` ON `$TABLE_TRACKSTAGE` (`trackId`)"
        const val CREATE_WEATHER = "CREATE TABLE IF NOT EXISTS `$TABLE_WEATHER` (`trackId` INTEGER NOT NULL, `type` TEXT NOT NULL, `content` TEXT NOT NULL, `dt` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `changed` INTEGER NOT NULL)"
        const val CREATE_WEATHER_I = "CREATE  INDEX `WeatherTrackIdIndex` ON `$TABLE_WEATHER` (`trackId`)"
    }

}
