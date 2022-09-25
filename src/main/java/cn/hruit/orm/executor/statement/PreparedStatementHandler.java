package cn.hruit.orm.executor.statement;

import cn.hruit.orm.executor.Executor;
import cn.hruit.orm.executor.keygen.Jdbc3KeyGenerator;
import cn.hruit.orm.executor.keygen.KeyGenerator;
import cn.hruit.orm.mapping.BoundSql;
import cn.hruit.orm.mapping.MappedStatement;
import cn.hruit.orm.session.ResultHandler;
import cn.hruit.orm.session.RowBounds;

import java.sql.*;
import java.util.List;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/29 09:10
 **/
public class PreparedStatementHandler extends BaseStatementHandler {
    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        String sql = boundSql.getSql();
        // 2022年9月18日19:19:14 添加 RETURN_GENERATED_KEYS  Jdbc3KeyGenerator才能正常的获取getGeneratedKeys
        if (mappedStatement.getKeyGenerator() instanceof Jdbc3KeyGenerator) {
            String[] keyColumnNames = mappedStatement.getKeyColumns();
            if (keyColumnNames == null) {
                return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            } else {
                return connection.prepareStatement(sql, keyColumnNames);
            }
        }
        return connection.prepareStatement(sql);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        parameterHandler.setParameters((PreparedStatement) statement);

    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.handleResultSets(ps);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        // 2022年9月18日19:15:27 将 execute 更换成 executeUpdate,否则无法获得  getGeneratedKeys
        ps.execute();
        int rows = ps.getUpdateCount();
        Object parameterObject = boundSql.getParameterObject();
        KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
        keyGenerator.processAfter(executor, mappedStatement, ps, parameterObject);
        return rows;
    }
}
