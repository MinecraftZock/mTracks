package info.mx.core_generated.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class RESTtrackStageReader extends JsonEntityReader<RESTtrackStage> {			
	
	public RESTtrackStageReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, RESTtrackStage entity) throws IOException {
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
			
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
			
			if(name.equals("id")) {
				entity.setId(reader.nextInt());
			}
			else if(name.equals("trackId")) {
				entity.setTrackId(reader.nextInt());
			}
			else if(name.equals("androidid")) {
				entity.setAndroidid(reader.nextString());
			}
			else if(name.equals("approved")) {
				entity.setApproved(reader.nextInt());
			}
			else if(name.equals("country")) {
				entity.setCountry(reader.nextString());
			}
			else if(name.equals("changed")) {
				entity.setChanged(reader.nextLong());
			}
			else if(name.equals("trackname")) {
				entity.setTrackname(reader.nextString());
			}
			else if(name.equals("longitude")) {
				entity.setLongitude(reader.nextDouble());
			}
			else if(name.equals("latitude")) {
				entity.setLatitude(reader.nextDouble());
			}
			else if(name.equals("ins_longitude")) {
				entity.setInsLongitude(reader.nextDouble());
			}
			else if(name.equals("ins_latitude")) {
				entity.setInsLatitude(reader.nextDouble());
			}
			else if(name.equals("ins_distance")) {
				entity.setInsDistance(reader.nextInt());
			}
			else if(name.equals("url")) {
				entity.setUrl(reader.nextString());
			}
			else if(name.equals("fees")) {
				entity.setFees(reader.nextString());
			}
			else if(name.equals("phone")) {
				entity.setPhone(reader.nextString());
			}
			else if(name.equals("notes")) {
				entity.setNotes(reader.nextString());
			}
			else if(name.equals("contact")) {
				entity.setContact(reader.nextString());
			}
			else if(name.equals("licence")) {
				entity.setLicence(reader.nextString());
			}
			else if(name.equals("kidstrack")) {
				entity.setKidstrack(reader.nextInt());
			}
			else if(name.equals("openmondays")) {
				entity.setOpenmondays(reader.nextInt());
			}
			else if(name.equals("opentuesdays")) {
				entity.setOpentuesdays(reader.nextInt());
			}
			else if(name.equals("openwednesday")) {
				entity.setOpenwednesday(reader.nextInt());
			}
			else if(name.equals("openthursday")) {
				entity.setOpenthursday(reader.nextInt());
			}
			else if(name.equals("openfriday")) {
				entity.setOpenfriday(reader.nextInt());
			}
			else if(name.equals("opensaturday")) {
				entity.setOpensaturday(reader.nextInt());
			}
			else if(name.equals("opensunday")) {
				entity.setOpensunday(reader.nextInt());
			}
			else if(name.equals("hoursmonday")) {
				entity.setHoursmonday(reader.nextString());
			}
			else if(name.equals("hourstuesday")) {
				entity.setHourstuesday(reader.nextString());
			}
			else if(name.equals("hourswednesday")) {
				entity.setHourswednesday(reader.nextString());
			}
			else if(name.equals("hoursthursday")) {
				entity.setHoursthursday(reader.nextString());
			}
			else if(name.equals("hoursfriday")) {
				entity.setHoursfriday(reader.nextString());
			}
			else if(name.equals("hourssaturday")) {
				entity.setHourssaturday(reader.nextString());
			}
			else if(name.equals("hourssunday")) {
				entity.setHourssunday(reader.nextString());
			}
			else if(name.equals("tracklength")) {
				entity.setTracklength(reader.nextInt());
			}
			else if(name.equals("soiltype")) {
				entity.setSoiltype(reader.nextInt());
			}
			else if(name.equals("camping")) {
				entity.setCamping(reader.nextInt());
			}
			else if(name.equals("shower")) {
				entity.setShower(reader.nextInt());
			}
			else if(name.equals("cleaning")) {
				entity.setCleaning(reader.nextInt());
			}
			else if(name.equals("electricity")) {
				entity.setElectricity(reader.nextInt());
			}
			else if(name.equals("supercross")) {
				entity.setSupercross(reader.nextInt());
			}
			else if(name.equals("trackaccess")) {
				entity.setTrackaccess(reader.nextString());
			}
			else if(name.equals("facebook")) {
				entity.setFacebook(reader.nextString());
			}
			else if(name.equals("adress")) {
				entity.setAdress(reader.nextString());
			}
			else if(name.equals("feescamping")) {
				entity.setFeescamping(reader.nextString());
			}
			else if(name.equals("daysopen")) {
				entity.setDaysopen(reader.nextString());
			}
			else if(name.equals("noiselimit")) {
				entity.setNoiselimit(reader.nextString());
			}
			else if(name.equals("campingrvhookups")) {
				entity.setCampingrvhookups(reader.nextInt());
			}
			else if(name.equals("singletrack")) {
				entity.setSingletrack(reader.nextInt());
			}
			else if(name.equals("mxtrack")) {
				entity.setMxtrack(reader.nextInt());
			}
			else if(name.equals("a4x4")) {
				entity.setA4x4(reader.nextInt());
			}
			else if(name.equals("enduro")) {
				entity.setEnduro(reader.nextInt());
			}
			else if(name.equals("utv")) {
				entity.setUtv(reader.nextInt());
			}
			else if(name.equals("quad")) {
				entity.setQuad(reader.nextInt());
			}
			else if(name.equals("trackstatus")) {
				entity.setTrackstatus(reader.nextString());
			}
			else if(name.equals("areatype")) {
				entity.setAreatype(reader.nextString());
			}
			else if(name.equals("schwierigkeit")) {
				entity.setSchwierigkeit(reader.nextInt());
			}
			else if(name.equals("indoor")) {
				entity.setIndoor(reader.nextInt());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<RESTtrackStage> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			RESTtrackStage item = new RESTtrackStage();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
