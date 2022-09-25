package cn.hruit.orm.reflection;


import cn.hruit.orm.reflection.factory.DefaultObjectFactory;
import cn.hruit.orm.reflection.factory.ObjectFactory;
import cn.hruit.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import cn.hruit.orm.reflection.wrapper.ObjectWrapperFactory;

/**
 * 一些系统级别的元对象
 *
 * @author HONGRRY
 */
public class SystemMetaObject {

    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

    private SystemMetaObject() {
        // Prevent Instantiation of Static Class
    }

    /**
     * 空对象
     */
    private static class NullObject {
    }

    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
    }

}
