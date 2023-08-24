package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityReader;
import com.robotoworks.mechanoid.net.JsonEntityReaderProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonReader;
import java.util.List;
import com.robotoworks.mechanoid.internal.util.JsonToken;

public class BaseResponseReader extends JsonEntityReader<BaseResponse> {			
	
	public BaseResponseReader(JsonEntityReaderProvider provider) {
		super(provider);
	}
	
	public void read(JsonReader reader, BaseResponse entity) throws IOException {
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
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
	}
	
	public void readList(JsonReader reader, List<BaseResponse> entities) throws IOException {
		reader.beginArray();
		
		while(reader.hasNext()) {
			BaseResponse item = new BaseResponse();
			read(reader, item);
			entities.add(item);
			
		}
		
		reader.endArray();
	}
}
