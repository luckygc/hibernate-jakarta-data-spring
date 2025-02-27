package github.gc;

import org.hibernate.StatelessSession;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;

public class ConstructorTest {

	private static Constructor<?> constructor;
	private static MethodHandle typedConstructor;
	private static SessionConstructor<?> factory;

	public static <T> T createInstance(Class<T> clazz, StatelessSession session) {
		try {
			if (typedConstructor == null) {
				// 构造函数参数类型为 StatelessSession，此时指定返回类型为 void
				MethodType originalType = MethodType.methodType(void.class, StatelessSession.class);
				MethodHandles.Lookup lookup = MethodHandles.lookup();
				MethodHandle constructor = lookup.findConstructor(clazz, originalType);
				// 将返回类型转换为Object类型，使得方法句柄类型为 (StatelessSession) -> Object,否则经过泛型查出严格调用会失败
				typedConstructor = constructor.asType(MethodType.methodType(Object.class, StatelessSession.class));
			}

			// 调用构造函数创建实例
			return clazz.cast(typedConstructor.invokeExact(session));
		} catch (Throwable e) {
			throw new RuntimeException("调用失败", e);
		}
	}

	public static <T> T createInstance2(Class<T> clazz, StatelessSession session) {
		try {
			if (constructor == null) {
				constructor = clazz.getConstructor(StatelessSession.class);
			}

			return clazz.cast(constructor.newInstance(session));
		} catch (Throwable e) {
			throw new RuntimeException("调用失败", e);
		}
	}

	@FunctionalInterface
	public interface SessionConstructor<T> {
		T create(StatelessSession session);
	}

	public static <T> T createInstance3(Class<T> clazz, StatelessSession session) {
		try {
			if (factory == null) {
				MethodHandles.Lookup lookup = MethodHandles.lookup();

				// 构造函数的原始方法类型：构造函数返回类型虽然是 void，但实际上会返回一个新对象
				// 所以此处要求构造函数签名为 (StatelessSession) -> void
				MethodType constructorType = MethodType.methodType(void.class, StatelessSession.class);
				MethodHandle constructorHandle = lookup.findConstructor(clazz, constructorType);

				// 接下来使用 LambdaMetafactory 生成一个构造函数工厂
				// invokedType: 工厂方法的签名，这里是无参返回 SessionConstructor 的实例
				MethodType invokedType = MethodType.methodType(SessionConstructor.class);
				// SAM（单抽象方法）签名：函数式接口的抽象方法签名。注意这里返回类型用 Object（泛型擦除），参数为 StatelessSession
				MethodType samMethodType = MethodType.methodType(Object.class, StatelessSession.class);
				// 实际构造函数的签名：(StatelessSession) -> clazz
				MethodType instantiatedMethodType = MethodType.methodType(clazz, StatelessSession.class);

				CallSite site = LambdaMetafactory.metafactory(lookup, "create",
						// 对应 SessionConstructor 接口中的方法名
						invokedType, samMethodType, constructorHandle, instantiatedMethodType);
				// 获取构造器工厂
				factory = (SessionConstructor<?>) site.getTarget().invokeExact();
			}
			// 调用工厂方法创建实例
			return clazz.cast(factory.create(session));
		} catch (Throwable t) {
			throw new RuntimeException("调用失败", t);
		}
	}

	public static TestRepo createInstance4(Class ignore,StatelessSession session) {
		return new TestRepo(session);
	}

	public static void main(String[] args) {
		long start = System.nanoTime();
		StatelessSession session = null;
		for (int i = 0; i < 1000; i++) {
			Object instance = createInstance3(TestRepo.class, session);
			if (instance == null) {
				throw new RuntimeException("instance is null");
			}
		}
		long end = System.nanoTime();
		System.out.println("耗时：" + (end - start)/1000 + " nano");
	}
}
