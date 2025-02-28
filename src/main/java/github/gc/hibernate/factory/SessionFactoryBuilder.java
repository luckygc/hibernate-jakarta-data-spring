package github.gc.hibernate.factory;

import jakarta.data.repository.Repository;
import jakarta.persistence.*;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
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
import java.util.Set;
import java.util.TreeSet;

public class SessionFactoryBuilder extends Configuration {

	private static final String RESOURCE_PATTERN = "/**/*.class";

	private static final TypeFilter[] ENTITY_TYPE_FILTERS = new TypeFilter[]{
			new AnnotationTypeFilter(Entity.class, false), new AnnotationTypeFilter(Embeddable.class, false),
			new AnnotationTypeFilter(MappedSuperclass.class, false)};

	private static final TypeFilter[] REPOSITORY_TYPE_FILTERS = new TypeFilter[]{
			new AnnotationTypeFilter(Repository.class, false)};

	private static final TypeFilter CONVERTER_TYPE_FILTER = new AnnotationTypeFilter(Converter.class, false);

	private final ResourcePatternResolver resourcePatternResolver;

	public SessionFactoryBuilder(DataSource dataSource) {
		this(dataSource, new PathMatchingResourcePatternResolver());
	}

	public SessionFactoryBuilder(DataSource dataSource, ClassLoader classLoader) {
		this(dataSource, new PathMatchingResourcePatternResolver(classLoader));
	}

	public SessionFactoryBuilder(DataSource dataSource, ResourceLoader resourceLoader) {
		this(dataSource, resourceLoader, new MetadataSources(
				new BootstrapServiceRegistryBuilder().applyClassLoader(resourceLoader.getClassLoader()).build()));
	}

	public SessionFactoryBuilder(DataSource dataSource, ResourceLoader resourceLoader,
			MetadataSources metadataSources) {

		super(metadataSources);

		if (dataSource != null) {
			getProperties().put(AvailableSettings.JAKARTA_NON_JTA_DATASOURCE, dataSource);
		}

		getProperties().put(AvailableSettings.CONNECTION_HANDLING,
				PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION);

		getProperties().put(AvailableSettings.CLASSLOADERS, Collections.singleton(resourceLoader.getClassLoader()));

		setProperty(AvailableSettings.STATEMENT_FETCH_SIZE, Integer.MAX_VALUE);
		setProperty(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, Integer.MAX_VALUE);
		setProperty(AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY, AccessType.READ_WRITE.getExternalName());
		setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, Boolean.FALSE);

		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}

	@SuppressWarnings("unchecked")
	public SessionFactoryBuilder scanPackages(String... packagesToScan) throws HibernateException {
		Set<String> entityClassNames = new TreeSet<>();
		Set<String> converterClassNames = new TreeSet<>();
		try {
			for (String pkg : packagesToScan) {
				String pattern =
						ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(
								pkg) + RESOURCE_PATTERN;
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
						// Ignore non-readable resource
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
				addAnnotatedClass(ClassUtils.forName(className, cl));
			}
			for (String className : converterClassNames) {
				addAttributeConverter((Class<? extends AttributeConverter<?, ?>>) ClassUtils.forName(className, cl));
			}

		} catch (ClassNotFoundException ex) {
			throw new MappingException("Failed to load annotated classes from classpath", ex);
		}
		return this;
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