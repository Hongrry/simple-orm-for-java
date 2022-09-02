package cn.hruit.mybatis.reflection.wrapper;

import cn.hruit.mybatis.reflection.MetaObject;
import cn.hruit.mybatis.reflection.factory.ObjectFactory;
import cn.hruit.mybatis.reflection.property.PropertyTokenizer;

import java.util.Collection;
import java.util.List;

/**
 * @author HONGRRY
 * @description Collection包装器
 * @date 2022/09/01 22:55
 **/
public class CollectionWrapper extends BaseWrapper {
    private final Collection<Object> originalCollection;

    public CollectionWrapper(MetaObject metaObject, Collection<Object> originalCollection) {
        super(metaObject);
        this.originalCollection = originalCollection;
    }

    /**
     * 为什么Get Set 是不允许的
     *
     * @param prop 属性
     * @return
     */
    @Override
    public Object get(PropertyTokenizer prop) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        throw new UnsupportedOperationException();

    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        throw new UnsupportedOperationException();

    }

    @Override
    public String[] getGetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void add(Object element) {
        originalCollection.add(element);
    }

    @Override
    public <E> void addAll(List<E> element) {
        originalCollection.addAll(element);
    }
}
