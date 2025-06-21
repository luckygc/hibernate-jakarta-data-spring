package github.gc.jakartadata.factory;

import github.gc.jakartadata.session.StatelessSessionTemplate;
import github.gc.jakartadata.utils.HibernateRepositoryUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Hibernate Data Repository 工厂 Bean
 * 参考 MyBatis MapperFactoryBean 的简洁设计
 */
public class HibernateDataRepositoryFactoryBean<T> implements FactoryBean<T> {

    private static final Logger log = LoggerFactory.getLogger(HibernateDataRepositoryFactoryBean.class);

    private final Class<T> repositoryInterface;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    public HibernateDataRepositoryFactoryBean(@NonNull Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        // 参考 MyBatis MapperFactoryBean，直接在 getObject 中创建代理
        StatelessSessionTemplate sessionTemplate = new StatelessSessionTemplate(sessionFactory, dataSource);

        return (T) Proxy.newProxyInstance(
            repositoryInterface.getClassLoader(),
            new Class<?>[]{repositoryInterface},
            new RepositoryInvocationHandler(sessionTemplate)
        );
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Repository 调用处理器
     * 参考 MyBatis 的简洁代理实现
     */
    private class RepositoryInvocationHandler implements InvocationHandler {

        private final StatelessSessionTemplate sessionTemplate;

        public RepositoryInvocationHandler(StatelessSessionTemplate sessionTemplate) {
            this.sessionTemplate = sessionTemplate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理 Object 类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return handleObjectMethod(proxy, method, args);
            }

            // 使用 SessionTemplate 执行方法
            return sessionTemplate.execute(session -> {
                try {
                    // 获取 Hibernate 生成的实现类
                    Class<? extends T> implClass = HibernateRepositoryUtils
                        .getRepositoryImplementationClass(repositoryInterface);

                    // 创建实现实例并调用方法
                    T impl = implClass.getDeclaredConstructor(session.getClass()).newInstance(session);
                    return method.invoke(impl, args);

                } catch (Exception e) {
                    log.error("Error executing repository method: {}.{}",
                             repositoryInterface.getSimpleName(), method.getName(), e);
                    throw new RuntimeException("Repository method execution failed", e);
                }
            });
        }

        private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
            String methodName = method.getName();
            switch (methodName) {
                case "equals":
                    return proxy == args[0];
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return repositoryInterface.getName() + " proxy";
                default:
                    throw new UnsupportedOperationException("Method not supported: " + methodName);
            }
        }
    }

    // Setters for manual configuration
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
