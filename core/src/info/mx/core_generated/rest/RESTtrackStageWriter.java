package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class RESTtrackStageWriter extends JsonEntityWriter<RESTtrackStage> {

	public RESTtrackStageWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, RESTtrackStage entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		writer.name("trackId");
		writer.value(entity.getTrackId());
		writer.name("androidid");
		writer.value(entity.getAndroidid());
		writer.name("approved");
		writer.value(entity.getApproved());
		writer.name("country");
		writer.value(entity.getCountry());
		writer.name("changed");
		writer.value(entity.getChanged());
		writer.name("trackname");
		writer.value(entity.getTrackname());
		writer.name("longitude");
		writer.value(entity.getLongitude());
		writer.name("latitude");
		writer.value(entity.getLatitude());
		writer.name("ins_longitude");
		writer.value(entity.getInsLongitude());
		writer.name("ins_latitude");
		writer.value(entity.getInsLatitude());
		writer.name("ins_distance");
		writer.value(entity.getInsDistance());
		writer.name("url");
		writer.value(entity.getUrl());
		writer.name("fees");
		writer.value(entity.getFees());
		writer.name("phone");
		writer.value(entity.getPhone());
		writer.name("notes");
		writer.value(entity.getNotes());
		writer.name("contact");
		writer.value(entity.getContact());
		writer.name("licence");
		writer.value(entity.getLicence());
		writer.name("kidstrack");
		writer.value(entity.getKidstrack());
		writer.name("openmondays");
		writer.value(entity.getOpenmondays());
		writer.name("opentuesdays");
		writer.value(entity.getOpentuesdays());
		writer.name("openwednesday");
		writer.value(entity.getOpenwednesday());
		writer.name("openthursday");
		writer.value(entity.getOpenthursday());
		writer.name("openfriday");
		writer.value(entity.getOpenfriday());
		writer.name("opensaturday");
		writer.value(entity.getOpensaturday());
		writer.name("opensunday");
		writer.value(entity.getOpensunday());
		writer.name("hoursmonday");
		writer.value(entity.getHoursmonday());
		writer.name("hourstuesday");
		writer.value(entity.getHourstuesday());
		writer.name("hourswednesday");
		writer.value(entity.getHourswednesday());
		writer.name("hoursthursday");
		writer.value(entity.getHoursthursday());
		writer.name("hoursfriday");
		writer.value(entity.getHoursfriday());
		writer.name("hourssaturday");
		writer.value(entity.getHourssaturday());
		writer.name("hourssunday");
		writer.value(entity.getHourssunday());
		writer.name("tracklength");
		writer.value(entity.getTracklength());
		writer.name("soiltype");
		writer.value(entity.getSoiltype());
		writer.name("camping");
		writer.value(entity.getCamping());
		writer.name("shower");
		writer.value(entity.getShower());
		writer.name("cleaning");
		writer.value(entity.getCleaning());
		writer.name("electricity");
		writer.value(entity.getElectricity());
		writer.name("supercross");
		writer.value(entity.getSupercross());
		writer.name("trackaccess");
		writer.value(entity.getTrackaccess());
		writer.name("facebook");
		writer.value(entity.getFacebook());
		writer.name("adress");
		writer.value(entity.getAdress());
		writer.name("feescamping");
		writer.value(entity.getFeescamping());
		writer.name("daysopen");
		writer.value(entity.getDaysopen());
		writer.name("noiselimit");
		writer.value(entity.getNoiselimit());
		writer.name("campingrvhookups");
		writer.value(entity.getCampingrvhookups());
		writer.name("singletrack");
		writer.value(entity.getSingletrack());
		writer.name("mxtrack");
		writer.value(entity.getMxtrack());
		writer.name("a4x4");
		writer.value(entity.getA4x4());
		writer.name("enduro");
		writer.value(entity.getEnduro());
		writer.name("utv");
		writer.value(entity.getUtv());
		writer.name("quad");
		writer.value(entity.getQuad());
		writer.name("trackstatus");
		writer.value(entity.getTrackstatus());
		writer.name("areatype");
		writer.value(entity.getAreatype());
		writer.name("schwierigkeit");
		writer.value(entity.getSchwierigkeit());
		writer.name("indoor");
		writer.value(entity.getIndoor());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<RESTtrackStage> entities) throws IOException {
		writer.beginArray();
		
		for(RESTtrackStage item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
