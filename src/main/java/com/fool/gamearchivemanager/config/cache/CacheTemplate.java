package com.fool.gamearchivemanager.config.cache;

public interface CacheTemplate {

    void put(Object key, Object value);

    Object get(Object key);

    boolean delete(Object key);

}
