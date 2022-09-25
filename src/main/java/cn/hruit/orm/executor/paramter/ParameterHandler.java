package cn.hruit.orm.executor.paramter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 参数处理器
 *
 * @author HONGRRY
 */
public interface ParameterHandler {

    /**
     * 获取参数
     *
     * @return 参数
     */
    Object getParameterObject();

    /**
     * 设置参数
     *
     * @param ps 预备语句
     * @throws SQLException 异常
     */
    void setParameters(PreparedStatement ps) throws SQLException;

}
