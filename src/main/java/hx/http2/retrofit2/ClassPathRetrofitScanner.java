package hx.http2.retrofit2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 *
 */
public class ClassPathRetrofitScanner extends ClassPathBeanDefinitionScanner {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClassPathRetrofitScanner.class);

    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;

    public ClassPathRetrofitScanner(BeanDefinitionRegistry registry) {
        super(registry,false);
    }

    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        // if specified, use the given annotation and / or marker interface
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        // override AssignableTypeFilter to ignore matches on the actual marker interface
        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            LOGGER.warn("No RetrofitClient was found in '{}' package. Please check your configuration.",Arrays.toString(basePackages));
        } else {
             processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            LOGGER.warn("Skipping RetrofitFactoryBean with name '{}' and '{}' retrofitInterface. Bean already defined with the same name!",beanName,beanDefinition.getBeanClassName());
            return false;
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        beanDefinitions.forEach(v ->{
            GenericBeanDefinition definition = (GenericBeanDefinition) v.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            LOGGER.info("---------------beanClassName = {}",beanClassName);
            LOGGER.debug("Creating RetrofitFactoryBean with name '{}' and '{}' retrofitInterface",v.getBeanName(),beanClassName);
            // the mapper interface is the original class of the bean
            // but, the actual class of the bean is RetrofitFactoryBean
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.setBeanClass(RetrofitClientFactoryBean.class); // 采用自定义bean生成方式

            LOGGER.debug("Enabling autowire by type for RetrofitFactoryBean with name '{}'.",v.getBeanName());
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        });
    }
}
