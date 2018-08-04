package hx.http2.client.handler;

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

import java.io.IOException;
import java.util.Collection;

/**
 * @Author mingliang
 * @Date 2018-07-27 15:22
 */
public class HXHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {

    public HXHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled, Collection<Class<? extends IOException>> clazzes) {
        super(retryCount, requestSentRetryEnabled, clazzes);
    }

    public HXHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
        super(retryCount, requestSentRetryEnabled);
    }

    public HXHttpRequestRetryHandler() {
    }
}
