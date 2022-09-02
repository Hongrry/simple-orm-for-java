package cn.hruit.mybatis.reflection.wrapper;

import cn.hruit.mybatis.reflection.MetaObject;

/**
 * 对象包装工厂
 *
 * @author HONGRRY
 */
public interface ObjectWrapperFactory {

    /**
     * 判断有没有包装器
     *
     * @param object 对象
     * @return 结果
     */
    boolean hasWrapperFor(Object object);

    /**
     * 得到包装器
     *
     * @param metaObject 元对象
     * @param object     原对象
     * @return 对象包装器
     */
    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}
