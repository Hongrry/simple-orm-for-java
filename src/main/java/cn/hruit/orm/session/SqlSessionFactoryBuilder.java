package cn.hruit.orm.session;

import cn.hruit.orm.builder.xml.XmlConfigBuilder;
import cn.hruit.orm.session.defaults.DefaultSqlSessionFactory;

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
