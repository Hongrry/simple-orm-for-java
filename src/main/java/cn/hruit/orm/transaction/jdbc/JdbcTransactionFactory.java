package cn.hruit.orm.transaction.jdbc;

import cn.hruit.orm.session.TransactionIsolationLevel;
import cn.hruit.orm.transaction.Transaction;
import cn.hruit.orm.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author HONGRRY
 * @description JDBC事务工厂
 * @date 2022/08/24 10:49
 **/
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
