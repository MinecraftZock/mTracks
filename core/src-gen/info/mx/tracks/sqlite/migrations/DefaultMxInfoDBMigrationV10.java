/*
 * Generated by Robotoworks Mechanoid
 */
package info.mx.tracks.sqlite.migrations;

import android.database.sqlite.SQLiteDatabase;
import com.robotoworks.mechanoid.db.SQLiteMigration;

public class DefaultMxInfoDBMigrationV10 extends SQLiteMigration {
	@Override
	public void onBeforeUp(SQLiteDatabase db) {}
	
	@Override
	public void up(SQLiteDatabase db) {
		db.execSQL(
			"drop table ratings "
		);	
		db.execSQL(
			"drop view tracksges "
		);	
		db.execSQL(
			"create view tracksges as " +
			"select " +
			"Trackname as trackname, " +
			"Approved as Approved, " +
			"Distance2location as distance2location, " +
			"Openmondays as openmondays, " +
			"Opentuesdays as opentuesdays, " +
			"Nu_events as n_u_events, " +
			"Openwednesday as openwednesday, Openthursday as openthursday, " +
			"Openfriday as openfriday, Opensaturday as opensaturday, " +
			"Opensunday as opensunday, " +
			"Country as country, trackaccess as trackaccess, " +
			"restId as restId, tracks._id as _id, brands as brands, " +
			"Metatext as metatext, " +
			"Kidstrack as Kidstrack, Supercross as Supercross, " +
			"Shower as Shower, Cleaning as Cleaning, " +
			"Electricity as Electricity, Camping as Camping, " +
			"Latitude as Latitude, Longitude as Longitude, " +
			"Hoursmonday as Hoursmonday, " +
			"Hourstuesday as Hourstuesday, " +
			"Hourswednesday as Hourswednesday, " +
			"Hoursthursday as Hoursthursday, " +
			"Hoursfriday as Hoursfriday, " +
			"Hourssaturday as Hourssaturday, " +
			"Hourssunday as Hourssunday, " +
			"validuntil as validuntil, " +
			"Url as Url, " +
			"Phone as Phone, " +
			"Contact as Contact, " +
			"Notes as Notes, " +
			"Tracklength as Tracklength, " +
			"Soiltype as Soiltype, " +
			"facebook as facebook, " +
			"adress as adress, " +
			"Licence as Licence, " +
			"Fees as Fees, " +
			" " +
			"feescamping as feescamping, " +
			"daysopen as daysopen, " +
			"noiselimit as noiselimit, " +
			"campingrvrvhookup as campingrvrvhookup, " +
			"singletracks as singletracks, " +
			"mxtrack as mxtrack, " +
			"a4x4 as a4x4, " +
			"enduro as endruo, " +
			"utv as utv, " +
			"quad as quad, " +
			"trackstatus as trackstatus, " +
			"areatype as areatype, " +
			"schwierigkeit as schwierigkeit, " +
			"indoor as indoor, " +
			" " +
			" " +
			"(cast(ifnull(picturecount,0) as integer)) as picturecount, " +
			"(cast(ifnull(eventcount,0) as integer)) as eventcount " +
			"from tracks " +
			"left join picturesum on tracks.restId = picturesum.track_restId " +
			"left join eventsum on tracks.restId = eventsum.track_restId "
		);	
		db.execSQL(
			"drop view tracksGesSum "
		);	
		db.execSQL(
			"create view tracksGesSum as select " +
			"Trackname as trackname, " +
			"Approved as Approved, " +
			"Distance2location as distance2location, " +
			"Openmondays as openmondays, " +
			"Opentuesdays as opentuesdays, " +
			"Nu_events as n_u_events, " +
			"Openwednesday as openwednesday, Openthursday as openthursday, " +
			"Openfriday as openfriday, Opensaturday as opensaturday, " +
			"Opensunday as opensunday, " +
			"Country as country, trackaccess as trackaccess, " +
			"restId as restId, tracks._id as _id, brands as brands, " +
			"Metatext as metatext, " +
			"Kidstrack as Kidstrack, Supercross as Supercross, " +
			"Shower as Shower, Cleaning as Cleaning, " +
			"Electricity as Electricity, Camping as Camping, " +
			"Latitude as Latitude, Longitude as Longitude, " +
			"Hoursmonday as Hoursmonday, " +
			"Hourstuesday as Hourstuesday, " +
			"Hourswednesday as Hourswednesday, " +
			"Hoursthursday as Hoursthursday, " +
			"Hoursfriday as Hoursfriday, " +
			"Hourssaturday as Hourssaturday, " +
			"Hourssunday as Hourssunday, " +
			"validuntil as validuntil, " +
			"Url as Url, " +
			"Phone as Phone, " +
			"Contact as Contact, " +
			"Notes as Notes, " +
			"Tracklength as Tracklength, " +
			"Soiltype as Soiltype, " +
			"facebook as facebook, " +
			"adress as adress, " +
			"Licence as Licence, " +
			"Fees as Fees, " +
			"feescamping as feescamping, " +
			"daysopen as daysopen, " +
			"noiselimit as noiselimit, " +
			"campingrvrvhookup as campingrvrvhookup, " +
			"singletracks as singletracks, " +
			"mxtrack as mxtrack, " +
			"a4x4 as a4x4, " +
			"enduro as endruo, " +
			"utv as utv, " +
			"quad as quad, " +
			"trackstatus as trackstatus, " +
			"areatype as areatype, " +
			"schwierigkeit as schwierigkeit, " +
			"indoor as indoor, " +
			" " +
			"(cast(ifnull(picturecount,0) as integer)) as picturecount, " +
			"(cast(ifnull(eventcount,0) as integer)) as eventcount " +
			"from tracks " +
			"left join picturesum on tracks.restId = picturesum.track_restId " +
			"left join eventsum on tracks.restId = eventsum.track_restId "
		);	
		db.execSQL(
			"drop view ratingsum "
		);	
	}
	
	@Override
	public void onAfterUp(SQLiteDatabase db) {}
}
