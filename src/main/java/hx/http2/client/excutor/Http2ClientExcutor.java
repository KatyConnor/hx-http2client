package hx.http2.client.excutor;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import hx.http2.gson.GsonBeanFactory;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * @Author mingliang
 * @Date 2018-08-01 17:27
 */
//@Component
public class Http2ClientExcutor {

    private static final String QUESTION_MARK = "?";
    private static final String AND_MARK = "&";
    private static final String EQUAL = "=";
    @Autowired
    private RequestConfig requestConfig;

    /**
     * 同步请求
     * @param request
     * @param classzz
     * @param <T>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public <T> hx.http2.client.response.HttpResponse<T> excute(HttpRequest request,Class<T> classzz) throws IOException, InterruptedException {
        HttpClient client = buildHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        hx.http2.client.response.HttpResponse<T> httpResponse = hx.http2.client.response.HttpResponse.build();
        httpResponse.setHttpResponse(response);
        return httpResponse;
    }

    /**
     * 异步请求
     * @param request
     * @param classzz HttpRequest
     * @param <T>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public <T> hx.http2.client.response.HttpResponse<T> excuteAsync(HttpRequest request,Class<T> classzz) throws IOException, InterruptedException, ExecutionException {
        HttpClient client = buildHttpClient();
        CompletableFuture< HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseStr = response.get();
        hx.http2.client.response.HttpResponse<T> httpResponse = hx.http2.client.response.HttpResponse.build();
        httpResponse.setData(GsonBeanFactory.newInstance().setIsLong(false).gsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                .fromJson(responseStr.body(),classzz));
        return httpResponse;
    }

    private HttpClient buildHttpClient(){
        Authenticator authenticator=new Authenticator() {
            @Override
            public PasswordAuthentication requestPasswordAuthenticationInstance(String host, InetAddress addr, int port, String protocol, String prompt, String scheme, URL url, RequestorType reqType) {
                return super.requestPasswordAuthenticationInstance(host, addr, port, protocol, prompt, scheme, url, reqType);
            }

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return  new PasswordAuthentication("username", "password".toCharArray());//super.getPasswordAuthentication();
            }

            @Override
            protected URL getRequestingURL() {
                return super.getRequestingURL();
            }

            @Override
            protected RequestorType getRequestorType() {
                return super.getRequestorType();
            }
        };
        try {
            HttpClient client= HttpClient.newBuilder().authenticator(authenticator) //配置authenticator
                    .sslContext(SSLContext.getDefault())//配置 sslContext
                    .sslParameters(new SSLParameters())//配置 sslParameters
                    .proxy(ProxySelector.getDefault())//配置 proxy
                    .executor(Executors.newCachedThreadPool())//配置 executor
                    .followRedirects(HttpClient.Redirect.ALWAYS)//配置 followRedirects
                    .cookieHandler(new CookieManager())//配置 cookieManager
                    .version(HttpClient.Version.HTTP_2)//配置 version
                    .build();
            return client;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HttpClient.newBuilder().build();
    }
}
