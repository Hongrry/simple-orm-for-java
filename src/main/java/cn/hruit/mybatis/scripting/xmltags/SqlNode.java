package cn.hruit.mybatis.scripting.xmltags;

/**
 * SQL 节点
 *
 * @author HONGRRY
 */
public interface SqlNode {

    boolean apply(DynamicContext context);

}