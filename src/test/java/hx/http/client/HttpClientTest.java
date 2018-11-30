package hx.http.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hx.http2.SpringbootApplications;
import hx.http2.client.config.HttpRequestConfig;
import hx.http2.client.entity.ContentType;
import hx.http2.client.enums.RequestMethodEnum;
import hx.http2.client.excutor.HttpClientExcutor;
import hx.http2.client.response.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author mingliang
 * @Date 2018-08-08 10:14
 */
@SpringBootTest(classes = SpringbootApplications.class,properties = "httpclient.properties")
@RunWith(SpringJUnit4ClassRunner.class)
public class HttpClientTest {
    @Autowired
    private HttpClientExcutor httpClientExcutor;

    @Test
    public void testExcute() throws IOException, URISyntaxException {
        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build().
                setHostAddress("https://localhost:8081/webPlatform/").setUrl("/test/rest").setMethod(RequestMethodEnum.GET);
        HttpResponse<String> response = new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));
    }

    @Test
    public void testAmExcute() throws IOException, URISyntaxException {
        Map<String,String> params = new HashMap<>();
        params.put("userName","15826986354");
        params.put("passworld","123456");
        params.put("appcode","APP2018081416120617");
        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build().
                setHostAddress("https://localhost:8085").setUrl("/xyd-am/api/auth?way=json").setMethod(RequestMethodEnum.POST).body(params);

        HttpResponse response = null;//new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));

        ///xyd-am/api/auth    /registration/info
    }


    @Test
    public void testPost() throws IOException, URISyntaxException {
        Map<String,String> params = new HashMap<>();
        params.put("phone","15826986354");
        params.put("password","123456");
        params.put("captcha","F5SB");
        params.put("role","16");

        Map<String,String> reqparams = new HashMap<>();
        reqparams.put("name","李静");
        reqparams.put("age","26");

        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build()
                .setHostAddress("https://localhost:8081")
                .setUrl("/webPlatform/test/reg")
                .setMethod(RequestMethodEnum.POST)
                .body(params)
                .requestParam(reqparams);

        HttpResponse response =new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));


    }

    @Test
    public void testPostJson() throws IOException, URISyntaxException {
        JSONObject params = new JSONObject();
        params.put("phone","15826986354");
        params.put("password","123456");
        params.put("captcha","F5SB");
        params.put("role","16");
        params.put("retest","attributeTest");
        params.put("name","李静");
        params.put("age","26");

        JSONObject reqparams = new JSONObject();
        reqparams.put("name","李静");
        reqparams.put("age","26");
//        reqparams.put("retest","attributeTest");

        Map<String,String> pathVariable = new HashMap<>();
        pathVariable.put("retest","attributeTest");

        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build().
                setHostAddress("https://localhost:8081").setUrl("/webPlatform/test/reg/{retest}").setMethod(RequestMethodEnum.POST).body(params)
                .requestParam(reqparams).setForm(false).setContentType(ContentType.MULTIPART_FORM_DATA)
                .pathVariable(pathVariable);

        HttpResponse response =new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));

    }

    @Test
    public void testPostPortal() throws IOException, URISyntaxException, ParseException {
        JSONObject params = new JSONObject();
        params.put("productNo","17C");
        params.put("startTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-09-10 00:00:0"));
        params.put("endTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-10-22 23:59:59"));

        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build().
                setHostAddress("https://localhost:8081").setUrl("/cooperation/cpportal/userinfo/realBoardLaonAmount").setMethod(RequestMethodEnum.POST).body(params)
                .requestParam(params).setForm(false).setContentType(ContentType.APPLICATION_JSON_UTF_8);

        HttpResponse response =new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));

    }

    public static void main(String[] args) throws ParseException {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(15);
        list.add(6);
        list.add(5);
        System.out.println(JSONArray.toJSONString(list));
        sort(list,new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2?-1:1;
            }
        });
        System.out.println(JSONArray.toJSONString(list));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(yesterday()));

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //sf.format("2018-12-01 00:23:23")
        System.out.println(sf.parse(sf.format(new Date())).getTime() == sf.parse("2018-11-20 00:23:23").getTime());



        BigDecimal urgent = new BigDecimal(3);
        BigDecimal totalUrgent = new BigDecimal(6);
        System.out.println(urgent.divide(totalUrgent).multiply(new BigDecimal(18)).setScale(1,BigDecimal.ROUND_HALF_EVEN));
        BigDecimal sd = new BigDecimal(12.5);
        System.out.println(sd.toString()+"-----"+sd.toString().split("\\.")[1]);

        BigDecimal ds = new BigDecimal(1.0);
        BigDecimal bs = new BigDecimal(3.0);
        Map<String,String> ss = new LinkedHashMap<>();
        System.out.println(ds.divide(bs,10,BigDecimal.ROUND_HALF_EVEN));

    }

    private static void sort(List<Integer> list,Comparator<? super Integer> c) {
        Object[] a = list.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<Integer> i = list.listIterator();
        for (Object e : a) {
            i.next();
            i.set((Integer) e);
        }
    }

    /**
     * 计算两个日期的差， date2 - date1
     *
     * @return int
     * @throws ParseException
     */
    public static Date yesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        return calendar.getTime();

    }
}
