package cn.hruit.orm.plugin;

import java.util.Properties;

/**
 * @author HONGRRY
 * @description 拦截器接口
 * @date 2022/09/21 10:19
 **/
public interface Interceptor {

    /**
     * 拦截，使用方实现
     *
     * @param invocation 调用信息
     * @return 调用结果
     * @throws Throwable 异常
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 应用插件
     *
     * @param target 原对象
     * @return 代理对象
     */
    default Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置属性
     *
     * @param properties 属性
     */
    default void setProperties(Properties properties) {
        // NOP
    }
}
