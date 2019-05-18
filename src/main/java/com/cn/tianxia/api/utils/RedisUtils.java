package com.cn.tianxia.api.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @ClassName RedisUtils
 * @Description redis工具类
 * @author Hardy
 * @Date 2019年5月3日 上午10:59:09
 * @version 1.0.0
 */
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;
    @Resource(name="redisTemplate")
    private HashOperations<String, String, Object> hashOperations;
    @Resource(name="redisTemplate")
    private ListOperations<String, Object> listOperations;
    @Resource(name="redisTemplate")
    private SetOperations<String, Object> setOperations;
    @Resource(name="redisTemplate")
    private ZSetOperations<String, Object> zSetOperations;
   
    /**  默认过期时长，单位：秒 (24小时)*/
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    
    /**  不设置过期时长 */
    public final static long NOT_EXPIRE = -1;

    public void set(String key, Object value, long expire){
        valueOperations.set(key, toJson(value));
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value){
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }
    
    /**
     * 
     * @Description put一个map
     * @param key
     * @param data
     */
    public void hset(String key,Map<String,Object> map){
        hset(key, map, DEFAULT_EXPIRE);
    }
    
    public void hset(String key,Map<String,Object> map,long expire){
        hashOperations.putAll(key, map);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }
    
    public void hmset(String key,Map<String,String> map){
        hmset(key, map, DEFAULT_EXPIRE);
    }
    
    public void hmset(String key,Map<String,String> map,long expire){
        hashOperations.putAll(key, map);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }
    
    public String hget(String key, String field) {
        String value = (String) hashOperations.get(key, field);
        return value;
    }

    /**
     * 
     * @Description zset
     * @param key
     * @param value
     */
    public void sadd(String key,String value,long expire){
        setOperations.add(key, value);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }
    
    public boolean isMember(String key,Object o){
        return setOperations.isMember(key, o);
    }
    
    public void sadd(String key,String value){
        sadd(key,value);
    }
    
    
    public void srem(String key,String value){
        setOperations.remove(key, value);
    }
    
    public Set<Object> smembers(String key){
        return setOperations.members(key);
    }
    
    
    /**
     * hgetAll
     *
     * @param key
     *
     * @return
     */
    public Map<String, Object> hgetAll(String key) {
        return hashOperations.entries(key);
    }
    
    
    /**
     * Object转成JSON数据
     */
    private String toJson(Object object){
        if(object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String){
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }
}
