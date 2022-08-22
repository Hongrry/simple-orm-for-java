package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.binding.MapperRegistry;
import cn.hruit.mybatis.session.SqlSession;

/**
 * @author HONGRRY
 * @description 默认会话
 * @date 2022/08/22 14:39
 **/
public class DefaultSqlSession implements SqlSession {
    private final MapperRegistry registry;

    public DefaultSqlSession(MapperRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return (T) ("selectOne:" + parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return registry.getMapper(type, this);
    }
}
