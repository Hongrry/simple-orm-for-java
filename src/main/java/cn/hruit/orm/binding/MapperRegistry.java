package cn.hruit.orm.binding;

import cn.hruit.orm.session.Configuration;
import cn.hruit.orm.session.SqlSession;
import cn.hutool.core.lang.ClassScanner;
import cn.hruit.orm.builder.annotation.MapperAnnotationBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author HONGRRY
 * @description 映射注册器
 * @date 2022/08/22 14:36
 **/
public class MapperRegistry {
    private Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();
    private Configuration configuration;

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<?> mapperProxyFactory = knownMappers.get(type);
        if (null == mapperProxyFactory) {
            throw new RuntimeException("Type: " + type + " is not in knownMappers");
        }
        // TODO 每次都是创建新的代理对象,不进行缓存吗 2022年8月22日15:23:56
        return (T) mapperProxyFactory.newInstance(sqlSession);
    }

    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry.");
            }
            // 解析注解
            MapperAnnotationBuilder parser = new MapperAnnotationBuilder(configuration, type);
            parser.parse();
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }
    }

    public void addMappers(String basePackage) {
        Set<Class<?>> classSet = ClassScanner.scanPackage(basePackage);
        for (Class<?> type : classSet) {
            addMapper(type);
        }
    }

    private <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }
}
