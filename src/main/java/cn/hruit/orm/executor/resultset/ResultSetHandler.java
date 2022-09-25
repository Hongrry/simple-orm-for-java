package cn.hruit.orm.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 结果集处理器
 *
 * @author HONGRRY
 */
public interface ResultSetHandler {
    /**
     * 处理结果集
     *
     * @param stmt 语句
     * @param <E>  类型
     * @return 处理结果
     * @throws SQLException SQLException
     */
    <E> List<E> handleResultSets(Statement stmt) throws SQLException ;

}
