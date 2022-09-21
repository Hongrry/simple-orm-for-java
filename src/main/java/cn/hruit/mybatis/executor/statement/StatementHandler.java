package cn.hruit.mybatis.executor.statement;

import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 语句处理器
 *
 * @author HONGRRY
 */
public interface StatementHandler {
    /**
     * 准备语句
     *
     * @param connection 连接
     * @return 语句
     */
    Statement prepare(Connection connection);

    /**
     * 参数化
     *
     * @param stmt 语句
     */
    void parameterize(Statement stmt) throws SQLException;

    /**
     * 查询语句
     *
     * @param stmt          语句
     * @param resultHandler 结果处理器
     * @return 结果
     */
    <E> List<E> query(Statement stmt, ResultHandler resultHandler) throws SQLException;

    /**
     * 更新语句
     *
     * @param stmt 语句
     * @return 更新结果
     */
    int update(Statement stmt) throws SQLException;

    BoundSql getBoundSql();
}
