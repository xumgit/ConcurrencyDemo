package com.xum.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.xum.demo.utils.CommonVariable;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.xum.demo.dao.oracle", sqlSessionFactoryRef = "oracleSqlSessionFactory")
public class OracleDataSourceConfig extends DataSourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(OracleDataSourceConfig.class);

    @Value("${spring.datasource.secondary.url}")
    private String url;
    @Value("${spring.datasource.secondary.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasource.secondary.username}")
    private String username;
    @Value("${spring.datasource.secondary.password}")
    private String password;

    @Bean(name = "oracleDataSource")
    public DataSource oracleDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "oracleTransactionManager")
    public DataSourceTransactionManager oracleTransactionManager(
            @Qualifier(value = "oracleDataSource") DataSource dataSource) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
        return manager;
    }

    @Bean(name = "oracleSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier(value = "oracleDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactory factoryBean = super.sqlSessionFactory(dataSource, CommonVariable.ORACLE);
        return factoryBean;
    }

    @Bean(name = "oracleSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(
            @Qualifier(value = "oracleSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
