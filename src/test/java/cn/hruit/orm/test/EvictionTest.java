package cn.hruit.orm.test;

import cn.hruit.orm.cache.Cache;
import cn.hruit.orm.cache.impl.PerpetualCache;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author HONGRRY
 * @description
 * @date 2022/09/28 09:39
 **/
public class EvictionTest {
    @Test
    public void testLruCacheEviction() {
        LruCache cache = new LruCache(2);
        cache.putObject("1", 1);
        cache.putObject("2", 2);
        cache.putObject("3", 3);
        // 淘汰1
        System.out.println(cache);
    }

    @Test
    public void testLruCacheEvictionWithUse() {
        LruCache cache = new LruCache(2);
        cache.putObject("1", 1);
        cache.putObject("2", 2);
        cache.getObject("1");
        cache.putObject("3", 3);
        // 淘汰2
        System.out.println(cache);
    }
}

class LruCache implements Cache {
    private final Cache delegate;
    private Map<Object, Object> keyMap;
    private Object eldestKey;

    public LruCache(final int size) {
        delegate = new PerpetualCache("");
        setSize(size);
    }

    private void setSize(final int size) {
        keyMap = new LinkedHashMap<Object, Object>(size, .75f, true) {
            private static final long serialVersionUID = 4267176411845948333L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                boolean tooBig = size() > size;
                if (tooBig) {
                    eldestKey = eldest.getKey();
                }
                return tooBig;
            }
        };
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        delegate.putObject(key, value);
        cycleKeyList(key);
    }

    @Override
    public Object getObject(Object key) {
        keyMap.get(key);
        return delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        keyMap.remove(key);
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        keyMap.clear();
        delegate.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    private void cycleKeyList(Object key) {
        keyMap.put(key, key);
        if (eldestKey != null) {
            delegate.removeObject(eldestKey);
            eldestKey = null;
        }
    }

    @Override
    public String toString() {
        Set<Object> set = keyMap.keySet();
        return String.valueOf(set);
    }
}