package cn.hruit.mybatis.binding;

import cn.hruit.mybatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author HONGRRY
 * @description Mapper代理
 * @date 2022/08/22 13:35
 **/
public class MapperProxy<T> implements InvocationHandler {
    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(proxy, args);
        }
        //TODO 怎么获取绑定的SQL 2022年8月22日15:18:15
        return method.getName() + "被代理了";
    }
}
