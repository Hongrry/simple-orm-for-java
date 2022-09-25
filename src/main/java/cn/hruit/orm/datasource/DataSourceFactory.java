package cn.hruit.orm.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据源工厂
 *
 * @author HONGRRY
 */
public interface DataSourceFactory {
    /**
     * 设置属性
     *
     * @param props 数据源属性
     */
    void setProperties(Properties props);

    /**
     * 获取数据源
     *
     * @return 数据源
     */
    DataSource getDataSource();
}
