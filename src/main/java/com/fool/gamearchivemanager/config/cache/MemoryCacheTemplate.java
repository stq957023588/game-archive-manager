package com.fool.gamearchivemanager.config.cache;

import org.apache.ibatis.cache.Cache;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryCacheTemplate implements CacheTemplate {

    private final ConcurrentHashMap<Object, Object> cache;

    public MemoryCacheTemplate() {
        cache = new ConcurrentHashMap<>();
    }


    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return cache.get(key);
    }

    @Override
    public boolean delete(Object key) {
        cache.remove(key);
        return true;
    }
}
