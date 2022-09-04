package cn.hruit.mybatis.scripting;


import cn.hruit.mybatis.executor.paramter.ParameterHandler;
import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.MappedStatement;
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
     * 创建参数处理器
     *
     * @param mappedStatement 映射语句
     * @param parameterObject 参数对象
     * @param boundSql        包装sql
     * @return 参数处理器
     */
    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

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
