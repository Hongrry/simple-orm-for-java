package cn.hruit.orm.builder.xml;

import cn.hruit.orm.builder.*;
import cn.hruit.orm.cache.Cache;
import cn.hruit.orm.io.Resources;
import cn.hruit.orm.mapping.ResultFlag;
import cn.hruit.orm.mapping.ResultMap;
import cn.hruit.orm.mapping.ResultMapping;
import cn.hruit.orm.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.*;

/**
 * @author HONGRRY
 * @description Mapper 构建器
 * @date 2022/09/03 15:28
 **/
public class XMLMapperBuilder extends BaseBuilder {

    private Element element;
    private String resource;
    private MapperBuilderAssistant builderAssistant;
    private String currentNamespace;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) throws DocumentException {
        this(new SAXReader().read(inputStream), configuration, resource);
    }

    private XMLMapperBuilder(Document document, Configuration configuration, String resource) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);

        this.element = document.getRootElement();
        this.resource = resource;
    }

    /**
     * 解析
     */
    public void parse() throws Exception {
        // 如果当前资源没有加载过再加载，防止重复加载
        if (!configuration.isResourceLoaded(resource)) {
            configurationElement(element);
            // 标记一下，已经加载过了
            configuration.addLoadedResource(resource);
            // 绑定映射器到namespace
            configuration.addMapper(Resources.classForName(currentNamespace));
        }
        // 处理因为条件不满足未完成的数据
        parsePendingCacheRefs();
        parsePendingStatements();
    }

    private void configurationElement(Element element) {
        // 1.配置namespace
        currentNamespace = element.attributeValue("namespace");
        if ("".equals(currentNamespace)) {
            throw new RuntimeException("Mapper's namespace cannot be empty");
        }
        builderAssistant.setCurrentNamespace(currentNamespace);
        // 解析ResultMap
        resultMapElements(element.elements("resultMap"));
        // 解析缓存引用
        cacheRefElement(element.element("cache-ref"));
        // 解析缓存
        cacheElement(element.element("cache"));
        // 2.配置select|insert|update|delete
        buildStatementFromContext(element.elements("select"),
                element.elements("insert"),
                element.elements("update"),
                element.elements("delete")
        );
    }

    private void parsePendingCacheRefs() {
        Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
        synchronized (incompleteCacheRefs) {
            Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().resolveCacheRef();
                    iter.remove();
                } catch (Exception e) {
                    // Cache ref is still missing a resource...
                }
            }
        }
    }

    private void parsePendingStatements() {
        Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
        synchronized (incompleteStatements) {
            Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().parseStatementNode();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // Statement is still missing a resource...
                }
            }
        }
    }

    private void cacheRefElement(Element cacheRef) {
        if (cacheRef != null) {
            String namespace = cacheRef.attributeValue("namespace");

            if (namespace == null || "".equals(namespace)) {
                throw new RuntimeException("namespace is empty or null");
            }
            configuration.addCacheRef(builderAssistant.getCurrentNamespace(), namespace);
            CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant, namespace);
            try {
                cacheRefResolver.resolveCacheRef();
            } catch (IncompleteElementException e) {
                // 会在最后进行解析
                configuration.addIncompleteCacheRef(cacheRefResolver);
            }
        }
    }

    private void cacheElement(Element cache) {
        if (cache != null) {
            // 底层实现
            String type = cache.attributeValue("type", "PERPETUAL");
            Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);

            // 淘汰策略
            String eviction = cache.attributeValue("eviction", "LRU");
            Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);

            // 刷新时间（过期时间？）
            Long flushInterval = longValueOf(cache.attributeValue("flushInterval"), null);

            // 缓存大小
            Integer size = integerValueOf(cache.attributeValue("size"), null);

            // 可读写（默认可以读写）
            boolean readWrite = !booleanValueOf(cache.attributeValue("readOnly"), false);

            // 是否阻塞
            boolean blocking = booleanValueOf(cache.attributeValue("blocking"), false);

            // 额外配置
            Properties props = new Properties();
            //Properties props = cache.elements("properties");
            builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
        }

    }

    private void resultMapElements(List<Element> resultMaps) {
        for (Element resultMap : resultMaps) {
            resultMapElement(resultMap);
        }
    }

    /**
     * <resultMap id="userResultMap" type="user">
     * <result column="user_id" property="userId"/>
     * <result column="user_name" property="userName"/>
     * <result column="user_head" property="userHead"/>
     * </resultMap>
     *
     * @param resultMapNode
     */
    private ResultMap resultMapElement(Element resultMapNode) {
        String id = resultMapNode.attributeValue("id");
        String type = resultMapNode.attributeValue("type");
        Class<Object> typeClass = typeAliasRegistry.resolveAlias(type);

        List<ResultMapping> resultMappings = new ArrayList<>();

        List<Element> resultChildren = resultMapNode.elements();
        for (Element resultChild : resultChildren) {
            // ResultFlag 的作用是什么?
            List<ResultFlag> flags = new ArrayList<>();
            if ("id".equals(resultChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            // 构建 ResultMapping
            resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
        }

        // 创建结果映射解析器
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, resultMappings);
        return resultMapResolver.resolve();
    }

    private ResultMapping buildResultMappingFromContext(Element context, Class<Object> resultType, List<ResultFlag> flags) {
        String property = context.attributeValue("property");
        String column = context.attributeValue("column");
        return builderAssistant.buildResultMapping(resultType, property, column, flags);
    }

    private void buildStatementFromContext(List<Element>... lists) {
        for (List<Element> list : lists) {
            for (Element element : list) {
                final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, element, builderAssistant);
                try {
                    statementParser.parseStatementNode();
                } catch (IncompleteElementException e) {
                    configuration.addIncompleteStatement(statementParser);
                }
            }
        }
    }
}
