package cn.hruit.mybatis.scripting.defaults;

import cn.hruit.mybatis.executor.paramter.ParameterHandler;
import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.mapping.ParameterMapping;
import cn.hruit.mybatis.reflection.MetaObject;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.type.JdbcType;
import cn.hruit.mybatis.type.TypeHandler;
import cn.hruit.mybatis.type.TypeHandlerRegistry;

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
