package cn.hruit.orm.reflection.wrapper;


import cn.hruit.orm.reflection.MetaObject;
import cn.hruit.orm.reflection.factory.ObjectFactory;
import cn.hruit.orm.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * 对象包装器
 *
 * @author HONGRRY
 */
public interface ObjectWrapper {
    /**
     * 获取属性
     *
     * @param prop 属性
     * @return 获取结果
     */
    Object get(PropertyTokenizer prop);

    /**
     * 设置属性
     *
     * @param prop  属性
     * @param value 参数
     */
    void set(PropertyTokenizer prop, Object value);

    /**
     * 查找属性
     *
     * @param name                属性名
     * @param useCamelCaseMapping 是否使用驼峰命名
     * @return 结果
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 取得getter的名字列表
     *
     * @return 列表
     */
    String[] getGetterNames();

    /**
     * 取得setter的名字列表
     *
     * @return 列表
     */
    String[] getSetterNames();

    /**
     * 取得setter的类型
     *
     * @param name 属性名
     * @return 类型
     */
    Class<?> getSetterType(String name);

    /**
     * 取得getter的类型
     *
     * @param name 属性名
     * @return 类型
     */
    Class<?> getGetterType(String name);

    /**
     * 是否有指定的setter
     *
     * @param name 属性名
     * @return 是否存在
     */
    boolean hasSetter(String name);

    /**
     * 是否有指定的getter
     *
     * @param name 属性名
     * @return 是否存在
     */
    boolean hasGetter(String name);

    /**
     * 实例化属性
     *
     * @param name          属性名
     * @param prop          属性prop
     * @param objectFactory 对象工厂
     * @return 元对象
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    /**
     * 是否是集合
     *
     * @return 是否为集合类型
     */
    boolean isCollection();

    /**
     * 添加属性
     *
     * @param element 元素
     */
    void add(Object element);

    /**
     * 添加多个属性
     *
     * @param element 元素集合额
     * @param <E>     类型
     */
    <E> void addAll(List<E> element);

}
