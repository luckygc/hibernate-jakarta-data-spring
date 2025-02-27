package github.gc.jakartadata;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.lang.invoke.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RepositoryFactoryUtils {

	private RepositoryFactoryUtils() {
	}

	/**
	 * RepositoryConstructor函数式接口方法名
	 */
	private static final String REPOSITORY_CONSTRUCTOR_METHOD_NAME = "construct";

	private static final Map<Class<?>, Class<?>> repositoryImplClasses = new ConcurrentHashMap<>(64);

	private static final Map<Class<?>, RepositoryConstructor> repositoryImplConstructors = new ConcurrentHashMap<>(64);

	@NonNull
	public static <R> R createRepository(@NonNull Class<R> repositoryImplClass, @NonNull StatelessSession session) {
		Assert.notNull(repositoryImplClass, "repositoryImplClass must not be null");
		Assert.notNull(session, "session must not be null");
		RepositoryConstructor repositoryConstructor = repositoryImplConstructors.computeIfAbsent(repositoryImplClass,
				RepositoryFactoryUtils::getRepositoryConstructor);

		return repositoryImplClass.cast(repositoryConstructor.construct(session));
	}

	private static RepositoryConstructor getRepositoryConstructor(Class<?> repositoryImplClass) {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();

			// 构造函数原始方法签名为 (StatelessSession) -> void
			MethodType originalConstructorType = MethodType.methodType(void.class, StatelessSession.class);
			MethodHandle constructorHandle = lookup.findConstructor(repositoryImplClass, originalConstructorType);

			// 接下来使用 LambdaMetaFactory 生成一个构造函数工厂
			// invokedType: 工厂方法的签名，这里是无参返回 RepositoryConstructor 的实例
			MethodType invokedType = MethodType.methodType(RepositoryConstructor.class);
			// SAM（单抽象方法）签名：函数式接口的抽象方法签名。注意这里返回类型用 Object（泛型擦除），参数为 StatelessSession
			MethodType samMethodType = MethodType.methodType(Object.class, StatelessSession.class);
			// 实际构造函数的签名：(StatelessSession) -> repositoryImplClass
			MethodType instantiatedMethodType = MethodType.methodType(repositoryImplClass, StatelessSession.class);

			CallSite site = LambdaMetafactory.metafactory(lookup, REPOSITORY_CONSTRUCTOR_METHOD_NAME, invokedType,
					samMethodType, constructorHandle, instantiatedMethodType);
			// 获取构造器工厂
			return (RepositoryConstructor) site.getTarget().invokeExact();
		} catch (Throwable t) {
			throw new RuntimeException("获取工厂构造函数失败", t);
		}
	}

	public static <R, I extends R> Class<I> getRepositoryImplClass(@NonNull Class<R> repositoryInterface) {
		Assert.notNull(repositoryInterface, "repositoryInterface must not be null");

		return (Class<I>) repositoryImplClasses.computeIfAbsent(repositoryInterface,
				RepositoryFactoryUtils::doGetRepositoryImplClass);
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

	public static <R, I extends R> I doGetTransactionalRepository(@NonNull SessionFactory sessionFactory,
			@NonNull Class<R> repositoryInterface) {
		Assert.notNull(sessionFactory, "sessionFactory must not be null");
		Assert.notNull(repositoryInterface, "repositoryInterface must not be null");

		Object resource = TransactionSynchronizationManager.getResource(repositoryInterface);

		Class<I> repositoryImplClass = getRepositoryImplClass(repositoryInterface);

		return null;
	}

	/**
	 * RepositoryConstructor函数式接口,用于创建Repository实例
	 */
	@FunctionalInterface
	private interface RepositoryConstructor {
		Object construct(StatelessSession session);
	}
}
