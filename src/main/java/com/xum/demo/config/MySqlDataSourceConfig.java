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
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.xum.demo.dao.mysql", sqlSessionFactoryRef = "mysqlSqlSessionFactory")
public class MySqlDataSourceConfig extends DataSourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MySqlDataSourceConfig.class);

    @Value("${spring.datasource.primary.url}")
    private String url;
    @Value("${spring.datasource.primary.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasource.primary.username}")
    private String username;
    @Value("${spring.datasource.primary.password}")
    private String password;

    @Primary
    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        //super.setDataSource(url, driverClassName, username, password);
        return druidDataSource;
    }

    @Primary
    @Bean(name = "mysqlTransactionManager")
    public DataSourceTransactionManager mysqlTransactionManager(
            @Qualifier(value = "mysqlDataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }

    @Primary
    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier(value = "mysqlDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactory factoryBean = super.sqlSessionFactory(dataSource, CommonVariable.MYSQL);
        return factoryBean;
    }

    @Primary
    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(
            @Qualifier(value = "mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
