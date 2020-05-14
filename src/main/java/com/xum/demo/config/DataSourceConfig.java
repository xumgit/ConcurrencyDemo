package com.xum.demo.config;

import com.xum.demo.utils.CommonVariable;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

public class DataSourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfig.class);

    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, String dataBaseType) throws Exception {
        LOG.info("dataBaseType:" + dataBaseType);
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setDataSource(dataSource);
        if (CommonVariable.MYSQL.equalsIgnoreCase(dataBaseType)) {
            factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/mysql/*.xml"));
            factoryBean.setTypeAliasesPackage("com.xum.demo.pojo.mysql");
        } else if (CommonVariable.ORACLE.equalsIgnoreCase(dataBaseType)) {
            factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/oracle/*.xml"));
            factoryBean.setTypeAliasesPackage("com.xum.demo.pojo.oracle");
        }
        factoryBean.setConfigLocation(resolver.getResource("classpath:config/mybatis/mybatis_config.xml"));
        return factoryBean.getObject();
    }

}
