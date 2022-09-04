package cn.hruit.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 类型处理器
 *
 * @param <T> 类型
 * @author HONGRRY
 */
public interface TypeHandler<T> {

    /**
     * 设置参数
     *
     * @param ps        预处理语句
     * @param i         参数位置
     * @param parameter 参数
     * @param jdbcType  JDBC类型
     * @throws SQLException 异常
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

}
