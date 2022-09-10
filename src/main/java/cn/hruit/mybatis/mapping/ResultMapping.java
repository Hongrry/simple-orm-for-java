package cn.hruit.mybatis.mapping;

import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.type.JdbcType;
import cn.hruit.mybatis.type.TypeHandler;

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
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;

    ResultMapping() {
    }

    public static class Builder {
        private ResultMapping resultMapping = new ResultMapping();

    }

    public String getColumn() {
        return column;
    }
}
