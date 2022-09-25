package cn.hruit.orm.builder;

import cn.hruit.orm.cache.Cache;

/**
 * @author HONGRRY
 * @description
 * @date 2022/09/25 21:44
 **/
public class CacheRefResolver {
    private final MapperBuilderAssistant assistant;
    private final String cacheRefNamespace;

    public CacheRefResolver(MapperBuilderAssistant assistant, String cacheRefNamespace) {
        this.assistant = assistant;
        this.cacheRefNamespace = cacheRefNamespace;
    }

    public Cache resolveCacheRef() {
        return assistant.useCacheRef(cacheRefNamespace);
    }
}
