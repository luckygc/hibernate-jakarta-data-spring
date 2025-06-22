package github.gc.jakartadata.session;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;

/**
 * Session 管理器
 * 负责管理 StatelessSession 的生命周期，处理事务情况
 * 使用 ResourceHolder 模式进行标准化的资源管理
 *
 * @author gc
 */
public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public SessionManager(@NonNull SessionFactory sessionFactory, @NonNull DataSource dataSource) {
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    /**
     * 获取 StatelessSession
     * 委托给 StatelessSessionUtils 进行标准化处理
     */
    public StatelessSession getSession() {
        return StatelessSessionUtils.getSession(sessionFactory, dataSource);
    }

    /**
     * 判断是否应该关闭 Session
     * 委托给 StatelessSessionUtils 进行标准化处理
     */
    public boolean shouldCloseSession() {
        return StatelessSessionUtils.shouldCloseSession(sessionFactory, dataSource);
    }

    /**
     * 关闭 Session
     */
    public void closeSession(StatelessSession session) {
        StatelessSessionUtils.closeSession(session);
    }
}
