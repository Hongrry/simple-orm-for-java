package cn.hruit.mybatis.executor;

import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.session.ResultHandler;
import cn.hruit.mybatis.session.RowBounds;
import cn.hruit.mybatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * @author HONGRRY
 * @description 执行器基类
 * @date 2022/08/28 20:28
 **/
public abstract class BaseExecutor implements Executor {
    private final Logger logger = LoggerFactory.getLogger(BaseExecutor.class);
    protected Transaction transaction;
    protected Executor wrapper;
    protected Configuration configuration;
    private boolean closed;

    public BaseExecutor(Transaction transaction, Configuration configuration) {
        this.transaction = transaction;
        this.configuration = configuration;
        wrapper = this;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return doUpdate(ms, parameter);
    }


    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new RuntimeException("Cannot commit, transaction is already closed");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }
    }

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return transaction;
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(true);
            } finally {
                transaction.close();
            }
        } catch (Exception e) {
            logger.warn("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        this.wrapper = executor;
    }

    /**
     * 执行查询
     *
     * @param ms            声明包装
     * @param parameter     参数
     * @param resultHandler 结果处理器
     * @param boundSql      SQL封装
     * @param rowBounds     分页记录限制
     * @return 查询结果
     * @return
     */
    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;

    /**
     * 执行更新
     *
     * @param ms        声明包装
     * @param parameter 参数
     * @return 更新结果
     */
    protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;
}
