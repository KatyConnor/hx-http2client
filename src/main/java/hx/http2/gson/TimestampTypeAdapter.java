package hx.http2.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author andy
 * @date 2018-08-31
 */
public class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Timestamp deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(jsonElement instanceof JsonPrimitive)) {
            throw new JsonParseException("The data should be a string value");
        }
        return new Timestamp(new Date(jsonElement.getAsString()).getTime());
    }

    @Override
    public JsonElement serialize(Timestamp timestamp, Type typeOfSrc, JsonSerializationContext context) {
        String dataFormatAsString = format.format(new Date(timestamp.getTime()));
        return new JsonPrimitive(dataFormatAsString);
    }
}
