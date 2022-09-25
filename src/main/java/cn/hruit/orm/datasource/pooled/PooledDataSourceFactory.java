package cn.hruit.orm.datasource.pooled;

import cn.hruit.orm.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @author HONGRRY
 * @description 池化数据源工厂
 * @date 2022/08/25 15:22
 **/
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {
    public PooledDataSourceFactory() {
        dataSource = new PooledDataSource();

    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
