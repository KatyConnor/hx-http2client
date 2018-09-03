package hx.http2.gson;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectStreamException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Gson 对象获取
 * @author andy
 * @date 2018-08-31
 */
public class GsonBeanFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(GsonBeanFactory.class);
    private final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static DateFormat OBJECT_FORMAT;
    private static String OBJECT_DATE_FORMAT;
    private static boolean isLong = false;
    private static Gson gsonBean;

    private GsonBeanFactory(){}

    public GsonBeanFactory setObjectDateFormat(String dateFormat){
        this.OBJECT_DATE_FORMAT = dateFormat !=null && !dateFormat.isEmpty()?dateFormat:null;
        return this;
    }

    public GsonBeanFactory setIsLong(boolean isLong){
        this.isLong = isLong;
        return this;
    }

    public static GsonBeanFactory newInstance(){
        return GsonBeanNewInstanceFactory.NEW_INSTANCE;
    }

    public Gson builder(){
        OBJECT_FORMAT = new SimpleDateFormat(OBJECT_DATE_FORMAT==null?DEFAULT_DATE_FORMAT:OBJECT_DATE_FORMAT);
        init();
        return gsonBean;
    }

    public GsonBuilder gsonBuilder(){
        return new GsonBuilder();
    }

    /**
     * 防止序列化和反序列
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return GsonBeanNewInstanceFactory.NEW_INSTANCE;
    }

    private static Gson init(){
        gsonBean= new GsonBuilder()
                 .registerTypeAdapter(Timestamp.class,( JsonSerializer<Timestamp>)(da, typeOfSrc, context) ->
                         isLong==true?new JsonPrimitive(da.getTime()):new JsonPrimitive(OBJECT_FORMAT.format(new Date(da.getTime()))))
                 .setDateFormat(OBJECT_DATE_FORMAT == null?DEFAULT_DATE_FORMAT:OBJECT_DATE_FORMAT)
                 .registerTypeAdapter(Date.class,(JsonSerializer<Date>)(da, typeOfSrc, context) ->
                         isLong==true?new JsonPrimitive(da.getTime()):new JsonPrimitive(OBJECT_FORMAT.format(new Date(da.getTime()))))
                 .setDateFormat(OBJECT_DATE_FORMAT == null?DEFAULT_DATE_FORMAT:OBJECT_DATE_FORMAT)
                 .registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>)(json,typeOfT,context)-> {
                     if (!(json instanceof JsonPrimitive)) {
                         throw new JsonParseException("The data should be a string value");
                     }
                     return new Timestamp(new Date(json.getAsString()).getTime());
                 }).setDateFormat(OBJECT_DATE_FORMAT == null?DEFAULT_DATE_FORMAT:OBJECT_DATE_FORMAT)
                 .registerTypeAdapter(Date.class, (JsonDeserializer<Date>)(json,typeOfT,context)-> {
                     try {
                         return OBJECT_FORMAT.parse(json.getAsString());
                     } catch (ParseException e) {
                         LOGGER.info("时间格式为long:{}",json.getAsJsonPrimitive().getAsLong());
                     }
                     return new Date(json.getAsJsonPrimitive().getAsLong());
                 }).setDateFormat(OBJECT_DATE_FORMAT== null?DEFAULT_DATE_FORMAT:OBJECT_DATE_FORMAT)
                 .serializeNulls().create();
        return gsonBean;
    }

    private final static class GsonBeanNewInstanceFactory{
        private final static GsonBeanFactory NEW_INSTANCE = new GsonBeanFactory();
    }

}
