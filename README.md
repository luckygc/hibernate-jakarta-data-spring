# hibernate-jakarta-data-spring
spring集成jakarta data,底层基于hibernate

参考 mybatis-spring,spring-data-jpa,spring-orm

暂时不支持多数据源，只支持本地事务，自动参与spring事务。使用DatasourceTransactionManager即可管理事务

使用repository或者session代理对象，有spring事务情况下，由事务同步关闭session，事务管理器关闭连接。无事务情况下执行完操作就关闭session，session会关闭连接。


TODO
- [ ] 异常翻译
- [ ] jta
