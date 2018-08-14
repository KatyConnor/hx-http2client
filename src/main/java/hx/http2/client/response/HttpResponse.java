package hx.http2.client.response;

import java.io.Serializable;

/**
 * @Author mingliang
 * @Date 2018-08-06 17:12
 */
public class HttpResponse<E extends BaseHttpResponse> implements Serializable {

    private String code;
    private int statusCode;
    private String error;
    private String data;
    private E dataEntity;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public E getDataEntity() {
        return dataEntity;
    }

    public void setDataEntity(E dataEntity) {
        this.dataEntity = dataEntity;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setFailure(){

    }
    public void successful(){

    }

}
