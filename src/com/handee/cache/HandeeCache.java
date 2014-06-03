/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * com.handee.cache.HandeeCache.java
 *
 * 2013-6-21 下午5:28:48
 *
 *******************************************************************/
package com.handee.cache;

import com.handee.interfaces.IBufferable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 数据缓存区。
 *
 * @author Mark
 */
public final class HandeeCache<T extends IBufferable> {
    private static final Logger logger = Logger.getLogger(HandeeCache.class);
    private Cache cache;

    /**
     * @param name                缓存名称
     * @param maxElementsInMemory 内存中存在的最大元素数量
     * @param overflowToDisk      如果走过内存数量上限，是否磁盘可写
     * @param eternal             是否允许外部缓存
     * @param timeToLiveSeconds   存活时间
     * @param timeToIdleSeconds   空闲时间
     */
    public HandeeCache(String name, int maxElementsInMemory, boolean overflowToDisk, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds) {
        cache = new Cache(name, maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
        HandeeCacheManager.getInstance().addCache(this);
    }

    /**
     * 将一个可缓存对象添加到缓存。
     * <p/>
     * 如果key不存在，新建缓存节点并添加至缓存。
     * <p/>
     * 如果key存在，更新缓存节点。
     *
     * @param key   缓存键
     * @param value 可缓存对象
     * @return 缓存成功对象
     */
    public synchronized T put(Object key, T value) {
        // log
        logger.info(cache.get(key) != null ? "update element in cache" : "create a new cache element");
        // put in cache.
        cache.put(new Element(key, value));
        // put success.
        return value;
    }

    public boolean remove(Object key) {
        return cache.remove(key);
    }

    public void removeAll() {
        cache.removeAll();
    }

    @SuppressWarnings("unchecked")
    public T get(Object key) {
        Element element = cache.get(key);
        return element == null ? null : (T) element.getObjectValue();
    }

    @SuppressWarnings("unchecked")
    public Map<Object, T> getAll(Collection<Object> keys) {
        Map<Object, Element> all = cache.getAll(keys);
        Map<Object, T> reVal = new HashMap<>();
        for (Entry<Object, Element> element : all.entrySet()) {
            reVal.put(element.getKey(), (T) element.getValue().getObjectValue());
        }
        all.clear();
        return reVal;
    }

    public String getName() {
        return cache.getName();
    }

    @SuppressWarnings("unchecked")
    public List<Object> keys() {
        return cache.getKeys();
    }

    Cache getStore() {
        return this.cache;
    }
}
