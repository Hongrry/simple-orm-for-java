package cn.hruit.mybatis.executor.resultset;

import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.type.TypeAliasRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author HONGRRY
 * @description 默认结果集处理器
 * @date 2022/08/29 10:08
 **/
public class DefaultResultSetHandler implements ResultSetHandler {
    private final MappedStatement mappedStatement;
    private final BoundSql boundSql;

    public DefaultResultSetHandler(MappedStatement mappedStatement) {
        this.mappedStatement = mappedStatement;
        this.boundSql = mappedStatement.getBoundSql();
    }

    @Override
    public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
        ResultSet resultSet = stmt.getResultSet();
        Configuration configuration = mappedStatement.getConfiguration();
        TypeAliasRegistry aliasRegistry = configuration.getTypeAliasRegistry();
        try {
            return resultSet2Obj(resultSet, aliasRegistry.resolveAlias(boundSql.getResultType()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ArrayList<T> list = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            T obj = (T) clazz.newInstance();
            for (int i = 1; i <= columnCount; i++) {
                Object value = resultSet.getObject(i);
                String columnName = metaData.getColumnName(i);
                String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);

                Method method;
                if (value instanceof Timestamp) {
                    method = clazz.getMethod(setMethod, Date.class);
                } else {
                    method = clazz.getMethod(setMethod, value.getClass());
                }
                method.invoke(obj, value);
            }
            list.add(obj);
        }
        return list;
    }

}
