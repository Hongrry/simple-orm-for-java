package cn.hruit.orm.mapping;

/**
 * SQL源码
 *
 * @author HONGRRY
 */
public interface SqlSource {

    /**
     * 获取SQL封装
     *
     * @param parameterObject 参数
     * @return Sql 封装
     */
    BoundSql getBoundSql(Object parameterObject);

}
