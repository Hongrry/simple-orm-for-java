package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.binding.MapperRegistry;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.SqlSessionFactory;

/**
 * @author HONGRRY
 * @description 默认会话工厂
 * @date 2022/08/22 15:02
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final MapperRegistry registry;

    public DefaultSqlSessionFactory(MapperRegistry registry) {
        this.registry = registry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(registry);
    }
}
