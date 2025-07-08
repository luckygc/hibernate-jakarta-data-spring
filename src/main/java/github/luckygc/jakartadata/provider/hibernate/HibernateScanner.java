package github.luckygc.jakartadata.provider.hibernate;

import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class HibernateScanner {

    /** 类文件资源匹配模式 用于匹配包路径下的所有.class文件 */
    private static final String RESOURCE_PATTERN = "/**/*.class";

    /** 实体类型过滤器数组 包含Entity和MappedSuperclass注解的过滤器，用于识别JPA实体类和映射超类 */
    private static final TypeFilter[] ENTITY_TYPE_FILTERS =
            new TypeFilter[] {
                new AnnotationTypeFilter(Entity.class, false),
                new AnnotationTypeFilter(MappedSuperclass.class, false)
            };

    /** 转换器类型过滤器 用于识别标注了@Converter注解的属性转换器类 */
    private static final TypeFilter CONVERTER_TYPE_FILTER =
            new AnnotationTypeFilter(Converter.class, false);

    /** 资源模式解析器 用于解析类路径下的资源文件 */
    private static final ResourcePatternResolver resourcePatternResolver =
            new PathMatchingResourcePatternResolver();

    /**
     * 扫描指定包路径下的实体类并添加到Hibernate配置中
     *
     * <p>该方法会扫描指定包路径下的所有类文件，识别标注了@Entity、@MappedSuperclass或@Converter注解的类，
     * 并将这些类添加到Hibernate持久化配置中作为管理类。
     *
     * @param configuration Hibernate持久化配置对象
     * @param packagesToScan 要扫描的包路径数组，支持多个包路径
     * @throws HibernateException 当扫描过程中发生错误时抛出
     */
    public static void scan(
            HibernatePersistenceConfiguration configuration, String... packagesToScan)
            throws HibernateException {
        // 使用TreeSet保证类名的有序性，便于调试和日志输出
        Set<String> managedClassNames = new TreeSet<>();
        try {
            // 遍历每个要扫描的包路径
            for (String pkg : packagesToScan) {
                // 构建类路径资源匹配模式
                String pattern =
                        ResourcePatternResolver.CLASSPATH_URL_PREFIX
                                + ClassUtils.convertClassNameToResourcePath(pkg)
                                + RESOURCE_PATTERN;
                // 获取匹配模式的所有资源文件
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                // 创建元数据读取器工厂，用于读取类的元数据信息
                MetadataReaderFactory readerFactory =
                        new CachingMetadataReaderFactory(resourcePatternResolver);

                // 遍历每个资源文件
                for (Resource resource : resources) {
                    try {
                        // 读取类的元数据信息
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();

                        // 检查是否匹配实体类型过滤器或转换器过滤器
                        if (matchesEntityTypeFilter(reader, readerFactory)
                                || CONVERTER_TYPE_FILTER.match(reader, readerFactory)) {
                            managedClassNames.add(className);
                        }
                    } catch (FileNotFoundException ex) {
                        // 忽略无法读取的资源文件
                    } catch (Throwable ex) {
                        throw new MappingException("读取候选组件类失败: " + resource, ex);
                    }
                }
            }
        } catch (IOException ex) {
            throw new MappingException("扫描类路径中的未列出类失败", ex);
        }

        try {
            // 获取类加载器用于加载扫描到的类
            ClassLoader cl = resourcePatternResolver.getClassLoader();

            // 将扫描到的管理类添加到Hibernate配置中
            for (String className : managedClassNames) {
                configuration.managedClass(ClassUtils.forName(className, cl));
            }
        } catch (ClassNotFoundException ex) {
            throw new MappingException("从类路径加载注解类失败", ex);
        }
    }

    /**
     * 检查类是否匹配实体类型过滤器
     *
     * <p>该方法检查给定的类是否标注了@Entity或@MappedSuperclass注解。
     *
     * @param reader 类的元数据读取器
     * @param readerFactory 元数据读取器工厂
     * @return 如果类匹配任一实体类型过滤器则返回true，否则返回false
     * @throws IOException 当读取类元数据时发生IO异常
     */
    private static boolean matchesEntityTypeFilter(
            MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        // 遍历所有实体类型过滤器
        for (TypeFilter filter : ENTITY_TYPE_FILTERS) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }
}
