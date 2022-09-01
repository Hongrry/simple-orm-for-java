package cn.hruit.mybatis.reflection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HONGRRY
 * @description 反射器工厂
 * @date 2022/08/31 17:37
 **/
public class DefaultReflectorFactory implements ReflectorFactory {
    /**
     * 类反射器缓存状态
     */
    private boolean classCacheEnabled = true;
    /**
     * 类反射器缓存
     */
    private final Map<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

    @Override
    public boolean isClassCacheEnabled() {
        return classCacheEnabled;
    }

    @Override
    public void setClassCacheEnabled(boolean status) {
        this.classCacheEnabled = status;
    }

    @Override
    public Reflector findForClass(Class<?> clazz) {
        if (classCacheEnabled) {
            return reflectorMap.computeIfAbsent(clazz, v -> new Reflector(clazz));
        } else {
            return new Reflector(clazz);
        }
    }
}
