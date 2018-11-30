package hx.http2.client.response;

import hx.http2.client.enums.HttpStatus;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.net.URI;

/**
 * @Author mingliang
 * @Date 2018-08-06 17:12
 */
public class HttpResponse<T> implements Serializable {
    /** 状态码 */
    private HttpStatus code;
    /** 业务状态码 */
    private String statusCode;
    /** 通信状态 */
    private boolean success;
    /** 业务状态 */
    private boolean process;
    /** 返回数据 */
    private T data;
    /** 返回描述 */
    private String message;
    /** 授权token */
    private String authorizedToken;
    /** 错误码 */
    private Object errorCode;
    /** 错误描述 */
    private String errorMessage;
    /** 请求地址 */
    private URI uri;
    /** HTTP2 */
    private java.net.http.HttpResponse httpResponse;
    /** */
    private HttpSession session;
    /** */
    private String cookie;

    public static <T> HttpResponse<T> build(){
        return new HttpResponse<>();
    }

    public HttpResponse<T> statusCode(int statusCode){
        switch (HttpStatus.resolve(statusCode)){
            case OK:
                successful();
                break;
            case NOT_FOUND:
                notFound();
                break;
            default:
                failure(statusCode);
                break;
        }
        return this;
    }

    public HttpResponse(){
        successful();
    }

    public HttpStatus getCode() {
        return code;
    }

    public HttpResponse<T> setCode(HttpStatus code) {
        this.code = code;
        return this;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public HttpResponse<T> setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public HttpResponse<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public boolean isProcess() {
        return process;
    }

    public HttpResponse<T> setProcess(boolean process) {
        this.process = process;
        return this;
    }

    public T getData() {
        return data;
    }

    public HttpResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public HttpResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getAuthorizedToken() {
        return authorizedToken;
    }

    public HttpResponse<T> setAuthorizedToken(String authorizedToken) {
        this.authorizedToken = authorizedToken;
        return this;
    }

    public Object getErrorCode() {
        return errorCode;
    }

    public HttpResponse<T> setErrorCode(Object errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpResponse<T> setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public HttpResponse<T> setHttpResponse(java.net.http.HttpResponse httpResponse){
        this.httpResponse = httpResponse;
        statusCode(httpResponse.statusCode());
        return this;
    }

    public URI getUri() {
        return uri;
    }

    public HttpResponse<T> setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public HttpSession getSession() {
        return session;
    }

    public HttpResponse setSession(HttpSession session) {
        this.session = session;
        return this;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public HttpResponse<T> successful(){
        this.code = HttpStatus.OK;
        this.statusCode = String.valueOf(HttpStatus.OK.value());
        this.success = true;
        this.process=true;
        this.message = "调用成功";
        return this;
    }
    public HttpResponse<T> notFound(){
        this.code = HttpStatus.NOT_FOUND;
        this.statusCode = String.valueOf(HttpStatus.NOT_FOUND.value());
        this.success = false;
        this.process=false;
        this.errorMessage = HttpStatus.NOT_FOUND.getReasonPhrase();
        this.message = String.format("url= [%s] 路径不存在",uri.getPath());
        return this;
    }

    public HttpResponse<T> failure(int statusCode){
        this.code = HttpStatus.valueOf(statusCode);
        this.statusCode = String.valueOf(statusCode);
        this.success = true;
        this.process=false;
        this.message = "调用失败";
        this.errorMessage = HttpStatus.valueOf(statusCode).getReasonPhrase();
        return this;
    }
}
