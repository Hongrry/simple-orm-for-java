package cn.hruit.orm.executor;

import cn.hruit.orm.cache.CacheKey;
import cn.hruit.orm.cache.impl.PerpetualCache;
import cn.hruit.orm.mapping.BoundSql;
import cn.hruit.orm.mapping.MappedStatement;
import cn.hruit.orm.mapping.ParameterMapping;
import cn.hruit.orm.reflection.MetaObject;
import cn.hruit.orm.session.Configuration;
import cn.hruit.orm.session.LocalCacheScope;
import cn.hruit.orm.session.ResultHandler;
import cn.hruit.orm.session.RowBounds;
import cn.hruit.orm.transaction.Transaction;
import cn.hruit.orm.type.TypeHandlerRegistry;
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
    /**
     * 一级缓存
     * 会话的生命周期短，数据不会一直被存放
     */
    protected PerpetualCache localCache;
    private boolean closed;
    /**
     * 避免递归查询时，缓存被清空
     */
    protected int queryStack;

    public BaseExecutor(Transaction transaction, Configuration configuration) {
        this.transaction = transaction;
        this.configuration = configuration;
        this.localCache = new PerpetualCache("LocalCache");
        wrapper = this;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameter);
        CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
        return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }

        if (queryStack == 0 && ms.isFlushCacheRequired()) {
            clearLocalCache();
        }
        List<E> list = null;
        try {
            queryStack++;
            list = (List<E>) localCache.getObject(key);
            if (list == null) {
                list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
            }
        } finally {
            queryStack--;
        }
        // 如果是 STATEMENT 级别，需要清空缓存
        if (queryStack == 0 && configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
            clearLocalCache();
        }
        return list;
    }

    private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        List<E> list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
        localCache.putObject(key, list);
        return list;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        clearLocalCache();
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
            clearLocalCache();
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
            localCache = null;
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

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(ms.getId());
        cacheKey.update(rowBounds.getOffset());
        cacheKey.update(rowBounds.getLimit());
        cacheKey.update(boundSql.getSql());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
        // mimic DefaultParameterHandler logic
        for (ParameterMapping parameterMapping : parameterMappings) {
            Object value;
            String propertyName = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (parameterObject == null) {
                value = null;
            } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                value = metaObject.getValue(propertyName);
            }
            cacheKey.update(value);

        }
        if (configuration.getEnvironment() != null) {
            // issue #176
            cacheKey.update(configuration.getEnvironment().getId());
        }
        return cacheKey;
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return localCache.getObject(key) != null;
    }

    @Override
    public void clearLocalCache() {
        if (!closed) {
            localCache.clear();
        }
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
