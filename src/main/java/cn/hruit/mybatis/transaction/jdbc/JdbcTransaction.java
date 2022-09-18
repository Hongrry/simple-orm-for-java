package cn.hruit.mybatis.transaction.jdbc;

import cn.hruit.mybatis.session.TransactionIsolationLevel;
import cn.hruit.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author HONGRRY
 * @description JDBC事务
 * @date 2022/08/24 10:41
 **/
public class JdbcTransaction implements Transaction {
    private Connection connection;
    private DataSource dataSource;
    private TransactionIsolationLevel level;
    private boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(Connection conn) {
        this.connection = conn;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }
        connection = dataSource.getConnection();
        connection.setAutoCommit(autoCommit);
        connection.setTransactionIsolation(level.getLevel());
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();

        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.close();
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
