package hx.http2.client.excutor;

import com.alibaba.fastjson.JSONObject;
import hx.http2.client.config.HttpRequestConfig;
import hx.http2.client.entity.HttpHeader;
import hx.http2.client.enums.HttpStatus;
import hx.http2.client.enums.RequestMethodEnum;
import hx.http2.client.exception.HttpConnectException;
import hx.http2.client.response.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Author mingliang
 * @Date 2017-10-11 15:50
 */
@Service
public class HttpClientExcutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientExcutor.class);
    private static final String UTF_8 = "UTF-8";

    // 创建Httpclient对象
    @Autowired
    private CloseableHttpClient httpClient;

    // 请求信息的配置 Httpclient
    @Autowired
    private RequestConfig requestConfig;

    public HttpResponse excute(HttpRequestConfig httpRequestConfig,
                                                           Class<? extends HttpResponse> classzz)
            throws IOException, URISyntaxException {

        switch (httpRequestConfig.getMethod()){
            case GET:
                return doGet(httpRequestConfig,classzz);
            case POST:
                return doPost(httpRequestConfig,classzz);
            case PUT:
                break;
            case HEAD:
                break;
            case TRACE:
                break;
            case DELETE:
                break;
            case CONNECT:
                break;
            case OPTIONS:
                break;
            default:
                throw new HttpConnectException(String.format("request method is not in method list,method :[s%], methodList:[s%]",
                        httpRequestConfig.getMethod(),RequestMethodEnum.values()));
        }
        return null;
    }


    /**
     * 执行Get请求
     *
     * @param httpRequestConfig
     *            请求中的参数
     * @return 请求到的内容
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    private <T> HttpResponse<T> doGet(HttpRequestConfig httpRequestConfig,
                                                           Class<T> classzz)
            throws URISyntaxException, ClientProtocolException, IOException {

        LOGGER.info("do get request, requestConfig:{}",JSONObject.toJSONString(httpRequestConfig));
        String url = getUrl(httpRequestConfig.getHostAddress(),httpRequestConfig.getUrl());
        URI uri = setGetUrl(url,httpRequestConfig.getParams());
        // 创建http GET请求
        HttpGet httpGet =new HttpGet(uri);
        // 设置请求参数
        Map<String,String> headers = httpRequestConfig.getHeaders().getHeaders();
        if (null != headers){
            headers.forEach((k,v) ->{
                httpGet.setHeader(k,v);
            });
        }else {
            // 伪装浏览器请求
            setHeader(httpGet,null,HttpHeader.build().setDfaultHeader());
        }
        // 配置请求的超时设置
        RequestConfig requestConfig = httpRequestConfig.getRequestConfig();
        httpGet.setConfig(null != requestConfig?requestConfig:this.requestConfig);
        // 请求的结果
        HttpResponse httpResponse = execute(null,httpGet,classzz);
        LOGGER.info("request service end, response:{}",JSONObject.toJSONString(httpResponse));
        return httpResponse;
    }

    /**
     * post 请求
     * @param httpRequestConfig
     * @param classzz
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    private HttpResponse doPost(HttpRequestConfig httpRequestConfig,
                                                           Class<? extends HttpResponse> classzz) throws URISyntaxException,
            ClientProtocolException, IOException {
        LOGGER.info("do post request, requestConfig:{}",JSONObject.toJSONString(httpRequestConfig));
        // 创建http POST请求
        String url = getUrl(httpRequestConfig.getHostAddress(),httpRequestConfig.getUrl());
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头参数
        Map<String,String> headers = httpRequestConfig.getHeaders().getHeaders();
        // 如果没有设置header默认伪装浏览器请求
        setHeader(null,httpPost, null != headers?headers:HttpHeader.build().setDfaultHeader());

        Map<String,Object> params = httpRequestConfig.getParams();
        final List<NameValuePair> parameters;
        final JSONObject paramsObject;
        if (params != null) {
            // 设置post参数
            parameters = new ArrayList<>();
            paramsObject = new JSONObject();
            params.forEach((k,v) ->{
                if (httpRequestConfig.isForm()){
                    parameters.add(new BasicNameValuePair(k,String.valueOf(v)));
                }else {
                    paramsObject.put(k,String.valueOf(v));
                }
            });

            // 构造一个form表单式的实体
            if (null != parameters){
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
                httpPost.setEntity(formEntity);
            }
            if (null != paramsObject){
                // 构建一个json，body数据传递
                StringEntity stringEntity = new StringEntity(paramsObject.toJSONString(), Charset.forName("UTF-8"));
                httpPost.setEntity(stringEntity);
            }

        }

        // 配置请求的超时设置
        RequestConfig requestConfig = httpRequestConfig.getRequestConfig();
        httpPost.setConfig(null != requestConfig?requestConfig:this.requestConfig);
        // 请求的结果
        return execute(httpPost,null,classzz);
    }

    private <T> HttpResponse<T> execute(HttpPost httpPost,HttpGet httpGet,
                                                             Class<T> classzz) throws IOException {
        CloseableHttpResponse response = null;
        HttpResponse httpResponse = new HttpResponse();
        try {
            // 执行请求
            response = null != httpPost?httpClient.execute(httpPost):httpClient.execute(httpGet);
            // 获取服务端返回的数据,并返回
            T parseObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), UTF_8),classzz);
            httpResponse.setCode(HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            httpResponse.setData(parseObject);
            if (response.getStatusLine().getStatusCode() != 200){
                httpResponse.failure();
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return httpResponse;
    }

    private void setHeader(HttpGet httpGet,HttpPost httpPost,Map<String,String> headerMap){
        if (headerMap != null && headerMap.size() > 0){
            if (null != httpGet){
                headerMap.forEach((k,v) ->{
                    httpGet.setHeader(k,v);
                });
            }

            if (null != httpPost){
                headerMap.forEach((k,v) ->{
                    httpPost.setHeader(k,v);
                });
            }
        }
    }

    private URI setGetUrl(String url, Map<String, Object> params) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        if (params != null) {
            params.forEach((k,v) ->{
                // @TODO 如果是时间和其他类型的对象参数需要进行修改
                builder.addParameter(k,String.valueOf(v));
            });
            return builder.build();
        }
        return builder.build();
    }

    private String getUrl(String hostAddress,String requestUrl){
        if (null == hostAddress || hostAddress.isEmpty()){
            throw new HttpConnectException(String.format("request HostAddress can not be null! HostAddress:s%",hostAddress));
        }

        if (null == requestUrl || requestUrl.isEmpty()){
            throw new HttpConnectException(String.format("request url can not be null! url:s%",requestUrl));
        }

        return new StringBuffer().append(hostAddress.endsWith("/")?hostAddress:hostAddress+"/").
                append(requestUrl.startsWith("/")?requestUrl.substring(1):requestUrl).toString();
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

}
