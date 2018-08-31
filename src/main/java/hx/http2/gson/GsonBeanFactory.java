package hx.http2.gson;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andy
 * @date 2018-08-31
 */
public class GsonBeanFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(GsonBeanFactory.class);
    private static Gson gson;
    private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Gson builder(){
         gson= new GsonBuilder().registerTypeAdapter(Date.class,( JsonSerializer<Date>)(da, typeOfSrc, context) -> new JsonPrimitive(da.getTime())).setDateFormat(DateFormat.LONG)
                 .registerTypeAdapter(Date.class, (JsonDeserializer<Date>)(json,typeOfT,context)-> {
                     try {
                         return format.parse(json.getAsString());
                     } catch (ParseException e) {

                     }
                     try {
                         return format.parse(format.format(new Date(json.getAsJsonPrimitive().getAsLong())));
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     return null;
                 }).setDateFormat("yyyy-MM-dd HH:mm:ss")
                 .registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss")
                 .registerTypeAdapter(Timestamp.class,( JsonSerializer<Timestamp>)(da, typeOfSrc, context) -> new JsonPrimitive(da.getTime())).setDateFormat("yyyy-MM-dd HH:mm:ss")
                 .serializeNulls().create();
        return gson;
    }

    public static void main(String[] args) {
        builder();
        Map<String,Object> param = new HashMap<>();
        param.put("name","dsfsdfs");
        param.put("add","ffffffff");
        param.put("date",new Timestamp(new Date().getTime()));
        String ss = gson.toJson(param);
        System.out.println(ss);
        Persion persion = gson.fromJson(ss,Persion.class);
        System.out.println("----"+gson.toJson(persion));
    }

}
