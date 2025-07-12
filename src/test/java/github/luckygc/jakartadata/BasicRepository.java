package github.luckygc.jakartadata;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

/**
 * 基本Repository接口
 * 只继承CrudRepository，不添加自定义方法
 */
@Repository
public interface BasicRepository extends CrudRepository<User, Long> {
}