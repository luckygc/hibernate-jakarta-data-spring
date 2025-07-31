/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package github.luckygc.jakartadata.provider.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;

public class SessionFactoryBean implements FactoryBean<SessionFactory>, InitializingBean, DisposableBean {

    private DataSource dataSource;

    private String[] packagesToScan;

    private SessionFactory sessionFactory;

    @Override
    public void afterPropertiesSet() {
        var configuration = new HibernatePersistenceConfiguration("data");
        configuration.property(AvailableSettings.JAKARTA_NON_JTA_DATASOURCE, this.dataSource);
        HibernateScanner.scan(configuration, packagesToScan);
        this.sessionFactory = configuration.createEntityManagerFactory();
    }

    @Override
    public SessionFactory getObject() {
        if (this.sessionFactory == null) {
            afterPropertiesSet();
        }

        return this.sessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return (this.sessionFactory != null ? this.sessionFactory.getClass() : SessionFactory.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
