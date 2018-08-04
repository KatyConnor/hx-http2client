package hx.http2.client.config;

import hx.http2.client.handler.HXHttpRequestRetryHandler;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * spring ioc 容器管理的bean配置, httpclient配置
 * @Author mingliang
 * @Date 2017-09-29 20:42
 */
@Component
@PropertySource({"classpath:httpclient.properties"})
public class HttpClientPoolBeanConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientPoolBeanConfig.class);
//    private final static String SYSTEM_PROPERTIES_MODE_NAME = "SYSTEM_PROPERTIES_MODE_OVERRIDE";
    private static final Set<Class<? extends IOException>> nonRetriableClasses = new HashSet<>();

    //最大连接数
    @Value("${http.maxTotal}")
    private int maxTotal;

    @Value("${http.maxTotal}")
    private int defaultMaxPerRoute;

    //连接的最长时间
    @Value("${http.connectTimeout}")
    private int connectTimeout;

    //从连接池中获取到连接的最长时间
    @Value("${http.connectionRequestTimeout}")
    private int connectionRequestTimeout;

    //数据传输的最长时间
    @Value("${http.socketTimeout}")
    private int socketTimeout;

    //提交请求前测试连接是否可用
    @Value("${http.staleConnectionCheckEnabled}")
    private boolean staleConnectionCheckEnabled;

    @Value("${http.retryCount}")
    private int retryCount;

    @Value("${http.requestSentRetryEnabled}")
    private boolean requestSentRetryEnabled;

    @Value("${http.maxIdleTime}")
    private long maxIdleTime;

    // 异常情况
    static {
        nonRetriableClasses.add(NoHttpResponseException.class);  // 如果服务器丢掉了连接
        nonRetriableClasses.add(SSLHandshakeException.class);  // 不要重试SSL握手异常
        nonRetriableClasses.add(InterruptedIOException.class);  // 超时
        nonRetriableClasses.add(UnknownHostException.class);  // 目标服务器不可达
        nonRetriableClasses.add(ConnectTimeoutException.class); // 连接被拒绝
        nonRetriableClasses.add(ConnectException.class); // 连接被拒绝
        nonRetriableClasses.add(SSLException.class); // ssl握手异常
    }

    /**
     * 定义连接管理器,创建httpclient的连接池
     * @return
     */
    @Bean(name = "httpClientConnectionManager", destroyMethod = "close")
    public PoolingHttpClientConnectionManager initHttpPool(@Qualifier("socketConfig") SocketConfig socketConfig){
        SSLContextBuilder sslContext = new SSLContextBuilder();
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;
        try {
            sslContext.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            LayeredConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext.build(),
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            // 配置同时支持 HTTP 和 HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("https", sslsf)
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();
            poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            poolingHttpClientConnectionManager.setMaxTotal(maxTotal); // 将最大连接数增加
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute); // 将每个路由基础的连接增加

            // 将目标主机的最大连接数添加
            poolingHttpClientConnectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("127.0.0.1",8080)), 30);
            poolingHttpClientConnectionManager.setSocketConfig(new HttpHost("127.0.0.1",8080),socketConfig);
        } catch (Exception e) {
            LOGGER.error("init SSLContext error，exception:{}",e);
        }
        return poolingHttpClientConnectionManager;
    }

    /**
     * 定义请求重试
     * @return
     */
    @Bean(name = "httpRequestRetryHandler")
    public DefaultHttpRequestRetryHandler defaultHttpRequestRetryHandler() {
        //请求重试处理,自定义那些异常不需要重试
        return new HXHttpRequestRetryHandler(retryCount, requestSentRetryEnabled,nonRetriableClasses);
    }

    /**
     *
     * httpclient对象构建器
     * 建议此处使用HttpClients.custom的方式来创建HttpClientBuilder，而不要使用HttpClientBuilder.create()方法来创建HttpClientBuilder
     * 从官方文档可以得出，HttpClientBuilder是非线程安全的，但是HttpClients确实Immutable的，immutable
     * 对象不仅能够保证对象的状态不被改变，
     * 而且还可以不使用锁机制就能被其他线程共享
     *
     * 创建httpClient对象,httpClient是由HttpClientBuilder通过build方法创建，这个可以设置连接池,
     * 创建HttpClientBuilder
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder createHttpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager connManager,
                                                     @Qualifier("httpRequestRetryHandler") DefaultHttpRequestRetryHandler retryHandler,
                                                     @Qualifier("requestConfig") RequestConfig requestConfig,
                                                     @Qualifier("connectionKeepAliveStrategy") ConnectionKeepAliveStrategy keepAliveStrategy,
                                                     @Qualifier("socketConfig") SocketConfig socketConfig){
        return HttpClients.custom().setConnectionManager(connManager).setRetryHandler(retryHandler)
                .setKeepAliveStrategy(keepAliveStrategy).setDefaultRequestConfig(requestConfig).setDefaultSocketConfig(socketConfig);
    }

    @Bean(name = "connectionKeepAliveStrategy")
    public ConnectionKeepAliveStrategy keepAliveStrategy(){
        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase
                            ("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return 60 * 1000;//如果没有约定，则默认定义时长为60s
            }
        };
        return myStrategy;
    }

    /**
     * 定义Httpclient对象
     * @return
     */
    @Bean(name = "httpClient")
    public CloseableHttpClient httpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    /**
     * 定义清理无效连接
     * @param connManager
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public IdleConnectionEvictor initIdleConnectionEvictor(@Qualifier("httpClientConnectionManager")
                                                                       PoolingHttpClientConnectionManager connManager){
        return new IdleConnectionEvictor(connManager, maxIdleTime, TimeUnit.MINUTES);
    }


    @Bean
    @Primary
    public RestTemplate restTemplate(RestTemplateBuilder builder,@Qualifier("httpClient") CloseableHttpClient httpClient) {
        return builder.requestFactory(HttpComponentsClientHttpRequestFactory.class).build();
    }


    /**
     *  定义请求参数,设置请求参数
     * @return
     */
    @Bean(name = "requestConfig")
    public RequestConfig initRequestConfig(){
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(connectTimeout).
                setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).
                setStaleConnectionCheckEnabled(staleConnectionCheckEnabled).build();
        return requestConfig;
    }

    /**
     * SocketConfig 配置，配置可以采用默认的配置
     * @return
     */
    @Bean(name = "socketConfig")
    public SocketConfig socketConfig(){
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)     //是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
                .setSoReuseAddress(true) //是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
                .setSoTimeout(500)       //接收数据的等待超时时间，单位ms
                .setSoLinger(60)         //关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
                .setSoKeepAlive(true)    //开启监视TCP连接是否有效
                .build();
        return socketConfig;
    }

}
