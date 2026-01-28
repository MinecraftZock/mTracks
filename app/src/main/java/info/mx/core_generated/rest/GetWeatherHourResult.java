package info.mx.core_generated.rest;


import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.net.ServiceResult;
import java.io.InputStream;
import com.robotoworks.mechanoid.util.Closeables;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import com.robotoworks.mechanoid.internal.util.JsonToken;
import java.util.List;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GetWeatherHourResult extends ServiceResult {
	private int cod;
	private double message;
	private City city;
	private String country;
	private int population;
	private int cnt;
	private List<TimeHour> list;
	public int getCod(){
		return this.cod;
	}
	public void setCod(int value){
		this.cod = value;
	}
	public double getMessage(){
		return this.message;
	}
	public void setMessage(double value){
		this.message = value;
	}
	public City getCity(){
		return this.city;
	}
	public void setCity(City value){
		this.city = value;
	}
	public String getCountry(){
		return this.country;
	}
	public void setCountry(String value){
		this.country = value;
	}
	public int getPopulation(){
		return this.population;
	}
	public void setPopulation(int value){
		this.population = value;
	}
	public int getCnt(){
		return this.cnt;
	}
	public void setCnt(int value){
		this.cnt = value;
	}
	public List<TimeHour> getList(){
		return this.list;
	}
	public void setList(List<TimeHour> value){
		this.list = value;
	}
	
	public GetWeatherHourResult(JsonEntityReaderProvider provider, InputStream inStream) throws IOException {
		JsonReader reader = null;
		try {
			if(inStream != null) {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset())));
		GetWeatherHourResult subject = this;
		reader.beginObject();
		
		while(reader.hasNext()) {
			String name = reader.nextName();
		
			if(reader.peek() == JsonToken.NULL) {
				reader.skipValue();
				continue;
			}
						
			if(name.equals("country")) {
				subject.setCountry(reader.nextString());
			}
			else if(name.equals("city")) {
				City entityMember = new City();
				provider.get(City.class).read(reader, entityMember);
				subject.setCity(entityMember);
			}
			else if(name.equals("cnt")) {
				subject.setCnt(reader.nextInt());
			}
			else if(name.equals("cod")) {
				subject.setCod(reader.nextInt());
			}
			else if(name.equals("message")) {
				subject.setMessage(reader.nextDouble());
			}
			else if(name.equals("list")) {
				List<TimeHour> entityMember = new ArrayList<TimeHour>();
				provider.get(TimeHour.class).readList(reader, entityMember);
				subject.setList(entityMember);
			}
			else if(name.equals("population")) {
				subject.setPopulation(reader.nextInt());
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
		}
	} finally {
		Closeables.closeSilently(reader);
	}
	}
}
