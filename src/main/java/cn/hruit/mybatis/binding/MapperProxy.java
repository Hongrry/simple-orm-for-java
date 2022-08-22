package cn.hruit.mybatis.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author HONGRRY
 * @description Mapper代理
 * @date 2022/08/22 13:35
 **/
public class MapperProxy<T> implements InvocationHandler {
    private final Map<String, String> sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxy(Map<String, String> sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(proxy, args);
        }
        return method.getName() + "被代理了:" + sqlSession.get(mapperInterface.getName() + "." + method.getName());
    }
}
