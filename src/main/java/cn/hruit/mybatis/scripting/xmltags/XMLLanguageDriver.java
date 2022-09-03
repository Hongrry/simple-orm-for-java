package cn.hruit.mybatis.scripting.xmltags;

import cn.hruit.mybatis.mapping.SqlSource;
import cn.hruit.mybatis.scripting.LanguageDriver;
import cn.hruit.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * @author HONGRRY
 * @description XML语言驱动
 * @date 2022/09/03 16:04
 **/
public class XMLLanguageDriver implements LanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }
}
