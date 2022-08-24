package cn.hruit.mybatis.session.defaults;

import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.Environment;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.session.SqlSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HONGRRY
 * @description 默认会话
 * @date 2022/08/22 14:39
 **/
public class DefaultSqlSession implements SqlSession {
    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            Environment environment = configuration.getEnvironment();
            Connection connection = environment.getDataSource().getConnection();
            BoundSql boundSql = mappedStatement.getBoundSql();
            PreparedStatement ps = connection.prepareStatement(boundSql.getSql());
            Object[] objects = (Object[]) parameter;
            ps.setLong(1, (Long) objects[0]);
            ResultSet resultSet = ps.executeQuery();
            List<T> list = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
            return list.get(0);
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

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
}
