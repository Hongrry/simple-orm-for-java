package cn.hruit.mybatis.mapping;

import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.type.TypeHandler;
import cn.hruit.mybatis.type.TypeHandlerRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HONGRRY
 * @description 结果映射
 * @date 2022/09/10 20:09
 **/
public class ResultMapping {

    private Configuration configuration;
    private String property;
    private String column;
    private Class<?> javaType;
    private TypeHandler<?> typeHandler;
    private List<ResultFlag> flags;

    ResultMapping() {
    }

    public static class Builder {

        private ResultMapping resultMapping = new ResultMapping();

        public Builder(Configuration configuration, String property, String column, Class<?> javaType) {
            resultMapping.configuration = configuration;
            resultMapping.property = property;
            resultMapping.column = column;
            resultMapping.javaType = javaType;
            resultMapping.flags = new ArrayList<>();
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            resultMapping.typeHandler = typeHandler;
            return this;
        }

        public ResultMapping build() {
            resolveTypeHandler();
            return resultMapping;
        }

        public Builder flags(List<ResultFlag> flags) {
            resultMapping.flags = flags;
            return this;
        }

        private void resolveTypeHandler() {
            if (resultMapping.typeHandler == null && resultMapping.javaType != null) {
                Configuration configuration = resultMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                resultMapping.typeHandler = typeHandlerRegistry.getTypeHandler(resultMapping.javaType, null);
            }
        }

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public String getColumn() {
        return column;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public List<ResultFlag> getFlags() {
        return flags;
    }

}
