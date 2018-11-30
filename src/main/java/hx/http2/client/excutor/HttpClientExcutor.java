package hx.http2.client.excutor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import hx.http2.client.config.HttpRequestConfig;
import hx.http2.client.entity.HttpHeader;
import hx.http2.client.enums.HttpStatus;
import hx.http2.client.enums.RequestMethodEnum;
import hx.http2.client.exception.HttpConnectException;
import hx.http2.client.response.HttpResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * http请求工具类
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

    @Autowired
    private CookieStore cookieStore;

    @Autowired
    private HttpClientContext httpClientContext;

    // 请求信息的配置 Httpclient
    @Autowired
    private RequestConfig requestConfig;

    public <T> HttpResponse<T> excute(HttpRequestConfig httpRequestConfig,
                                                           Class<T> classzz)
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
        Map<String,String> pathVariable = httpRequestConfig.getPathVariable();
        Map<String, Object> requestParam = httpRequestConfig.getRequestParam();
        Map<String, Object> params = httpRequestConfig.getParams();
        if (null == params){
            params = new HashMap<>();
        }
        if (null != requestParam && requestParam.size() >0){
            params.putAll(requestParam);
        }
        if (null != pathVariable){
            url = setPathVariable(url,pathVariable);
        }
        URI uri = setGetUrl(url,params);
        // 创建http GET请求
        HttpGet httpGet =new HttpGet(uri);
        // 设置请求参数
        HttpHeader header = httpRequestConfig.getHeaders();
        // 伪装浏览器请求
        setHeader(httpGet,null,null != header && null != header.getHeaders()?header.getHeaders():HttpHeader.build().setDfaultHeader());
        // 配置请求的超时设置
        RequestConfig requestConfig = httpRequestConfig.getRequestConfig();
        httpGet.setConfig(null != requestConfig?requestConfig:this.requestConfig);
        // 请求的结果
        return execute(null,httpGet,classzz,httpRequestConfig.getCookieStore());
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
    private <T> HttpResponse<T> doPost(HttpRequestConfig httpRequestConfig,
                                                           Class<T> classzz) throws URISyntaxException,
            ClientProtocolException, IOException {
        LOGGER.info("do post request, requestConfig:{}",JSONObject.toJSONString(httpRequestConfig), SerializerFeature.IgnoreNonFieldGetter);
        // 创建http POST请求
        String url = getUrl(httpRequestConfig.getHostAddress(),httpRequestConfig.getUrl());
        Map<String,Object> requestParam = httpRequestConfig.getRequestParam();
        Map<String,String> pathVariable = httpRequestConfig.getPathVariable();

        if (null != pathVariable){
            url = setPathVariable(url,pathVariable);
        }

        if (null != requestParam){
            url = setPostRequestParam(url,requestParam);
        }
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头参数
        HttpHeader header = httpRequestConfig.getHeaders();
        // 如果没有设置header默认伪装浏览器请求
        setHeader(null,httpPost, null != header && null != header.getHeaders()?header.getHeaders():HttpHeader.build().setDfaultHeader());

        Map<String,Object> params = httpRequestConfig.getParams();
        setParam(params,httpPost,httpRequestConfig.isForm());

        // 配置请求的超时设置
        RequestConfig requestConfig = httpRequestConfig.getRequestConfig();
        httpPost.setConfig(null != requestConfig?requestConfig:this.requestConfig);
        // 请求的结果
        return execute(httpPost,null,classzz,httpRequestConfig.getCookieStore());
    }

    /**
     * 请求执行
     * @param httpPost
     * @param httpGet
     * @param classzz
     * @param <T>
     * @return
     * @throws IOException
     */
    private <T> HttpResponse<T> execute(HttpPost httpPost, HttpGet httpGet,Class<T> classzz,boolean cookie) throws IOException {
        CloseableHttpResponse response = null;
        URI uri = null != httpPost?httpPost.getURI():httpGet.getURI();
        HttpResponse httpResponse = HttpResponse.build().setUri(uri);
        try {
            // 执行请求
            if (cookie){
                response = null != httpPost?httpClient.execute(httpPost,httpClientContext):httpClient.execute(httpGet,httpClientContext);
                httpResponse.setSession((HttpSession) httpClientContext.getAttribute("sessionId"));
            }else {
                response = null != httpPost?httpClient.execute(httpPost):httpClient.execute(httpGet);
            }
            // 获取服务端返回的数据,并返回
            T parseObject = null;
            HttpEntity entity = response.getEntity();
            String resultStr =EntityUtils.toString(entity, UTF_8);
            Header header = entity.getContentType();
            if (null != header && null != header.getValue() && header.getValue().indexOf("text/html") != -1){
                httpResponse.setData(resultStr);
            }else {
                try{
                    parseObject = JSONObject.parseObject(resultStr,classzz);
                    httpResponse.setData(parseObject);
                }catch (Exception e){
                    httpResponse.setData(JSONObject.parse(resultStr));
                }
            }

            httpResponse.setCode(HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            if (response.getStatusLine().getStatusCode() != 200){
                httpResponse.failure(response.getStatusLine().getStatusCode());
                httpResponse.setErrorMessage(response.getStatusLine().getProtocolVersion()+","+
                        response.getStatusLine().getReasonPhrase());
            }else {
                httpResponse.setMessage(response.getStatusLine().getReasonPhrase());
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        LOGGER.info("request service end, response ：{}",JSONObject.toJSONStringWithDateFormat(httpResponse,"yyyy-MM-dd HH:mm:ss"));
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
        return setUrl(url,params);
    }

    private String setPostRequestParam(String url, Map<String, Object> params) throws URISyntaxException{
        URI uri = setUrl(url,params);
        return uri.toString();
    }

    private URI setUrl(String url, Map<String, Object> params) throws URISyntaxException {
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

    private void setParam(Map<String,Object> params,HttpPost httpPost,boolean isform) throws UnsupportedEncodingException {
        if (params != null) {
            // 设置post参数
            final List<NameValuePair> parameters = new ArrayList<>();
            final JSONObject paramsObject = new JSONObject();
            params.forEach((k,v) ->{
                if (isform){
                    parameters.add(new BasicNameValuePair(k,String.valueOf(v)));
                }else {
                    paramsObject.put(k,v);
                }
            });

            // 构造一个form表单式的实体
            if (parameters.size() >0){
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters,"UTF-8");
                httpPost.setEntity(formEntity);
            }
            if (paramsObject.size() >0){
                // 构建一个json，body数据传递
                StringEntity stringEntity = new StringEntity(paramsObject.toJSONString(), Charset.forName("UTF-8"));
                stringEntity.setContentEncoding("UTF-8");
                stringEntity.setContentType("application/json");
                httpPost.setEntity(stringEntity);
            }
        }
    }

    private String setPathVariable(String url,Map<String,String> pathVariable){
        StringBuffer sbUrl = new StringBuffer(url);
        pathVariable.forEach((k,v)->{
            String urls = sbUrl.toString();
            sbUrl.delete(0,sbUrl.length());
            sbUrl.append(urls.replace("{"+k+"}",v));
        });
        return sbUrl.toString();
    }

    public void setCookieStore(org.apache.http.HttpResponse httpResponse,String domain,String path) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("---set CookieStore  ");
        }
        String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
        String JSESSIONID = setCookie.substring("SESSION=".length(),setCookie.indexOf(";"));
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("---set setCookie  {}",setCookie);
        }
        // 新建一个Cookie
        BasicClientCookie cookie = new BasicClientCookie("SESSION", JSESSIONID);
        cookie.setVersion(0);
        cookie.setPath("/");
        cookieStore.getCookies();
        cookieStore.addCookie(cookie);
    }

    private void setContext() {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("---- set Context");
        }
        httpClientContext.setCookieStore(cookieStore);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public HttpClientContext getContext() {
        return httpClientContext;
    }
}
