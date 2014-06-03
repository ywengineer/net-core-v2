/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * com.handee.cache.CacheManager.java
 * 
 * 2013-6-21 下午5:21:33
 * 
 *******************************************************************/
package com.handee.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class HandeeCacheManager implements CacheManagerEventListener {
	private static final Logger logger = Logger.getLogger(HandeeCacheManager.class);
	private static final HandeeCacheManager instance = new HandeeCacheManager();
	private CacheManager manager;
	Map<String, HandeeCache<?>> caches = new ConcurrentHashMap<>();

	private HandeeCacheManager() {
		manager = CacheManager.getInstance();
		manager.setCacheManagerEventListener(this);
	}

	public static HandeeCacheManager getInstance() {
		return instance;
	}

	public void addCache(HandeeCache<?> cache) {
		manager.addCache(cache.getStore());
		caches.put(cache.getName(), cache);
	}

	public HandeeCache<?> getCache(String name) {
		return caches.get(name);
	}

	public void remove(String name) {
		caches.remove(name);
		manager.removeCache(name);
	}

	public void clear() {
		caches.clear();
        manager.removeAllCaches();
	}
	
	public void shutdown(){
		clear();
		manager.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.ehcache.event.CacheManagerEventListener#init()
	 */
	@Override
	public void init() throws CacheException {
		logger.info("Handee cache manager initialized.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.ehcache.event.CacheManagerEventListener#getStatus()
	 */
	@Override
	public Status getStatus() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.ehcache.event.CacheManagerEventListener#dispose()
	 */
	@Override
	public void dispose() throws CacheException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.ehcache.event.CacheManagerEventListener#notifyCacheAdded(java.
	 * lang.String)
	 */
	@Override
	public void notifyCacheAdded(String cacheName) {
		logger.info("Handee cache[" + cacheName + "] added.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.ehcache.event.CacheManagerEventListener#notifyCacheRemoved(java
	 * .lang.String)
	 */
	@Override
	public void notifyCacheRemoved(String cacheName) {
		logger.info("Handee cache[" + cacheName + "] removed.");
	}
}
