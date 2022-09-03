package cn.hruit.mybatis.builder.xml;

import cn.hruit.mybatis.builder.BaseBuilder;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.mapping.SqlCommandType;
import cn.hruit.mybatis.mapping.SqlSource;
import cn.hruit.mybatis.scripting.LanguageDriver;
import cn.hruit.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.Locale;

/**
 * @author HONGRRY
 * @description XML语句构建器
 * @date 2022/09/03 15:38
 **/
public class XMLStatementBuilder extends BaseBuilder {

    private String currentNamespace;
    private Element element;

    public XMLStatementBuilder(Configuration configuration, Element element, String currentNamespace) {
        super(configuration);
        this.element = element;
        this.currentNamespace = currentNamespace;
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
        // 结果类型
        String resultType = element.attributeValue("resultType");
        Class<?> resultTypeClass = resolveAlias(resultType);
        // 获取命令类型(select|insert|update|delete)
        String nodeName = element.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

        // 获取默认语言驱动器
        Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);

        SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);

        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, currentNamespace + "." + id, sqlCommandType, sqlSource, resultTypeClass).build();

        // 添加解析 SQL
        configuration.addMappedStatement(mappedStatement);

    }
}
