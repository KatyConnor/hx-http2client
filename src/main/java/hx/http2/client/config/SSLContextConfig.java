package hx.http2.client.config;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.URL;

/**
 * @Author mingliang
 * @Date 2018-08-03 17:40
 */
@Configuration
public class SSLContextConfig {

    private static final URL classPath = Thread.currentThread().getContextClassLoader().getResource("keystore.p12");

    @Value("${http.maxIdleTime}")
    private long maxIdleTime;

    @Value("${http.maxIdleTime}")
    private long maxIdleTime;

    // 采用http2
    @Bean(name = "sSLContext")
    public SSLContext initSSLContext(){
        SSLContext sslcontext = null;
        try {
            sslcontext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(new File(classPath.getPath()), "root,.159357".toCharArray(),new TrustSelfSignedStrategy())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslcontext;
    }
}
