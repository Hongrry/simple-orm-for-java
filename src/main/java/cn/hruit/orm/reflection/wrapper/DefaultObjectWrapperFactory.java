package cn.hruit.orm.reflection.wrapper;

import cn.hruit.orm.reflection.MetaObject;

/**
 * @author HONGRRY
 * @description 默认对象包装工厂
 * @date 2022/09/01 17:36
 **/
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {
    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new RuntimeException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }
}
