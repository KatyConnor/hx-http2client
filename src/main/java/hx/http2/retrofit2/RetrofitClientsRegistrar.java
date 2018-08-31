package hx.http2.retrofit2;

import hx.http2.retrofit2.annotation.EnableRetrofitClients;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

public class RetrofitClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    public RetrofitClientsRegistrar() {}

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerDefaultConfiguration(importingClassMetadata,registry);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private void registerDefaultConfiguration(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathRetrofitScanner scanner = new ClassPathRetrofitScanner(registry);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.setAnnotationClass(EnableRetrofitClients.class);
        scanner.registerFilters();
        scanner.doScan(ClassUtils.getPackageName(metadata.getClassName()));
    }
}
