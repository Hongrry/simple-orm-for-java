package cn.hruit.mybatis.datasource.pooled;

import cn.hruit.mybatis.datasource.unpooled.UnpooledDataSource;
import cn.hruit.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @author HONGRRY
 * @description 池化数据源工厂
 * @date 2022/08/25 15:22
 **/
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {
    @Override
    public DataSource getDataSource() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        return dataSource;
    }
}
