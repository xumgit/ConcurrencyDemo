package com.xum.demo.config.dynamic;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/*@Configuration
@MapperScan(basePackages = {"com.xum.demo.dao.mysql", "com.xum.demo.dao.oracle"},
        sqlSessionFactoryRef = "sqlSessionFactory", sqlSessionTemplateRef = "sqlSessionTemplate")*/
public class MysqlAndOracleDataSourceConfig {

    @Value("${spring.datasource.primary.url}")
    private String url_mysql;
    @Value("${spring.datasource.primary.driverClassName}")
    private String driverClassName_mysql;
    @Value("${spring.datasource.primary.username}")
    private String username_mysql;
    @Value("${spring.datasource.primary.password}")
    private String password_mysql;

    @Value("${spring.datasource.secondary.url}")
    private String url_oracle;
    @Value("${spring.datasource.secondary.driverClassName}")
    private String driverClassName_oracle;
    @Value("${spring.datasource.secondary.username}")
    private String username_oracle;
    @Value("${spring.datasource.secondary.password}")
    private String password_oracle;

    @Primary
    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClassName_mysql);
        druidDataSource.setUrl(url_mysql);
        druidDataSource.setUsername(username_mysql);
        druidDataSource.setPassword(password_mysql);
        return druidDataSource;
    }

    @Bean(name = "oracleDataSource")
    public DataSource oracleDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClassName_oracle);
        druidDataSource.setUrl(url_oracle);
        druidDataSource.setUsername(username_oracle);
        druidDataSource.setPassword(password_oracle);
        return druidDataSource;
    }

    @Bean(name = "dynamicDataSource")
    public DynamicDataSource DataSource(@Qualifier(value = "mysqlDataSource") DataSource mysqlDataSource,
                                        @Qualifier(value = "oracleDataSource") DataSource oracleDataSource) {
        Map<Object, Object> targetDataSource = new HashMap<>();
        targetDataSource.put(DataSourceType.DataBaseType.MYSQL, mysqlDataSource);
        targetDataSource.put(DataSourceType.DataBaseType.ORACLE, oracleDataSource);
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(mysqlDataSource);
        return dataSource;
    }

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier(value = "dynamicDataSource") DataSource dynamicDataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setDataSource(dynamicDataSource);
        bean.setMapperLocations(resolver.getResources("classpath:mapper/*/*.xml"));
        bean.setTypeAliasesPackage("com.xum.demo.pojo.mysql" + "," + "com.xum.demo.pojo.oracle");
        return bean.getObject();
    }

    @Primary
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(
            @Qualifier(value = "dynamicDataSource") DataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier(value = "sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
