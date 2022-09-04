package cn.hruit.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author HONGRRY
 * @description 未知类型处理器
 * @date 2022/09/04 15:13
 **/
public class UnknownTypeHandler extends BaseTypeHandler<Object> {
    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }
}
