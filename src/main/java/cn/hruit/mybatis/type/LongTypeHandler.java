package cn.hruit.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author HONGRRY
 * @description 长整型处理器
 * @date 2022/09/04 09:33
 **/
public class LongTypeHandler extends BaseTypeHandler<Long> {
    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter);
    }
}
