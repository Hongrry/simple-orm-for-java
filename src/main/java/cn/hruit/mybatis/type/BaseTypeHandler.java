package cn.hruit.mybatis.type;

import cn.hruit.mybatis.session.Configuration;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 类型处理器基类
 *
 * @param <T> 泛型
 * @author HONGRRY
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {

    protected Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        // 定义抽象方法，由子类实现不同类型的属性设置
        setNonNullParameter(ps, i, parameter, jdbcType);
    }

    /**
     * 设置非空参数
     *
     * @param ps        预处理语句
     * @param i         参数位置
     * @param parameter 参数
     * @param jdbcType  JDBC类型
     * @throws SQLException 异常
     */
    protected abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

}