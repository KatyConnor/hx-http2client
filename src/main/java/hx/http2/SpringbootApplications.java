package hx.http2;

import hx.http2.retrofit2.annotation.EnableRetrofitClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author mingliang
 * @Date 2018-08-09 10:38
 */
@SpringBootApplication
@EnableRetrofitClients
public class SpringbootApplications {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplications.class,args);
    }
}
