package cn.hruit.mybatis.binding;

import cn.hruit.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;

/**
 * @author HONGRRY
 * TODO Mybatis启动时会将配置文件中的sql解析，与接口方法进行绑定，保存在 sqlSession中
 * @description Mapper代理工厂
 * @date 2022/08/22 13:40
 **/
public class MapperProxyFactory<T> {
    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession) {
        MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{mapperInterface}, mapperProxy);
    }
}
