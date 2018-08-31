package hx.http.client;

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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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

        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build().
                setHostAddress("https://localhost:8081").setUrl("/webPlatform/test/reg").setMethod(RequestMethodEnum.POST).body(params).requestParam(reqparams);

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

        JSONObject reqparams = new JSONObject();
        reqparams.put("name","李静");
        reqparams.put("age","26");
        reqparams.put("retest","attributeTest");

        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build().
                setHostAddress("https://localhost:8081").setUrl("/webPlatform/test/reg/%s").setMethod(RequestMethodEnum.GET).body(params)
                .requestParam(reqparams).setForm(false).setContentType(ContentType.MULTIPART_FORM_DATA);

        HttpResponse response =new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));


    }
}
