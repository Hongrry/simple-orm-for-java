package cn.hruit.mybatis.session;

import cn.hruit.mybatis.binding.MapperRegistry;
import cn.hruit.mybatis.datasource.pooled.PooledDataSourceFactory;
import cn.hruit.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import cn.hruit.mybatis.mapping.Environment;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.transaction.jdbc.JdbcTransactionFactory;
import cn.hruit.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HONGRRY
 * @description 全局会话配置
 * @date 2022/08/23 11:12
 **/
public class Configuration {
    private Environment environment;

    private final MapperRegistry registry = new MapperRegistry();
    /**
     * 映射的语句，存在Map里
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
    }

    public void addMapper(Class<?> type) {
        registry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return registry.getMapper(type, sqlSession);
    }

    public void addMappedStatement(MappedStatement mappedStatement) {
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }
}
