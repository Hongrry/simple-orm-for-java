package cn.hruit.mybatis.datasource.unpooled;

import cn.hruit.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author HONGRRY
 * @description 无池化数据源工厂
 * @date 2022/08/25 14:46
 **/
public class UnpooledDataSourceFactory implements DataSourceFactory {
    protected Properties props;

    public UnpooledDataSourceFactory() {
    }

    @Override
    public void setProperties(Properties props) {
        this.props = props;

    }

    @Override
    public DataSource getDataSource() {
        UnpooledDataSource dataSource = new UnpooledDataSource();
        dataSource.setDriver(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        return dataSource;
    }
}
