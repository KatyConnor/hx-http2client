package hx.http2.client.response;

import java.io.Serializable;

/**
 * @Author mingliang
 * @Date 2018-08-06 17:12
 */
public class HttpResponse implements Serializable {

    /** 状态码 */
    private String code;
    /** 业务状态码 */
    private String statusCode;
    /** 错误码 */
    private String errorCode;
    /** 错误描述 */
    private String errorMessage;
    /** 返回数据 */
    private String data;
    /** 返回描述 */
    private String msg;
    /** 授权token */
    private String authorizedToken;



}
