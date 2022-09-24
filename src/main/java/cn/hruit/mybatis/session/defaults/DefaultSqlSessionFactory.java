package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.executor.Executor;
import cn.hruit.mybatis.mapping.Environment;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.SqlSessionFactory;
import cn.hruit.mybatis.session.TransactionIsolationLevel;
import cn.hruit.mybatis.transaction.Transaction;
import cn.hruit.mybatis.transaction.TransactionFactory;

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
