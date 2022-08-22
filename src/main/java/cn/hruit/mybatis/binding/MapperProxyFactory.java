package cn.hruit.mybatis.binding;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author HONGRRY
 * TODO Mybatis启动时会将配置文件中的sql解析，与接口方法进行绑定，保存在 sqlSession中
 * @description Mapper代理工厂
 * @date 2022/08/22 13:40
 **/
public class MapperProxyFactory<T> {
    private final Map<String, String> sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxyFactory(Map<String, String> sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    public T newInstance() {
        MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{mapperInterface}, mapperProxy);
    }
}
