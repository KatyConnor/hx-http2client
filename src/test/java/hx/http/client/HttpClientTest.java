package hx.http.client;

import com.alibaba.fastjson.JSONObject;
import hx.http2.client.SpringbootApplications;
import hx.http2.client.config.HttpRequestConfig;
import hx.http2.client.enums.RequestMethodEnum;
import hx.http2.client.excutor.HttpClientExcutor;
import hx.http2.client.response.BaseHttpResponse;
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
                setHostAddress("https://localhost:8081/webPlatform/").setUrl("/test/rests").setMethod(RequestMethodEnum.GET);
        HttpResponse<BaseHttpResponse> response = new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));
    }

    @Test
    public void testAmExcute() throws IOException, URISyntaxException {
        Map<String,Object> params = new HashMap<>();
//        params.put("serialNO","\"sdfsf\"");
//        params.put("sign","\"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKmV4CoRGZfvBl8Nh3OaFlOq/KF/zWU9hxwUjPcLtqGPdISr3i1h1rMho+gyhyeauJmk80ovYMGusyevef2OAWXet76u3kRjgxMR3t2bIAplCN2gYAOhEnwkTwhpnosYi9HL3+cIJ2n7zkNViStpejsOvGLSXh6Q84VDQMsTrw+lAgMBAAECgYBeBYFd8VMeQGBo7MhPWHvOcXtVUtUer0ksUVyCT5U256EzOVKaGKd/c0Q7pV/3njmAvcpBNBlR6LjdZNWetU+iMxkG1kEiZXlYhAueatMN5AUj8D+iy7gI2zWKIhNYmQQ0qabxhuy76vCKMBbByP94i3/+fiaEMQCnAGzQYyKVYQJBANETNroO8M8VURpTB+b/bEfxEayTRRXI4lPhnPkmP5Z/GPNTe3H8lZf4xrpYQnN9xdppGcYGbsu+iqrtPJoKxTkCQQDPpbcWym6qZ9bpH3/JfkThmaHhcSxKQ+zUhKHiDD1bpVLnpJI2EjwYf+y5gij/5b8HIaKqpE/DmDjylMdMQSnNAkEAzDkIbBXMA3u/DQet4GX/TwabTQdNpEnlSipHOtexpT/hD02sd1tShG/tLnin8Egay41+L4B5GWu15+B0IcQDgQJBALimrcz3kTe6BZPjbfiZb9mKjDj1MiSMiMNneqmQptOjjbjuURMCXiyPPcytoZYGETzfPU8vtLrD2AhoI159KBUCQC7/BZzVK7wuIysspM3pJDwbJ3izETzYxPLuLYdyZDMmEiH2YIRonZ04U9oSvscAFivpeQz6rDDUfTrf2plhYp8=\"");
//        params.put("noneStr","sdfsdf");
//        params.put("securityCode","2ce49be5c78237dcf607af8a2675dffb");
        params.put("userName","15826986354");
        params.put("passworld","123456");
        params.put("appcode","APP2018081416120617");
        HttpRequestConfig httpRequestConfig = HttpRequestConfig.build().
                setHostAddress("https://localhost:8085").setUrl("/xyd-am/api/auth?way=json").setMethod(RequestMethodEnum.POST).setParams(params);
        HttpResponse<BaseHttpResponse> response = new HttpResponse<>();
        HttpResponse excute = httpClientExcutor.excute(httpRequestConfig, response.getClass());
        System.out.println(JSONObject.toJSONString(excute));

        ///xyd-am/api/auth    /registration/info
    }
}
