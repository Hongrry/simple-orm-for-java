package cn.hruit.orm.builder;

import cn.hruit.orm.mapping.BoundSql;
import cn.hruit.orm.mapping.ParameterMapping;
import cn.hruit.orm.mapping.SqlSource;
import cn.hruit.orm.session.Configuration;

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
