package cn.hruit.mybatis.plugin;

/**
 * 方法签名
 *
 * @author HONGRRY
 */
public @interface Signature {
    /**
     * 被拦截类
     */
    Class<?> type();

    /**
     * 被拦截类的方法
     */
    String method();

    /**
     * 被拦截类的方法的参数
     */
    Class<?>[] args();
}
