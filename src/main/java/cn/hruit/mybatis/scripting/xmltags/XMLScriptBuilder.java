package cn.hruit.mybatis.scripting.xmltags;

import cn.hruit.mybatis.builder.BaseBuilder;
import cn.hruit.mybatis.mapping.SqlSource;
import cn.hruit.mybatis.scripting.defaults.RawSqlSource;
import cn.hruit.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * XML脚本构建器
 *
 * @author HONGRRY
 */
public class XMLScriptBuilder extends BaseBuilder {

    private Element element;
    private boolean isDynamic;
    private Class<?> parameterType;

    public XMLScriptBuilder(Configuration configuration, Element element, Class<?> parameterType) {
        super(configuration);
        this.element = element;
        this.parameterType = parameterType;
    }

    public SqlSource parseScriptNode() {
        List<SqlNode> contents = parseDynamicTags(element);
        MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
        return new RawSqlSource(configuration, rootSqlNode, parameterType);
    }

    List<SqlNode> parseDynamicTags(Element element) {
        List<SqlNode> contents = new ArrayList<>();
        // element.getText 拿到 SQL
        String data = element.getText();
        // 动态SQL怎么处理，将SQL进行分片
        contents.add(new StaticTextSqlNode(data));
        return contents;
    }

}
