package cn.hruit.mybatis.reflection;

/**
 * 反射器工厂接口
 *
 * @author HONGRRY
 */
public interface ReflectorFactory {
    /**
     * 是否开启缓存
     *
     * @return 缓存状态
     */
    boolean isClassCacheEnabled();

    /**
     * 设置缓存状态
     *
     * @param status 缓存状态
     */
    void setClassCacheEnabled(boolean status);

    /**
     * 获取反射器
     *
     * @param clazz 类
     * @return 反射器
     */
    Reflector findForClass(Class<?> clazz);
}
