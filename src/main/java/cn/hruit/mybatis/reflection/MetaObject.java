package cn.hruit.mybatis.reflection;

import cn.hruit.mybatis.reflection.factory.ObjectFactory;
import cn.hruit.mybatis.reflection.property.PropertyTokenizer;
import cn.hruit.mybatis.reflection.wrapper.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author HONGRRY
 * @description 元对象
 * @date 2022/09/01 16:31
 **/
public class MetaObject {
    /**
     * 原对象
     */
    private final Object originalObject;
    /**
     * 对象包装器
     */
    private final ObjectWrapper objectWrapper;
    /**
     * 对象工厂
     */
    private final ObjectFactory objectFactory;
    /**
     * 对象包装工厂
     */
    private final ObjectWrapperFactory objectWrapperFactory;
    /**
     * 反射器工厂
     */
    private final ReflectorFactory reflectorFactory;

    private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;
        this.reflectorFactory = reflectorFactory;

        if (object instanceof ObjectWrapper) {
            // 如果对象本身已经是ObjectWrapper型，则直接赋给objectWrapper
            this.objectWrapper = (ObjectWrapper) object;
        } else if (objectWrapperFactory.hasWrapperFor(object)) {
            // 如果有包装器,调用ObjectWrapperFactory.getWrapperFor
            this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
        } else if (object instanceof Map) {
            // 如果是Map型，返回MapWrapper
            this.objectWrapper = new MapWrapper(this, (Map) object);
        } else if (object instanceof Collection) {
            // 如果是Collection型，返回CollectionWrapper
            this.objectWrapper = new CollectionWrapper(this, (Collection) object);
        } else {
            // 除此以外，返回BeanWrapper
            this.objectWrapper = new BeanWrapper(this, object);
        }
    }

    public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
        if (object == null) {
            // 处理一下null,将null包装起来
            return SystemMetaObject.NULL_META_OBJECT;
        } else {
            return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
        }
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    /* --------以下方法都是委派给 ObjectWrapper------ */

    /**
     * 查找属性
     *
     * @param propName            属性名
     * @param useCamelCaseMapping 是否驼峰命名
     * @return 属性
     */
    public String findProperty(String propName, boolean useCamelCaseMapping) {
        return objectWrapper.findProperty(propName, useCamelCaseMapping);
    }

    /**
     * 取得getter的名字列表
     *
     * @return getter 名字列表
     */
    public String[] getGetterNames() {
        return objectWrapper.getGetterNames();
    }

    /**
     * 取得setter的名字列表
     *
     * @return setter 名字列表
     */
    public String[] getSetterNames() {
        return objectWrapper.getSetterNames();
    }

    /**
     * 取得setter的类型列表
     *
     * @param name 属性名
     * @return 属性类型
     */
    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    /**
     * 取得getter的类型列表
     *
     * @param name 属性名
     * @return 属性类型
     */
    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }

    /**
     * 是否有指定的setter
     *
     * @param name 属性名
     * @return 结果
     */
    public boolean hasSetter(String name) {
        return objectWrapper.hasSetter(name);
    }

    /**
     * 是否有指定的getter
     *
     * @param name 属性名
     * @return 结果
     */
    public boolean hasGetter(String name) {
        return objectWrapper.hasGetter(name);
    }

    /**
     * 获取属性值
     *
     * @param name 属性名
     * @return 属性值
     */
    public Object getValue(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                // 如果上层就是null了，那就结束，返回null
                return null;
            } else {
                // 否则继续看下一层，递归调用getValue
                return metaValue.getValue(prop.getChildren());
            }
        } else {
            return objectWrapper.get(prop);
        }
    }

    /**
     * 设置属性值
     *
     * @param name  属性名
     * @param value 属性值
     */
    public void setValue(String name, Object value) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                if (value == null && prop.getChildren() != null) {
                    // don't instantiate child path if value is null
                    // 如果上层就是 null 了，还得看有没有儿子，没有那就结束
                    return;
                } else {
                    // 否则还得 new 一个，委派给 ObjectWrapper.instantiatePropertyValue
                    metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
                }
            }
            // 递归调用setValue
            metaValue.setValue(prop.getChildren(), value);
        } else {
            // 到了最后一层了，所以委派给 ObjectWrapper.set
            objectWrapper.set(prop, value);
        }
    }

    /**
     * 为属性生成元对象
     *
     * @param name 属性名
     * @return 元对象
     */
    public MetaObject metaObjectForProperty(String name) {
        // 实际是递归调用
        Object value = getValue(name);
        return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }

    /**
     * 是否是集合
     *
     * @return 结果
     */
    public boolean isCollection() {
        return objectWrapper.isCollection();
    }

    /**
     * 添加属性
     *
     * @param element 属性
     */
    public void add(Object element) {
        objectWrapper.add(element);
    }

    /**
     * 添加属性
     *
     * @param list 属性列表
     * @param <E>  类型
     */
    public <E> void addAll(List<E> list) {
        objectWrapper.addAll(list);
    }
}
