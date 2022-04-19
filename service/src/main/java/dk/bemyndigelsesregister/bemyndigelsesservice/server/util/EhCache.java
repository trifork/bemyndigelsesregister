package dk.bemyndigelsesregister.bemyndigelsesservice.server.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * Simple generics wrapper for EhCache
 */
public class EhCache<KEY, ELEMENT> {

    private Cache cache;

    public EhCache(String name) {
        this(name, 500);
    }

    public EhCache(String name, int timeToLiveSeconds) {
        CacheManager cm = CacheManager.getInstance();
        cache = new Cache(new CacheConfiguration(name, 1000) // configure as memory cache
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
                .eternal(false)
                .timeToLiveSeconds(timeToLiveSeconds)
                .diskExpiryThreadIntervalSeconds(0)
                .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE)));
        cm.addCache(cache);
    }

    public void put(KEY key, ELEMENT element) {
        cache.put(new Element(key, element));
    }

    public ELEMENT get(KEY key) {
        Element e = cache.get(key);
        return e != null ? (ELEMENT) e.getObjectValue() : null;
    }

    public boolean contains(KEY key) {
        return cache.isKeyInCache(key);
    }

    public boolean remove(KEY key) {
        return cache.remove(key);
    }

    public void clear() {
        cache.removeAll();
    }
}
