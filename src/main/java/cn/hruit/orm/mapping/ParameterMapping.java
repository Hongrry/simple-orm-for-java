package cn.hruit.orm.mapping;


import cn.hruit.orm.session.Configuration;
import cn.hruit.orm.type.JdbcType;
import cn.hruit.orm.type.TypeHandler;
import cn.hruit.orm.type.TypeHandlerRegistry;

/**
 * 参数映射 #{property,javaType=int,jdbcType=NUMERIC}
 *
 * @author HONGRRY
 */
public class ParameterMapping {

    private Configuration configuration;

    /**
     * property 属性名
     */
    private String property;
    /**
     * javaType = int
     */
    private Class<?> javaType = Object.class;
    /**
     * jdbcType=NUMERIC
     */
    private JdbcType jdbcType;
    /**
     * 类型处理器
     */
    private TypeHandler<?> typeHandler;

    private ParameterMapping() {
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public static class Builder {

        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }

        public ParameterMapping build() {
            if (parameterMapping.typeHandler == null && parameterMapping.javaType != null) {
                Configuration configuration = parameterMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType, parameterMapping.jdbcType);
            }
            return parameterMapping;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }
}
