package cn.hruit.mybatis.session;

import cn.hruit.mybatis.binding.MapperRegistry;
import cn.hruit.mybatis.datasource.pooled.PooledDataSourceFactory;
import cn.hruit.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import cn.hruit.mybatis.executor.Executor;
import cn.hruit.mybatis.executor.SimpleExecutor;
import cn.hruit.mybatis.executor.paramter.ParameterHandler;
import cn.hruit.mybatis.executor.resultset.DefaultResultSetHandler;
import cn.hruit.mybatis.executor.resultset.ResultSetHandler;
import cn.hruit.mybatis.executor.statement.PreparedStatementHandler;
import cn.hruit.mybatis.executor.statement.StatementHandler;
import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.Environment;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.reflection.DefaultReflectorFactory;
import cn.hruit.mybatis.reflection.MetaObject;
import cn.hruit.mybatis.reflection.ReflectorFactory;
import cn.hruit.mybatis.reflection.factory.DefaultObjectFactory;
import cn.hruit.mybatis.reflection.factory.ObjectFactory;
import cn.hruit.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import cn.hruit.mybatis.reflection.wrapper.ObjectWrapperFactory;
import cn.hruit.mybatis.scripting.LanguageDriver;
import cn.hruit.mybatis.scripting.LanguageDriverRegistry;
import cn.hruit.mybatis.scripting.xmltags.XMLLanguageDriver;
import cn.hruit.mybatis.transaction.Transaction;
import cn.hruit.mybatis.transaction.jdbc.JdbcTransactionFactory;
import cn.hruit.mybatis.type.TypeAliasRegistry;
import cn.hruit.mybatis.type.TypeHandlerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    /**
     * 类型别名中心
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    /**
     * 语言驱动注册器
     */
    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
    /**
     * 类型处理注册器
     */
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    /**
     * 默认反射器工厂
     */
    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    /**
     * 默认对象工厂
     */
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    /**
     * 默认对象包装工厂
     */
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    /**
     * 资源加载集合，记录已经加载过的资源
     */
    protected final Set<String> loadedResources = new HashSet<>();
    /**
     * databaseId 的作用是什么
     */
    protected String databaseId;

    public Configuration() {
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        // 设置默认XML语言驱动
        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
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

    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    public Executor newExecutor(Transaction tx) {
        return new SimpleExecutor(tx, this);
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, ms, parameter, resultHandler, boundSql);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public LanguageDriverRegistry getLanguageRegistry() {
        return languageRegistry;
    }

    public MetaObject newMetaObject(Object parameterObject) {
        return MetaObject.forObject(parameterObject, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public Object getDatabaseId() {
        return databaseId;
    }

    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
        return parameterHandler;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return languageRegistry.getDefaultDriver();
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }
}
