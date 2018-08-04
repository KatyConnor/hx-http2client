package hx.http2.client;

import hx.http2.client.enums.RequestMethodEnum;

import java.util.Map;

/**
 *  请求参数配置
 * @Author mingliang
 * @Date 2018-08-01 18:12
 */
public class RequestConfig {

    private RequestMethodEnum method;
    private String hostAddress;
    private String url;
    private Map<String,Object> headers;
    private String dataType;
//    private

}
