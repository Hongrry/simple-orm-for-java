package cn.hruit.orm.reflection.invoker;

/**
 * 调用器通用接口
 *
 * @author HONGRRY
 */
public interface Invoker {
    /**
     * 调用
     *
     * @param target 目标对象
     * @param args   参数
     * @return 结果
     * @throws Exception 异常
     */
    Object invoke(Object target, Object[] args) throws Exception;

    /**
     * 获取类型
     *
     * @return Class
     */
    Class<?> getType();
}
