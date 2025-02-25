package com.fool.gamearchivemanager.config.cache;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisCacheTemplate implements CacheTemplate {
    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisCacheTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void put(Object key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object get(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean delete(Object key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
