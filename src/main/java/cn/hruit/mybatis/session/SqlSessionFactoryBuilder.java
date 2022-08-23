package cn.hruit.mybatis.session;

import cn.hruit.mybatis.builder.xml.XmlConfigBuilder;
import cn.hruit.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/23 11:10
 **/
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XmlConfigBuilder configBuilder = new XmlConfigBuilder(reader);
        return new DefaultSqlSessionFactory(configBuilder.parse());
    }
}
