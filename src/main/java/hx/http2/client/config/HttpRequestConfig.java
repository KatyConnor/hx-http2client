package hx.http2.client.config;

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
    private Object param;
    private String paramstr;
    private RequestConfig requestConfig;
    private boolean body;
    private ContentType contentType;

    private HttpRequestConfig(){

    }

    public static HttpRequestConfig build(){
        return new HttpRequestConfig();
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

    public HttpRequestConfig setParams(Map<String, Object> params) {
        if (null == this.params){
            this.params = params;
        }else {
            this.params.putAll(params);
        }
        return this;
    }

    public Object getParam() {
        return param;
    }

    public HttpRequestConfig setParam(Object param) {
        if (null == this.params){
            this.params = new HashMap<>();
        }
        try {
            this.params.putAll(BeanMapUtil.beanToMap(param));
        } catch (IllegalAccessException e) {
            LOGGER.error("cast bean to map,exception:{}",e);
        }
        this.param = param;
        return this;
    }

    public String getParamstr() {
        return paramstr;
    }

    public HttpRequestConfig setParamstr(String paramstr) {
        this.paramstr = paramstr;
        return this;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public HttpRequestConfig setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        return this;
    }

    public boolean isBody() {
        return body;
    }

    public HttpRequestConfig setBody(boolean body) {
        this.body = body;
        return this;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public HttpRequestConfig setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;

    }
}
