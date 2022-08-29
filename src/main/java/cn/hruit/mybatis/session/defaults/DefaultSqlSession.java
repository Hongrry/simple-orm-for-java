package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.executor.Executor;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.session.SqlSession;

import java.util.List;

/**
 * @author HONGRRY
 * @description 默认会话
 * @date 2022/08/22 14:39
 **/
public class DefaultSqlSession implements SqlSession {
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
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        List<T> list = executor.query(mappedStatement, parameter, Executor.NO_RESULT_HANDLER, mappedStatement.getBoundSql());
        return list.get(0);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
}
