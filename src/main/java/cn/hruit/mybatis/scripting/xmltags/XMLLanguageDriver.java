package cn.hruit.mybatis.scripting.xmltags;

import cn.hruit.mybatis.executor.paramter.ParameterHandler;
import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.mapping.SqlSource;
import cn.hruit.mybatis.scripting.LanguageDriver;
import cn.hruit.mybatis.scripting.defaults.DefaultParameterHandler;
import cn.hruit.mybatis.scripting.defaults.RawSqlSource;
import cn.hruit.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * @author HONGRRY
 * @description XML语言驱动
 * @date 2022/09/03 16:04
 **/
public class XMLLanguageDriver implements LanguageDriver {
    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        // 暂时不解析动态 SQL
        return new RawSqlSource(configuration, script, parameterType);
    }
}
