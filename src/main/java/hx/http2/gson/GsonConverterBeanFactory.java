package hx.http2.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonRequestBodyConverter;
//import retrofit2.converter.gson.GsonResponseBodyConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author andy
 * @date 2018-08-31
 */
public class GsonConverterBeanFactory extends Converter.Factory {

    private final Gson gson;

    public static GsonConverterBeanFactory create() {
        return create(new Gson());
    }

    public static GsonConverterBeanFactory create(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        } else {
            return new GsonConverterBeanFactory(gson);
        }
    }

    private GsonConverterBeanFactory(Gson gson) {
        this.gson = gson;
    }

//    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//        TypeAdapter<?> adapter = this.gson.getAdapter(TypeToken.get(type));
//        return new GsonResponseBodyConverter(this.gson, adapter);
//    }
//
//    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//        TypeAdapter<?> adapter = this.gson.getAdapter(TypeToken.get(type));
//        return new GsonRequestBodyConverter(this.gson, adapter);
//    }
}
