package org.example.Common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate,
                        StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public <T> T queryWithProtect(String key, Class<T> type,
                                  Supplier<T> dbSupplier,
                                  long ttl, TimeUnit unit) {
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            return parseCachedValue(cached, type);
        }

        String lockKey = "lock:" + key;
        String lockVal = UUID.randomUUID().toString();
        Boolean locked = false;

        try {
            locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockVal, 10, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(locked)) {
                cached = stringRedisTemplate.opsForValue().get(key);
                if (cached != null) {
                    return parseCachedValue(cached, type);
                }
                T data = dbSupplier.get();
                if (data != null) {
                    setCache(key, data, ttl, unit);
                } else {
                    setNullCache(key);
                }
                return data;
            } else {
                Thread.sleep(100);
                return queryWithProtect(key, type, dbSupplier, ttl, unit);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            if (Boolean.TRUE.equals(locked)
                    && lockVal.equals(redisTemplate.opsForValue().get(lockKey))) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    private <T> T parseCachedValue(String cached, Class<T> type) {
        if (CacheConst.NULL_PLACEHOLDER.equals(cached)) {
            return null;
        }
        return JSON.parseObject(cached, type);
    }

    private void setCache(String key, Object data, long ttl, TimeUnit unit) {
        String json = JSON.toJSONString(data, SerializerFeature.PrettyFormat);
        long offset = ThreadLocalRandom.current().nextLong(0, ttl / 5);
        stringRedisTemplate.opsForValue().set(key, json, ttl + offset, unit);
    }

    private void setNullCache(String key) {
        stringRedisTemplate.opsForValue().set(key, CacheConst.NULL_PLACEHOLDER, 60, TimeUnit.SECONDS);
    }

    public <T> List<T> queryListWithProtect(String key, Class<T> itemType,
                                             Supplier<List<T>> dbSupplier,
                                             long ttl, TimeUnit unit) {
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            if (CacheConst.LIST_EMPTY.equals(cached)) return new ArrayList<>();
            return JSON.parseArray(cached, itemType);
        }

        String lockKey = "lock:" + key;
        String lockVal = UUID.randomUUID().toString();
        Boolean locked = false;

        try {
            locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockVal, 10, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(locked)) {
                cached = stringRedisTemplate.opsForValue().get(key);
                if (cached != null) {
                    if (CacheConst.LIST_EMPTY.equals(cached)) return new ArrayList<>();
                    return JSON.parseArray(cached, itemType);
                }
                List<T> data = dbSupplier.get();
                if (data != null && !data.isEmpty()) {
                    setCache(key, data, ttl, unit);
                } else {
                    setListEmptyCache(key);
                }
                return data != null ? data : new ArrayList<>();
            } else {
                Thread.sleep(100);
                return queryListWithProtect(key, itemType, dbSupplier, ttl, unit);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            if (Boolean.TRUE.equals(locked)
                    && lockVal.equals(redisTemplate.opsForValue().get(lockKey))) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    private void setListEmptyCache(String key) {
        stringRedisTemplate.opsForValue().set(key, CacheConst.LIST_EMPTY, 60, TimeUnit.SECONDS);
    }

    private static class CacheConst {
        static final String NULL_PLACEHOLDER = "null_val";
        static final String LIST_EMPTY = "empty_list";
    }
}