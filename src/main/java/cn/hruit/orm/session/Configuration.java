package cn.hruit.orm.session;

import cn.hruit.orm.binding.MapperRegistry;
import cn.hruit.orm.builder.CacheRefResolver;
import cn.hruit.orm.builder.xml.XMLStatementBuilder;
import cn.hruit.orm.cache.Cache;
import cn.hruit.orm.cache.decorators.LruCache;
import cn.hruit.orm.cache.impl.PerpetualCache;
import cn.hruit.orm.datasource.pooled.PooledDataSourceFactory;
import cn.hruit.orm.datasource.unpooled.UnpooledDataSourceFactory;
import cn.hruit.orm.executor.CachingExecutor;
import cn.hruit.orm.executor.Executor;
import cn.hruit.orm.executor.SimpleExecutor;
import cn.hruit.orm.executor.keygen.KeyGenerator;
import cn.hruit.orm.executor.paramter.ParameterHandler;
import cn.hruit.orm.executor.resultset.DefaultResultSetHandler;
import cn.hruit.orm.executor.resultset.ResultSetHandler;
import cn.hruit.orm.executor.statement.PreparedStatementHandler;
import cn.hruit.orm.executor.statement.StatementHandler;
import cn.hruit.orm.mapping.BoundSql;
import cn.hruit.orm.mapping.Environment;
import cn.hruit.orm.mapping.MappedStatement;
import cn.hruit.orm.mapping.ResultMap;
import cn.hruit.orm.plugin.Interceptor;
import cn.hruit.orm.plugin.InterceptorChain;
import cn.hruit.orm.reflection.DefaultReflectorFactory;
import cn.hruit.orm.reflection.MetaObject;
import cn.hruit.orm.reflection.ReflectorFactory;
import cn.hruit.orm.reflection.factory.DefaultObjectFactory;
import cn.hruit.orm.reflection.factory.ObjectFactory;
import cn.hruit.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import cn.hruit.orm.reflection.wrapper.ObjectWrapperFactory;
import cn.hruit.orm.scripting.LanguageDriver;
import cn.hruit.orm.scripting.LanguageDriverRegistry;
import cn.hruit.orm.scripting.xmltags.XMLLanguageDriver;
import cn.hruit.orm.transaction.Transaction;
import cn.hruit.orm.transaction.jdbc.JdbcTransactionFactory;
import cn.hruit.orm.type.TypeAliasRegistry;
import cn.hruit.orm.type.TypeHandlerRegistry;

import java.util.*;

/**
 * @author HONGRRY
 * @description 全局会话配置
 * @date 2022/08/23 11:12
 **/
public class Configuration {
    /**
     * 是否启用驼峰映射
     */
    protected boolean mapUnderscoreToCamelCase;
    /**
     * 本地缓存作用域（一级缓存作用域）
     */
    protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;
    /**
     * 二级缓存是否启动
     */
    protected boolean cacheEnabled = true;
    private Environment environment;
    protected boolean useGeneratedKeys = false;
    private final MapperRegistry registry = new MapperRegistry(this);
    /**
     * 二级缓存
     */
    protected final Map<String, Cache> caches = new HashMap<>();
    /**
     * 二级缓存引用
     */
    protected final Map<String, String> cacheRefMap = new HashMap<String, String>();

    /**
     * ResultMap
     */
    protected final Map<String, ResultMap> resultMaps = new HashMap<>();
    protected final Map<String, KeyGenerator> keyGenerators = new HashMap<>();
    /**
     * 映射的语句，存在Map里
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();
    /**
     * 插件拦截链
     */
    protected final InterceptorChain interceptorChain = new InterceptorChain();
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
    /**
     * 未完成处理的缓存引用
     */
    protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<CacheRefResolver>();
    /**
     * 未完成处理的构建器
     */
    protected final Collection<XMLStatementBuilder> incompleteStatements = new LinkedList<XMLStatementBuilder>();

    public Configuration() {
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
        typeAliasRegistry.registerAlias("LRU", LruCache.class);


        // 设置默认XML语言驱动
        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
    }

    public void addMapper(Class<?> type) {
        registry.addMapper(type);
    }

    public void addMappers(String basePackage) {
        registry.addMappers(basePackage);
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

    /**
     * 创建结果集处理器
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement, resultHandler, rowBounds, boundSql);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }

    public Executor newExecutor(Transaction tx) {
        Executor executor = new SimpleExecutor(tx, this);
        if (cacheEnabled) {
            executor = new CachingExecutor(executor);
        }
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }

    /**
     * 创建语句处理器
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new PreparedStatementHandler(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
        // 嵌入插件
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
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
        parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
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

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public void addResultMap(ResultMap resultMap) {
        resultMaps.put(resultMap.getId(), resultMap);
    }

    public ResultMap getResultMap(String id) {
        return resultMaps.get(id);
    }


    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
        keyGenerators.put(id, keyGenerator);
    }

    public KeyGenerator getKeyGenerator(String id) {
        return keyGenerators.get(id);
    }

    public boolean hasKeyGenerator(String id) {
        return keyGenerators.containsKey(id);
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
    }

    public void setLocalCacheScope(LocalCacheScope localCacheScope) {
        this.localCacheScope = localCacheScope;
    }

    public LocalCacheScope getLocalCacheScope() {
        return localCacheScope;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public void addCache(Cache cache) {
        caches.put(cache.getId(), cache);
    }

    public Collection<String> getCacheNames() {
        return caches.keySet();
    }

    public Collection<Cache> getCaches() {
        return caches.values();
    }

    public Cache getCache(String id) {
        return caches.get(id);
    }

    public boolean hasCache(String id) {
        return caches.containsKey(id);
    }

    public void addCacheRef(String namespace, String referencedNamespace) {
        cacheRefMap.put(namespace, referencedNamespace);
    }

    public Collection<CacheRefResolver> getIncompleteCacheRefs() {
        return incompleteCacheRefs;
    }

    public void addIncompleteCacheRef(CacheRefResolver incompleteCacheRef) {
        incompleteCacheRefs.add(incompleteCacheRef);
    }

    public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
        incompleteStatements.add(incompleteStatement);
    }

    public Collection<XMLStatementBuilder> getIncompleteStatements() {
        return incompleteStatements;
    }
}
