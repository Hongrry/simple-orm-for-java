package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.executor.Executor;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.session.RowBounds;
import cn.hruit.mybatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * @author HONGRRY
 * @description 默认会话
 * @date 2022/08/22 14:39
 **/
public class DefaultSqlSession implements SqlSession {
    private Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);

    private final Configuration configuration;
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = this.<T>selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.query(ms, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
        } catch (SQLException e) {
            throw new RuntimeException("Error querying database.  Cause: " + e);
        }
    }

    @Override
    public int insert(String statement, Object parameter) {
        // 在 Mybatis 中 insert 调用的是 update
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.update(ms, parameter);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating database.  Cause: " + e);
        }
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public void commit() {
        commit(true);
    }

    @Override
    public void commit(boolean force) {
        try {
            executor.commit(force);
        } catch (Exception e) {
            throw new RuntimeException("Error committing transaction.  Cause: " + e, e);
        }
    }
}
