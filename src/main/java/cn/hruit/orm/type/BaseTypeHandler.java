package cn.hruit.orm.type;

import cn.hruit.orm.session.Configuration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        return getNullableResult(rs, columnName);
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getNullableResult(rs, columnIndex);
    }

    /**
     * 获取非空值
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return 结果
     * @throws SQLException 异常
     */
    protected abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

    /**
     * 获取非空值
     *
     * @param rs          结果集
     * @param columnIndex 列名
     * @return 结果
     * @throws SQLException 异常
     */
    protected abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

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