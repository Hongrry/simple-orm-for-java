package cn.hruit.orm.builder.xml;

import cn.hruit.orm.builder.BaseBuilder;
import cn.hruit.orm.builder.MapperBuilderAssistant;
import cn.hruit.orm.executor.keygen.Jdbc3KeyGenerator;
import cn.hruit.orm.executor.keygen.KeyGenerator;
import cn.hruit.orm.executor.keygen.NoKeyGenerator;
import cn.hruit.orm.executor.keygen.SelectKeyGenerator;
import cn.hruit.orm.mapping.MappedStatement;
import cn.hruit.orm.mapping.SqlCommandType;
import cn.hruit.orm.mapping.SqlSource;
import cn.hruit.orm.scripting.LanguageDriver;
import cn.hruit.orm.session.Configuration;
import org.dom4j.Element;

import java.util.List;
import java.util.Locale;

/**
 * @author HONGRRY
 * @description XML语句构建器
 * @date 2022/09/03 15:38
 **/
public class XMLStatementBuilder extends BaseBuilder {

    private MapperBuilderAssistant builderAssistant;

    private Element element;

    public XMLStatementBuilder(Configuration configuration, Element element, MapperBuilderAssistant builderAssistant) {
        super(configuration);
        this.element = element;
        this.builderAssistant = builderAssistant;
    }

    /**
     * <select id="queryUserInfoById" parameterType="java.lang.Long" resultType="user">
     * SELECT id, userId, userName, userHead
     * FROM user
     * where id = #{id}
     * </select>
     */
    public void parseStatementNode() {
        String id = element.attributeValue("id");
        // 参数类型
        String parameterType = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveAlias(parameterType);
        // 外部应用 resultMap
        String resultMap = element.attributeValue("resultMap");
        // 结果类型
        String resultType = element.attributeValue("resultType");
        Class<?> resultTypeClass = resolveAlias(resultType);
        // 获取命令类型(select|insert|update|delete)
        String nodeName = element.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        boolean flushCache = booleanValueOf(element.attributeValue("flushCache"), !isSelect);
        // 只有Select语句可以使用 useCache 属性,默认值为true 使用缓存
        boolean useCache = booleanValueOf(element.attributeValue("useCache"), isSelect);

        // 获取默认语言驱动器
        Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);

        // 解析<selectKey> step-14 新增
        processSelectKeyNodes(id, parameterTypeClass, langDriver);

        // 解析成SqlSource，DynamicSqlSource/RawSqlSource
        SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);

        // 属性标记【仅对 insert 有用】, MyBatis 会通过 getGeneratedKeys 或者通过 insert 语句的 selectKey 子元素设置它的值
        String keyProperty = element.attributeValue("keyProperty");

        KeyGenerator keyGenerator = null;
        String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;

        keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);
        // keyStatementId 什么时候有什么用
        // processSelectKeyNodes 会创建一个 id=keyStatementId 的键值构建器
        if (configuration.hasKeyGenerator(keyStatementId)) {
            keyGenerator = configuration.getKeyGenerator(keyStatementId);
        } else {
            String useGeneratedKeys = element.attributeValue("useGeneratedKeys");
            keyGenerator = booleanValueOf(useGeneratedKeys,
                    configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType))
                    ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
        }

        // 调用助手类
        builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultMap,
                resultTypeClass,
                flushCache,
                useCache,
                keyGenerator,
                keyProperty,
                langDriver);

    }

    private void processSelectKeyNodes(String id, Class<?> parameterTypeClass, LanguageDriver langDriver) {
        List<Element> selectKeyNodes = element.elements("selectKey");
        parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver);

    }

    private void parseSelectKeyNodes(String parentId, List<Element> list, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        for (Element nodeToHandle : list) {
            String id = parentId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
            parseSelectKeyNode(id, nodeToHandle, parameterTypeClass, languageDriver);
        }
    }

    /**
     * <selectKey keyProperty="id" order="AFTER" resultType="long">
     * SELECT LAST_INSERT_ID()
     * </selectKey>
     */
    private void parseSelectKeyNode(String id, Element nodeToHandle, Class<?> parameterTypeClass, LanguageDriver langDriver) {
        String resultType = nodeToHandle.attributeValue("resultType");
        Class<?> resultTypeClass = resolveAlias(resultType);
        boolean executeBefore = "BEFORE".equals(nodeToHandle.attributeValue("order", "AFTER"));
        String keyProperty = nodeToHandle.attributeValue("keyProperty");

        // default
        String resultMap = null;
        KeyGenerator keyGenerator = new NoKeyGenerator();

        // 解析成SqlSource，DynamicSqlSource/RawSqlSource
        SqlSource sqlSource = langDriver.createSqlSource(configuration, nodeToHandle, parameterTypeClass);
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;

        // 调用助手类
        MappedStatement keyStatement = builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultMap,
                resultTypeClass,
                false,
                false,
                keyGenerator,
                keyProperty,
                langDriver);

        // 给id加上namespace前缀
        id = builderAssistant.applyCurrentNamespace(id, false);

        // 存放键值生成器配置
        configuration.addKeyGenerator(id, new SelectKeyGenerator(keyStatement, executeBefore));
    }
}
