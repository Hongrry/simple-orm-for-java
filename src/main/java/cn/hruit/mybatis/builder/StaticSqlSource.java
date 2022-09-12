package cn.hruit.mybatis.builder;

import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.ParameterMapping;
import cn.hruit.mybatis.mapping.SqlSource;
import cn.hruit.mybatis.session.Configuration;

import java.util.List;

/**
 * @author HONGRRY
 * @description 静态SQL源码
 * @date 2022/09/03 16:39
 **/
public class StaticSqlSource implements SqlSource {

    private String sql;
    private List<ParameterMapping> parameterMappings;
    private Configuration configuration;


    public  StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }

}
