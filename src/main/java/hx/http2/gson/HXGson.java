package hx.http2.gson;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andy
 * @date 2018-09-03
 */
public class HXGson {

    private final static Logger LOGGER = LoggerFactory.getLogger(HXGson.class);

    public static HXGson builder(){
        return new HXGson();
    }

    public static void main(String[] args) {
        Gson gsonBean = GsonBeanFactory.newInstance().setIsLong(false).setObjectDateFormat("yyyy-MM-dd HH:mm").builder();
        Map<String,Object> param = new HashMap<>();
        param.put("name","dsfsdfs");
        param.put("add","ffffffff");
        param.put("date",new Timestamp(new Date().getTime()));
        String ss = gsonBean.toJson(param);
        System.out.println(ss);
        Persion persion = gsonBean.fromJson(ss,Persion.class);
        System.out.println("----"+gsonBean.toJson(persion));
    }
}
