package cn.hruit.mybatis.reflection.wrapper;

import cn.hruit.mybatis.reflection.MetaObject;
import cn.hruit.mybatis.reflection.SystemMetaObject;
import cn.hruit.mybatis.reflection.factory.ObjectFactory;
import cn.hruit.mybatis.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HONGRRY
 * @description Map 包装器
 * @date 2022/09/01 17:41
 **/
public class MapWrapper extends BaseWrapper {

    /**
     * 原Map
     */
    private Map<String, Object> originalMap;

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject);
        this.originalMap = map;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        // 如果有index, 需要在集合中获取
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, originalMap);
            return getCollectionValue(prop, collection);
        } else {
            return originalMap.get(prop.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, value);
            setCollectionValue(prop, collection, value);
        } else {
            originalMap.put(prop.getName(), value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    @Override
    public String[] getGetterNames() {
        return originalMap.keySet().toArray(new String[0]);
    }

    @Override
    public String[] getSetterNames() {
        return originalMap.keySet().toArray(new String[0]);
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getName());
            if (null == metaValue) {
                return Object.class;
            }
            return metaValue.getSetterType(prop.getChildren());
        } else {
            Object value = originalMap.get(prop.getName());
            if (null != value) {
                return value.getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getGetterType(prop.getChildren());
            }
        } else {
            Object value = originalMap.get(name);
            if (value != null) {
                return value.getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (originalMap.containsKey(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return originalMap.containsKey(prop.getName());
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<>();
        set(prop, map);
        return MetaObject.forObject(map, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
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
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();

    }
}
