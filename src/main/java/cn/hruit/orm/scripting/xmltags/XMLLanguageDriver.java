package cn.hruit.orm.scripting.xmltags;

import cn.hruit.orm.executor.paramter.ParameterHandler;
import cn.hruit.orm.mapping.BoundSql;
import cn.hruit.orm.mapping.MappedStatement;
import cn.hruit.orm.mapping.SqlSource;
import cn.hruit.orm.scripting.LanguageDriver;
import cn.hruit.orm.scripting.defaults.DefaultParameterHandler;
import cn.hruit.orm.scripting.defaults.RawSqlSource;
import cn.hruit.orm.session.Configuration;
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
