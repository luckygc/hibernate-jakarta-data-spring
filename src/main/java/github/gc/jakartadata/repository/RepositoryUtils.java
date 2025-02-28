package github.gc.jakartadata.repository;

import github.gc.hibernate.session.proxy.StatelessSessionProxy;
import org.hibernate.StatelessSession;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class RepositoryUtils {

	private RepositoryUtils() {
	}

	private static final Map<Class<?>, Class<?>> repositoryImplClasses = new ConcurrentHashMap<>(64);

	@NonNull
	public static <R> R createRepository(@NonNull Class<R> repositoryInterfaceClass,
			@NonNull StatelessSessionProxy sessionProxy) {
		Assert.notNull(repositoryInterfaceClass, "repositoryInterfaceClass must not be null");
		Assert.notNull(sessionProxy, "sessionProxy must not be null");

		Class<R> repositoryImplClass = getRepositoryImplClass(repositoryInterfaceClass);

		try {
			Constructor<R> constructor = repositoryImplClass.getConstructor(StatelessSession.class);
			return constructor.newInstance(sessionProxy);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <R, I extends R> Class<I> getRepositoryImplClass(@NonNull Class<R> repositoryInterface) {
		Assert.notNull(repositoryInterface, "repositoryInterface must not be null");

		return (Class<I>) repositoryImplClasses.computeIfAbsent(repositoryInterface,
				RepositoryUtils::doGetRepositoryImplClass);
	}

	private static Class<?> doGetRepositoryImplClass(Class<?> repositoryInterface) {
		String interfaceName = repositoryInterface.getName();
		// hibernate-processor生成的Repository实现类
		String implName = interfaceName + "_";
		try {
			return Class.forName(implName);
		} catch (Exception e) {
			throw new RuntimeException("加载Jakarta Data Repository实现类[%s]失败".formatted(implName), e);
		}
	}
}
