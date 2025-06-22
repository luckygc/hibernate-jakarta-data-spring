package github.gc.jakartadata.session;

import org.hibernate.StatelessSession;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * StatelessSession 资源持有者
 * 继承 ResourceHolderSupport，提供标准的 Spring 事务资源管理功能
 * 
 * @author gc
 */
public class StatelessSessionHolder extends ResourceHolderSupport {

    private final StatelessSession statelessSession;
    
    /**
     * 构造函数
     * 
     * @param statelessSession StatelessSession 实例
     */
    public StatelessSessionHolder(@NonNull StatelessSession statelessSession) {
        this.statelessSession = statelessSession;
    }

    /**
     * 获取 StatelessSession
     * 
     * @return StatelessSession 实例
     */
    @NonNull
    public StatelessSession getStatelessSession() {
        return this.statelessSession;
    }

    /**
     * 检查 Session 是否有效
     * 
     * @return true 如果 Session 连接有效
     */
    public boolean hasValidSession() {
        return this.statelessSession.isConnected();
    }

    /**
     * 检查资源是否为空
     * 
     * @return true 如果 Session 无效或已断开连接
     */
    @Override
    public boolean isVoid() {
        return !hasValidSession();
    }

    @Override
    public String toString() {
        return "StatelessSessionHolder{" +
                "session=" + statelessSession +
                ", connected=" + statelessSession.isConnected() +
                ", synchronizedWithTransaction=" + isSynchronizedWithTransaction() +
                ", rollbackOnly=" + isRollbackOnly() +
                '}';
    }
}
