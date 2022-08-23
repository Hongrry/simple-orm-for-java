package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.SqlSessionFactory;

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
        return new DefaultSqlSession(configuration);
    }
}
