/**
 * Copyright 2010-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hx.http2.retrofit2;

import hx.http2.gson.GsonBeanFactory;
import hx.http2.retrofit2.annotation.RetrofitClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * create proxy bean
 * @param <T>
 */
public class RetrofitClientFactoryBean<T> implements FactoryBean<T>, EnvironmentAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(RetrofitClientFactoryBean.class);

    private Class<T> retrofitInterface;
    private Environment environment;

    public RetrofitClientFactoryBean() {
    }

    public RetrofitClientFactoryBean(Class<T> retrofitInterface) {
        this.retrofitInterface = retrofitInterface;
    }

    @Override
    public T getObject() throws Exception {
        RetrofitClient annotation = this.getRetrofitInterface().getAnnotation(RetrofitClient.class);
        String url = environment.resolvePlaceholders(annotation.url());

        LOGGER.info("interface class is :{} ",retrofitInterface);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(GsonBeanFactory.newInstance().setIsLong(false).builder())) // gson扩展
                .baseUrl(url)
                .build();
        return retrofit.create(retrofitInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return this.retrofitInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setRetrofitInterface(Class<T> retrofitInterface) {
        this.retrofitInterface = retrofitInterface;
    }

    public Class<T> getRetrofitInterface() {
        return retrofitInterface;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
