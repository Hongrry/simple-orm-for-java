package cn.hruit.orm.scripting.xmltags;

/**
 * SQL 节点
 *
 * @author HONGRRY
 */
public interface SqlNode {

    boolean apply(DynamicContext context);

}