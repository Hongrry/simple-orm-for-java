package cn.hruit.mybatis.builder.xml;

import cn.hruit.mybatis.builder.BaseBuilder;
import cn.hruit.mybatis.builder.MapperBuilderAssistant;
import cn.hruit.mybatis.builder.ResultMapResolver;
import cn.hruit.mybatis.io.Resources;
import cn.hruit.mybatis.mapping.ResultFlag;
import cn.hruit.mybatis.mapping.ResultMapping;
import cn.hruit.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import cn.hruit.mybatis.mapping.ResultMap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        // 2.配置select|insert|update|delete
        buildStatementFromContext(element.elements("select"),
                element.elements("insert"),
                element.elements("update"),
                element.elements("delete")
        );
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
                statementParser.parseStatementNode();
            }
        }
    }
}
