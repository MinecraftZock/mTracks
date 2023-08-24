package info.mx.tracks.rest;

import com.robotoworks.mechanoid.net.JsonEntityWriter;
import com.robotoworks.mechanoid.net.JsonEntityWriterProvider;
import java.io.IOException;
import com.robotoworks.mechanoid.internal.util.JsonWriter;
import java.util.List;


public class BaseResponseWriter extends JsonEntityWriter<BaseResponse> {

	public BaseResponseWriter(JsonEntityWriterProvider provider) {
		super(provider);
	}
	
	public void write(JsonWriter writer, BaseResponse entity) throws IOException {
		writer.beginObject();
		
		writer.name("id");
		writer.value(entity.getId());
		
		writer.endObject();
	}
	
	public void writeList(JsonWriter writer, List<BaseResponse> entities) throws IOException {
		writer.beginArray();
		
		for(BaseResponse item:entities) {
			write(writer, item);
		}
		
		writer.endArray();
	}
}
