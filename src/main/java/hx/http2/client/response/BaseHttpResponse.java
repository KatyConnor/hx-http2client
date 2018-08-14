package hx.http2.client.response;

import java.io.Serializable;

/**
 * @Author mingliang
 * @Date 2018-08-06 17:16
 */
public abstract class BaseHttpResponse implements Serializable {

    private String code;
    private String data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
