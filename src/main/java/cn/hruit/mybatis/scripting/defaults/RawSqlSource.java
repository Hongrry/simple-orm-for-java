package cn.hruit.mybatis.scripting.defaults;

import cn.hruit.mybatis.builder.xml.SqlSourceBuilder;
import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.SqlSource;
import cn.hruit.mybatis.scripting.xmltags.DynamicContext;
import cn.hruit.mybatis.scripting.xmltags.SqlNode;
import cn.hruit.mybatis.session.Configuration;

import java.util.HashMap;

/**
 * 原生静态SQL
 *
 * @author HONGRRY
 */
public class RawSqlSource implements SqlSource {

    private final SqlSource sqlSource;

    public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
        this(configuration, getSql(configuration, rootSqlNode), parameterType);
    }

    public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Class<?> clazz = parameterType == null ? Object.class : parameterType;
        sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(configuration, null);
        rootSqlNode.apply(context);
        return context.getSql();
    }

}
