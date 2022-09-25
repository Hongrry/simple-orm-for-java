package cn.hruit.orm.cache;


/**
 * 空缓存键
 *
 * @author HONGRRY
 */
public final class NullCacheKey extends CacheKey {

    private static final long serialVersionUID = 3704229911977019465L;

    public NullCacheKey() {
        super();
    }

    @Override
    public void update(Object object) {
        throw new RuntimeException("Not allowed to update a NullCacheKey instance.");
    }

    @Override
    public void updateAll(Object[] objects) {
        throw new RuntimeException("Not allowed to update a NullCacheKey instance.");
    }
}
