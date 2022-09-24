package cn.hruit.mybatis.session;

/**
 * 本地缓存作用域
 *
 * @author HONGRRY
 */

public enum LocalCacheScope {
    /**
     * 会话作用域
     */
    SESSION,
    /**
     * 语句作用域
     */
    STATEMENT
}
