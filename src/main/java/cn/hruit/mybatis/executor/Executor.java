package cn.hruit.mybatis.executor;


import cn.hruit.mybatis.mapping.BoundSql;
import cn.hruit.mybatis.mapping.MappedStatement;
import cn.hruit.mybatis.session.ResultHandler;
import cn.hruit.mybatis.session.RowBounds;
import cn.hruit.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * 执行器接口
 *
 * @author HONGRRY
 */
public interface Executor {
    ResultHandler NO_RESULT_HANDLER = null;

    /**
     * 更新
     *
     * @param ms        声明包装
     * @param parameter 参数
     * @return 更新结果
     * @throws SQLException 异常
     */
    int update(MappedStatement ms, Object parameter) throws SQLException;

    /**
     * 查询
     *
     * @param ms            声明包装
     * @param parameter     参数
     * @param resultHandler 结果处理器
     * @param rowBounds     分页限制
     * @param boundSql      SQL 封装
     * @return 查询结果
     */
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;

    /**
     * 提交事务
     *
     * @param required required
     * @throws SQLException SQLException
     */
    void commit(boolean required) throws SQLException;

    /**
     * 回滚事务
     *
     * @param required required
     * @throws SQLException SQLException
     */
    void rollback(boolean required) throws SQLException;

    /**
     * 获取事务
     *
     * @return 事务
     */
    Transaction getTransaction();

    /**
     * 关闭执行器
     *
     * @param forceRollback 是否强制回滚
     */
    void close(boolean forceRollback);

    /**
     * 执行器是否已关闭
     *
     * @return 结果
     */
    boolean isClosed();

    /**
     * setExecutorWrapper
     *
     * @param executor executor
     */
    void setExecutorWrapper(Executor executor);
}
