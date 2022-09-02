package cn.hruit.mybatis.reflection.wrapper;

import cn.hruit.mybatis.reflection.MetaClass;
import cn.hruit.mybatis.reflection.MetaObject;
import cn.hruit.mybatis.reflection.SystemMetaObject;
import cn.hruit.mybatis.reflection.factory.ObjectFactory;
import cn.hruit.mybatis.reflection.invoker.Invoker;
import cn.hruit.mybatis.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * @author HONGRRY
 * @description Bean 包装器
 * @date 2022/09/02 09:41
 **/
public class BeanWrapper extends BaseWrapper {
    private final Object originalBean;
    private final MetaClass metaClass;

    public BeanWrapper(MetaObject metaObject, Object originalBean) {
        super(metaObject);
        this.originalBean = originalBean;
        this.metaClass = MetaClass.forClass(originalBean.getClass(), metaObject.getReflectorFactory());
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, originalBean);
            return getCollectionValue(prop, collection);
        }
        return getBeanProperty(prop, originalBean);
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        // 如果有index,说明是集合，那就要解析集合,调用的是BaseWrapper.resolveCollection 和 setCollectionValue
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, originalBean);
            setCollectionValue(prop, collection, value);
        } else {
            // 否则，setBeanProperty
            setBeanProperty(prop, originalBean, value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return metaClass.findProperty(name, useCamelCaseMapping);
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getSetterType(name);
            } else {
                return metaValue.getSetterType(prop.getChildren());
            }
        } else {
            return metaClass.getSetterType(name);
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getGetterType(name);
            } else {
                return metaValue.getGetterType(prop.getChildren());
            }
        } else {
            return metaClass.getGetterType(name);
        }
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasSetter(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasSetter(name);
                } else {
                    return metaValue.hasSetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasSetter(name);
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasGetter(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasGetter(name);
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasGetter(name);
        }
    }


    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        MetaObject metaValue;
        Class<?> type = getSetterType(prop.getName());
        try {
            Object newObject = objectFactory.create(type);
            metaValue = MetaObject.forObject(newObject, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
            set(prop, newObject);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set value of property '" + name + "' because '" + name + "' is null and cannot be instantiated on instance of " + type.getName() + ". Cause:" + e.toString(), e);
        }
        return metaValue;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> list) {
        throw new UnsupportedOperationException();
    }

    private Object getBeanProperty(PropertyTokenizer prop, Object object) {
        try {
            // 得到getter方法，然后调用
            Invoker method = metaClass.getGetInvoker(prop.getName());
            return method.invoke(object, NO_ARGUMENTS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException("Could not get property '" + prop.getName() + "' from " + object.getClass() + ".  Cause: " + t.toString(), t);
        }
    }

    private void setBeanProperty(PropertyTokenizer prop, Object object, Object value) {
        try {
            // 得到setter方法，然后调用
            Invoker method = metaClass.getSetInvoker(prop.getName());
            Object[] params = {value};
            method.invoke(object, params);
        } catch (Throwable t) {
            throw new RuntimeException("Could not set property '" + prop.getName() + "' of '" + object.getClass() + "' with value '" + value + "' Cause: " + t.toString(), t);
        }
    }
}
