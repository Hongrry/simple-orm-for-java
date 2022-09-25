package cn.hruit.orm.scripting.defaults;

import cn.hruit.orm.executor.paramter.ParameterHandler;
import cn.hruit.orm.mapping.BoundSql;
import cn.hruit.orm.mapping.MappedStatement;
import cn.hruit.orm.mapping.ParameterMapping;
import cn.hruit.orm.reflection.MetaObject;
import cn.hruit.orm.session.Configuration;
import cn.hruit.orm.type.JdbcType;
import cn.hruit.orm.type.TypeHandler;
import cn.hruit.orm.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author HONGRRY
 * @description 默认参数处理器
 * @date 2022/09/03 17:37
 **/
public class DefaultParameterHandler implements ParameterHandler {
    private final MappedStatement mappedStatement;
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final Object parameterObject;
    private final BoundSql boundSql;
    private final Configuration configuration;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    @SuppressWarnings("all")
    public void setParameters(PreparedStatement ps) throws SQLException {
        // 多参数怎么处理
        // 怎么在给定的参数数组中找到需要的参数
        List<ParameterMapping> mappings = boundSql.getParameterMappings();
        for (int i = 0; i < mappings.size(); i++) {
            ParameterMapping parameterMapping = mappings.get(i);
            String propertyName = parameterMapping.getProperty();
            Object value;
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                value = metaObject.getValue(propertyName);
            }
            JdbcType jdbcType = parameterMapping.getJdbcType();
            // 设置参数
            TypeHandler typeHandler = parameterMapping.getTypeHandler();
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
        }
    }
}
