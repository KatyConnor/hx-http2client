package hx.http2.client.config;

import com.alibaba.fastjson.JSONObject;
import hx.http2.client.entity.ContentType;
import hx.http2.client.entity.HttpHeader;
import hx.http2.client.enums.RequestMethodEnum;
import hx.http2.client.utils.BeanMapUtil;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author mingliang
 * @Date 2018-08-06 17:43
 */
public class HttpRequestConfig implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestConfig.class);

    private RequestMethodEnum method;
    private String hostAddress;
    private String url;
    private HttpHeader headers;
    private Map<String,Object> params;
    private Map<String,Object> requestParam;
    private RequestConfig requestConfig;
    private boolean form;
    private ContentType contentType;

    private HttpRequestConfig(){}

    public static HttpRequestConfig build(){
        return new HttpRequestConfig();
    }

    public HttpRequestConfig bodyStr(Map<String,String> paramMap){
        checkMap(this.params);
        this.params.putAll(paramMap);
        return this;
    }

    public HttpRequestConfig body(Map<String,Object> paramMap){
        checkMap(this.params);
        this.params.putAll(paramMap);
        return this;
    }

    public HttpRequestConfig body(Object param){
        checkMap(this.params);
        if (param instanceof JSONObject){
            this.params.putAll((JSONObject)param);
            return this;
        }

        try {
            this.params.putAll(BeanMapUtil.beanToMap(param));
        } catch (IllegalAccessException e) {
            LOGGER.error("cast bean to map,exception:{}",e);
        }
        return this;
    }

    public HttpRequestConfig body(String jsonParam){
        JSONObject object = null;
        try{
            object = JSONObject.parseObject(jsonParam);
        }catch (Exception e){
            LOGGER.error("param string is not json string, jsonParam:{}",jsonParam);
        }
        checkMap(this.params);
        this.params.putAll(object);
        return this;
    }

    public HttpRequestConfig body(String key,Object value){
        checkMap(this.params);
        this.params.put(key,value);
        return this;
    }

    public HttpRequestConfig requestParamStr(Map<String,String> paramMap){
        checkMap(this.requestParam);
        this.requestParam.putAll(paramMap);
        return this;
    }

    public HttpRequestConfig requestParam(Map<String,Object> paramMap){
        checkMap(this.requestParam);
        this.requestParam.putAll(paramMap);
        return this;
    }

    public HttpRequestConfig requestParam(Object param){
        checkMap(this.requestParam);
        if (param instanceof JSONObject){
            this.requestParam.putAll((JSONObject)param);
            return this;
        }

        try {
            this.requestParam.putAll(BeanMapUtil.beanToMap(param));
        } catch (IllegalAccessException e) {
            LOGGER.error("cast bean to map,exception:{}",e);
        }
        return this;
    }

    public HttpRequestConfig requestParam(String jsonParam){
        JSONObject object = null;
        try{
            object = JSONObject.parseObject(jsonParam);
        }catch (Exception e){
            LOGGER.error("param string is not json string, jsonParam:{}",jsonParam);
        }
        checkMap(this.requestParam);
        this.requestParam.putAll(object);
        return this;
    }

    public HttpRequestConfig requestParam(String key,String value){
        checkMap(this.requestParam);
        this.requestParam.put(key,value);
        return this;
    }

    public RequestMethodEnum getMethod() {
        return method;
    }

    public HttpRequestConfig setMethod(RequestMethodEnum method) {
        this.method = method;
        return this;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public HttpRequestConfig setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public HttpRequestConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpHeader getHeaders() {
        return headers;
    }

    public HttpRequestConfig setHeaders(HttpHeader headers) {
        this.headers = headers;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public HttpRequestConfig setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        return this;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public HttpRequestConfig setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;

    }

    public boolean isForm() {
        return form;
    }

    public void setForm(boolean form) {
        this.form = form;
    }

    public Map<String, Object> getRequestParam() {
        return requestParam;
    }

    private void checkMap(Map<String,Object> map){
        if (null == map){
            map = new HashMap<>();
        }
    }
}
