package hx.http2.client.excutor;

import com.alibaba.fastjson.JSONObject;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @Author mingliang
 * @Date 2017-10-11 15:50
 */
@Service
public class HttpClientExcutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientExcutor.class);

    private static final String UTF_8 = "UTF-8";
    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int SOCKET_TIMEOUT = 25 * 1000;
    private static final int CONNECT_REQUEST_TIMEOUT = 5 * 1000;

    // 创建Httpclient对象
    @Autowired
    private CloseableHttpClient httpClient;

    // 请求信息的配置 Httpclient
    @Autowired
    private RequestConfig requestConfig;

    public void getHttpClient() throws URISyntaxException, IOException, InterruptedException {
//        HttpRequest req = HttpRequest.create(new URI("http://www.infoq.com"))
//                .body(noBody()).GET();
//        CompletableFuture<HttpResponse> aResp = req.sendAsync();
//        Thread.sleep(10);
//        if (!aResp.isDone()) {
//            aResp.cancel(true);
//            System.out.println("Failed to reply quickly...");
//            return;
//        }
//        HttpResponse response = aResp.get();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse httpResponse = httpClient.send(
                HttpRequest.newBuilder(new URI("http://transport.opendata.ch/")).GET().build(),
                HttpResponse.BodyHandler.asString()
        );
        int code = httpResponse.statusCode();
        System.out.println(code);
        System.out.println(httpResponse.body().toString());
    }

    public void postTest() throws URISyntaxException {
//        URI uri = new URI("https://www.baidu.com/");
//        HttpRequest.Builder post = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyProcessor.fromString("###要请求的参数"));
//        //post.setHeader可以设置UA、Cookie等参数
//        post.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1");
//
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpResponse httpResponse = httpClient.send(post.build(), HttpResponse.BodyHandler.asString());
//        int code = httpResponse.statusCode();
//        System.out.println(code);
//        System.out.println(uncompress(httpResponse.body().toString().getBytes()));
    }

    /**
     * 执行Get请求
     *
     * @param url
     * @return 请求到的内容
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String doGet(String url) throws URISyntaxException, ClientProtocolException, IOException {
        return doGet(url, null);
    }

    /**
     * 执行Get请求
     *
     * @param url
     * @param params
     *            请求中的参数
     * @return 请求到的内容
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String doGet(String url, Map<String, Object> params) throws URISyntaxException, ClientProtocolException, IOException {
        // 定义请求的参数
        URI uri = null;
        if (params != null) {
            URIBuilder builder = new URIBuilder(url);
            params.forEach((k,v) ->{
                // @TODO 如果是时间和其他类型的对象参数呢
                builder.addParameter(k,String.valueOf(v));
            });
            uri = builder.build();
        }

        // 创建http GET请求
        HttpGet httpGet = null;
        if (uri != null) {
            httpGet = new HttpGet(uri);
        } else {
            httpGet = new HttpGet(url);
        }
        // 设置请求参数
        httpGet.setConfig(this.requestConfig);

        // 请求的结果
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取服务端返回的数据,并返回
                return EntityUtils.toString(response.getEntity(), UTF_8);
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     *
     * @param url
     * @param params
     *            请求中的参数
     * @return 请求到的内容
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String doPost(String url, Map<String, Object> params) throws URISyntaxException,
            ClientProtocolException, IOException {
        // 设置post参数
        List<NameValuePair> parameters = null;
        // 构造一个form表单式的实体
        UrlEncodedFormEntity formEntity = null;

        // 定义请求的参数
        if (params != null) {
            // 设置post参数
            parameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 添加参数
                parameters.add(new BasicNameValuePair(entry.getKey(), String
                        .valueOf(entry.getValue())));
            }
            // 构造一个form表单式的实体
            formEntity = new UrlEncodedFormEntity(parameters);
        }

        // 创建http POST请求
        HttpPost httpPost = null;
        if (formEntity != null) {
            httpPost = new HttpPost(url);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
            // 伪装浏览器请求
            setHeader(httpPost,setDefualHeader());
        } else {
            httpPost = new HttpPost(url);
            // 伪装浏览器请求
            setHeader(httpPost,setDefualHeader());
        }
        // 设置请求参数
        httpPost.setConfig(this.requestConfig);

        // 请求的结果
        CloseableHttpResponse response = execute(httpPost,null);
        return null;
    }


    /**
     *
     * @param url
     * @param params
     *            请求中的参数
     * @return 请求到的内容
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public CloseableHttpResponse doPostBody(String url, String params) throws Exception {
        // 创建http POST请求
        HttpPost httpPost = null;
        if (params != null) {
            // 设置post参数
            StringEntity s = new StringEntity(params, "utf-8");
            HttpEntity entity = new StringEntity(params);
            httpPost = new HttpPost(url);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(entity);
            // 伪装浏览器请求
            setHeader(httpPost,setDefualHeader());
        } else {
            httpPost = new HttpPost(url);
            // 伪装浏览器请求
            setHeader(httpPost,setDefualHeader());
        }
        // 设置请求参数
        httpPost.setConfig(this.requestConfig);

        // 请求的结果
        CloseableHttpResponse response = execute(httpPost,null);
        return response;
    }


    /**
     *
     * @param url
     *            请求中的参数
     * @return 请求到的内容
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String doPost(String url) throws URISyntaxException, ClientProtocolException, IOException {
        return doPost(url, null);
    }


    /**
     * 处理json格式的body post请求
     *
     * @return
     * @throws Exception
     * @throws ClientProtocolException
     */
    public String processPostJson(String postUrl, JSONObject jsonObj) throws ClientProtocolException, IOException {
        HttpPost post = new HttpPost(postUrl);
        post.setHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Basic YWRtaW46");
        String str = null;
        StringEntity s = new StringEntity(jsonObj.toJSONString(), "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(25000).setConnectTimeout(3000).build();

        post.setEntity(s);
        post.setConfig(requestConfig);

        CloseableHttpResponse response = execute(post,null);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instreams = entity.getContent();
            str = convertStreamToString(instreams);
            post.abort();
        }
        return str;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    private CloseableHttpResponse execute(HttpPost httpPost,HttpGet httpGet) throws IOException {
        CloseableHttpResponse response = null;
        try {
            // 执行请求
//            httpClient = getHttpClient();
            response = httpClient.execute(httpPost);
//            InputStream in=response.getEntity().getContent();
//            String json = IOUtils.;
//            in.close();
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取服务端返回的数据,并返回
                return response;
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    private void setHeader(HttpPost httpPost,Map<String,String> headerMap){
       if (headerMap != null && headerMap.size() > 0){
           for (Map.Entry entry : headerMap.entrySet()){
               httpPost.setHeader((String) entry.getKey(),(String) entry.getValue());
           }
       }
    }

    private Map<String,String> setDefualHeader(){
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        headerMap.put("Content-Type", "application/json");
        return headerMap;
    }

//    private CloseableHttpClient getHttpClient(){
//        return httpConnectionPoolManage.getHttpClient();
//    }

    public  String processHttpRequest(String url, String requestMethod, Map<String, String> paramsMap) {
        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
        if ("post".equals(requestMethod)) {
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Content-Type", "application/json");
            for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                String value = paramsMap.get(key);
                formparams.add(new BasicNameValuePair(key, value));
            }
            return doRequest(httppost, null, formparams);
        } else if ("get".equals(requestMethod)) {
            HttpGet httppost = new HttpGet(url);
            for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                String value = paramsMap.get(key);
                formparams.add(new BasicNameValuePair(key, value));
            }
            return doRequest(null, httppost, formparams);
        }
        return "";
    }

    private String doRequest(HttpPost httpPost, HttpGet httpGet, List<BasicNameValuePair> formparams) {

        try {
            CloseableHttpResponse response = null;
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).
                    setConnectTimeout(CONNECT_TIMEOUT)
                    .build();
            if (null != httpPost) {
                uefEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(uefEntity);
                httpPost.setConfig(requestConfig);
                response = execute(httpPost,null);
            } else {
                httpGet.setConfig(requestConfig);
                response = execute(null,httpGet);
            }
            HttpEntity entity = response.getEntity();
            String str = EntityUtils.toString(entity, "UTF-8");
            if (null == str || "".equals(str)) {
                return "";
            } else {
                return str;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return "";
    }

    /**
     *
     * @param httpRequestBase
     */
    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase
        // .setHeader("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language",
        // "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset",
        // "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpRequestBase.setConfig(requestConfig);
    }
}
