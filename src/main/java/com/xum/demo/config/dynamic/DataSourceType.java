package com.xum.demo.config.dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceType {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceType.class);

    public enum DataBaseType {
        MYSQL, ORACLE
    }

    // 使用ThreadLocal保证线程安全
    private static final ThreadLocal<DataBaseType> TYPE = new ThreadLocal<DataBaseType>();

    // 往当前线程里设置数据源类型
    public static void setDataBaseType(DataBaseType dataBaseType) {
        if (dataBaseType == null) {
            throw new NullPointerException();
        }
        //LOG.info("set database type:" + dataBaseType);
        TYPE.set(dataBaseType);
    }

    // 获取数据源类型
    public static DataBaseType getDataBaseType() {
        DataBaseType dataBaseType = TYPE.get() == null ? DataBaseType.MYSQL : TYPE.get();
        //LOG.info("get database type:" + dataBaseType);
        return dataBaseType;
    }

    // 清空数据类型
    public static void clearDataBaseType() {
        TYPE.remove();
    }

}
