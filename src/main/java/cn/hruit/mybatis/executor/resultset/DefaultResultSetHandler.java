package cn.hruit.mybatis.executor.resultset;

import cn.hruit.mybatis.executor.Executor;
import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.reflection.MetaObject;
import cn.hruit.mybatis.reflection.SystemMetaObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HONGRRY
 * @description 默认结果集处理器
 * @date 2022/08/29 10:08
 **/
public class DefaultResultSetHandler implements ResultSetHandler {
    private final MappedStatement mappedStatement;
    private final BoundSql boundSql;


    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        this.boundSql = boundSql;
        this.mappedStatement = mappedStatement;
    }

    @Override
    public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
        ResultSet resultSet = stmt.getResultSet();
        return resultSet2Obj(resultSet, mappedStatement.getResultType());
    }

    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 每次遍历行值
            while (resultSet.next()) {
                T obj = (T) clazz.newInstance();
                MetaObject metaObject = SystemMetaObject.forObject(obj);
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    String columnName = convertCamel(metaData.getColumnName(i));
            /*        String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method;*/
                    if (metaObject.hasSetter(columnName)) {
                        // 判断类型是否正确
                        metaObject.setValue(columnName, value);
                    }
                    // 转换

                    // 在Map中处理 Map
       /*             if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethod, Date.class);
                    } else {
                        method = clazz.getMethod(setMethod, value.getClass());
                    }
                    method.invoke(obj, value);*/
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String convertCamel(String original) {
        if (mappedStatement.getConfiguration().isMapUnderscoreToCamelCase()) {
            StringBuilder builder = new StringBuilder();
            String[] split = original.split("_");
            builder.append(split[0]);
            for (int i = 1; i < split.length; i++) {
                char[] array = split[i].toCharArray();
                builder.append(Character.toUpperCase(array[0]));
                builder.append(array, 1, array.length-1);
            }

            return builder.toString();
        }
        return original;
    }
}
