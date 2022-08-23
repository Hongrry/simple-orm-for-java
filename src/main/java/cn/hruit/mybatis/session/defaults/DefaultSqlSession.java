package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.session.SqlSession;

/**
 * @author HONGRRY
 * @description 默认会话
 * @date 2022/08/22 14:39
 **/
public class DefaultSqlSession implements SqlSession {
    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T) ("你被代理了！" + "\n方法：" + statement + "\n入参：" + parameter + "\n待执行SQL：" + mappedStatement.getSql());
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
