package cn.hruit.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author HONGRRY
 * @description 字符串处理器
 * @date 2022/09/04 09:34
 **/
public class StringTypeHandler extends BaseTypeHandler<String> {
    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }
}
