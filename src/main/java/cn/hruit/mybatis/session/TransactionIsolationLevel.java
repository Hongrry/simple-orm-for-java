package cn.hruit.mybatis.session;

import java.sql.Connection;

/**
 * 会话事务隔离级别
 *
 * @author HONGRRY
 */
public enum TransactionIsolationLevel {
    /**
     * 关闭事务
     */
    NONE(Connection.TRANSACTION_NONE),
    /**
     * 读提交
     */
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    /***
     * 读未提交
     */
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    /**
     * 可重复读
     */
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    /**
     * 串行序列化
     */
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int level;

    TransactionIsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}