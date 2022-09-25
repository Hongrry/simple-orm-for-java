package cn.hruit.orm.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wraps a database connection.
 * Handles the connection lifecycle that comprises: its creation, preparation, commit/rollback and close.
 *
 * @author HONGRRY
 */
public interface Transaction {

    /**
     * Retrieve inner database connection.
     * 获取数据库连接
     *
     * @return DataBase connection
     * @throws SQLException the SQL exception
     */
    Connection getConnection() throws SQLException;

    /**
     * Commit inner database connection.
     * 提交事务
     *
     * @throws SQLException the SQL exception
     */
    void commit() throws SQLException;

    /**
     * Rollback inner database connection.
     * 回滚事务
     *
     * @throws SQLException the SQL exception
     */
    void rollback() throws SQLException;

    /**
     * Close inner database connection.
     * 关闭连接
     *
     * @throws SQLException the SQL exception
     */
    void close() throws SQLException;

    /**
     * Get transaction timeout if set.
     * 获取超时时间
     *
     * @return the timeout
     * @throws SQLException the SQL exception
     */
    Integer getTimeout() throws SQLException;

}
