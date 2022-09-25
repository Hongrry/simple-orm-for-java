package cn.hruit.orm.scripting.defaults;

import cn.hruit.orm.builder.SqlSourceBuilder;
import cn.hruit.orm.mapping.BoundSql;
import cn.hruit.orm.mapping.SqlSource;
import cn.hruit.orm.scripting.xmltags.DynamicContext;
import cn.hruit.orm.scripting.xmltags.SqlNode;
import cn.hruit.orm.session.Configuration;

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
