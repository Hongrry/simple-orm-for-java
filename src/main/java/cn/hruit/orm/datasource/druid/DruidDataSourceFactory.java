package cn.hruit.orm.datasource.druid;

import cn.hruit.orm.datasource.DataSourceFactory;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author HONGRRY
 * 日常使用时是通过 jar包 进行集成的，后期怎么设计集成外部数据源和动态数据源
 * @description Druid 数据源工厂
 * @date 2022/08/24 10:18
 **/
public class DruidDataSourceFactory implements DataSourceFactory {
    private Properties properties;

    @Override
    public void setProperties(Properties props) {
        this.properties = props;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(properties.getProperty("driver"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        return dataSource;
    }
}
