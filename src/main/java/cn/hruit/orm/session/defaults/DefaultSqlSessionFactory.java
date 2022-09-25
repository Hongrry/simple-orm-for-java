package cn.hruit.orm.session.defaults;

import cn.hruit.orm.executor.Executor;
import cn.hruit.orm.mapping.Environment;
import cn.hruit.orm.session.Configuration;
import cn.hruit.orm.session.SqlSession;
import cn.hruit.orm.session.SqlSessionFactory;
import cn.hruit.orm.session.TransactionIsolationLevel;
import cn.hruit.orm.transaction.Transaction;
import cn.hruit.orm.transaction.TransactionFactory;

import java.sql.SQLException;

/**
 * @author HONGRRY
 * @description 默认会话工厂
 * @date 2022/08/22 15:02
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSession(false);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        Transaction tx = null;
        Environment environment = configuration.getEnvironment();
        try {
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, autoCommit);
            Executor executor = configuration.newExecutor(tx);
            return new DefaultSqlSession(configuration, executor);
        } catch (Exception e) {
            try {
                assert tx != null;
                tx.close();
            } catch (SQLException ignore) {
            }
            throw new RuntimeException("Error opening session.  Cause: " + e);
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
