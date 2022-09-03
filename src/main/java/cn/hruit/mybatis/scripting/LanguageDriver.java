package cn.hruit.mybatis.scripting;


import cn.hruit.mybatis.mapping.SqlSource;
import cn.hruit.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * 脚本语言驱动
 *
 * @author HONGRRY
 */
public interface LanguageDriver {
    /**
     * 创建SQL源码
     *
     * @param configuration 全局配置
     * @param script        脚本节点
     * @param parameterType 参数类型
     * @return SQL源码
     */

    SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType);

}
