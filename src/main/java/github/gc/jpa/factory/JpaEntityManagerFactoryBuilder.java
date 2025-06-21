package github.gc.jpa.factory;

import jakarta.data.repository.Repository;
import jakarta.persistence.*;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.service.ServiceRegistry;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * JPA EntityManagerFactory 构建器
 * 基于 Hibernate 实现，使用 JPA 标准 API
 */
public class JpaEntityManagerFactoryBuilder {

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private static final TypeFilter[] ENTITY_TYPE_FILTERS = new TypeFilter[]{
            new AnnotationTypeFilter(Entity.class, false), 
            new AnnotationTypeFilter(Embeddable.class, false),
            new AnnotationTypeFilter(MappedSuperclass.class, false)};

    private static final TypeFilter CONVERTER_TYPE_FILTER = new AnnotationTypeFilter(Converter.class, false);

    private final ResourcePatternResolver resourcePatternResolver;
    private final StandardServiceRegistryBuilder registryBuilder;
    private final MetadataSources metadataSources;
    private ServiceRegistry serviceRegistry;

    public JpaEntityManagerFactoryBuilder(DataSource dataSource) {
        this(dataSource, new PathMatchingResourcePatternResolver());
    }

    public JpaEntityManagerFactoryBuilder(DataSource dataSource, ClassLoader classLoader) {
        this(dataSource, new PathMatchingResourcePatternResolver(classLoader));
    }

    public JpaEntityManagerFactoryBuilder(DataSource dataSource, ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.registryBuilder = new StandardServiceRegistryBuilder();

        // 配置数据源
        if (dataSource != null) {
            this.registryBuilder.applySetting(AvailableSettings.JAKARTA_NON_JTA_DATASOURCE, dataSource);
        }

        // 配置连接处理模式
        this.registryBuilder.applySetting(AvailableSettings.CONNECTION_HANDLING,
                PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION);

        // 配置类加载器
        this.registryBuilder.applySetting(AvailableSettings.CLASSLOADERS, 
                Collections.singleton(resourceLoader.getClassLoader()));

        // 创建服务注册表和元数据源
        this.serviceRegistry = this.registryBuilder.build();
        this.metadataSources = new MetadataSources(this.serviceRegistry);
    }

    public JpaEntityManagerFactoryBuilder addProperties(Properties properties) {
        if (properties != null) {
            properties.forEach((key, value) -> 
                this.registryBuilder.applySetting(key.toString(), value));
        }
        return this;
    }

    public JpaEntityManagerFactoryBuilder scanPackages(String... packagesToScan) throws HibernateException {
        Set<String> entityClassNames = new TreeSet<>();
        Set<String> converterClassNames = new TreeSet<>();
        
        try {
            for (String pkg : packagesToScan) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + 
                        ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
                Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                
                for (Resource resource : resources) {
                    try {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        if (matchesEntityTypeFilter(reader, readerFactory)) {
                            entityClassNames.add(className);
                        } else if (CONVERTER_TYPE_FILTER.match(reader, readerFactory)) {
                            converterClassNames.add(className);
                        }
                    } catch (FileNotFoundException ex) {
                        // 忽略不可读的资源
                    } catch (Throwable ex) {
                        throw new MappingException("Failed to read candidate component class: " + resource, ex);
                    }
                }
            }
        } catch (IOException ex) {
            throw new MappingException("Failed to scan classpath for unlisted classes", ex);
        }
        
        try {
            ClassLoader cl = this.resourcePatternResolver.getClassLoader();
            for (String className : entityClassNames) {
                this.metadataSources.addAnnotatedClass(ClassUtils.forName(className, cl));
            }
            for (String className : converterClassNames) {
                this.metadataSources.addAnnotatedClass(ClassUtils.forName(className, cl));
            }
        } catch (ClassNotFoundException ex) {
            throw new MappingException("Failed to load annotated classes from classpath", ex);
        }
        
        return this;
    }

    public EntityManagerFactory buildEntityManagerFactory() {
        // 重新构建服务注册表以包含所有设置
        if (this.serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(this.serviceRegistry);
        }
        this.serviceRegistry = this.registryBuilder.build();

        // 使用新的服务注册表创建元数据源
        MetadataSources newMetadataSources = new MetadataSources(this.serviceRegistry);

        // 复制已添加的类
        this.metadataSources.getAnnotatedClasses().forEach(newMetadataSources::addAnnotatedClass);

        // 构建 SessionFactory 然后转换为 EntityManagerFactory
        return newMetadataSources.getMetadataBuilder()
                .build()
                .getSessionFactoryBuilder()
                .build()
                .unwrap(EntityManagerFactory.class);
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    private boolean matchesEntityTypeFilter(MetadataReader reader, MetadataReaderFactory readerFactory)
            throws IOException {
        for (TypeFilter filter : ENTITY_TYPE_FILTERS) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }
}
