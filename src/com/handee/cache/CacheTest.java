/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * com.handee.cache.CacheTest.java
 * 
 * 2013-6-21 下午3:47:51
 * 
 *******************************************************************/
package com.handee.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class CacheTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CacheManager manager = CacheManager.create();
		manager.setCacheManagerEventListener(new CacheManagerEventListener() {

			@Override
			public void notifyCacheRemoved(String cacheName) {
				System.out.println("cache[" + cacheName + "] removed");
			}

			@Override
			public void notifyCacheAdded(String cacheName) {
				System.out.println("cache[" + cacheName + "] added");
			}

			@Override
			public void init() throws CacheException {

			}

			@Override
			public Status getStatus() {
				return null;
			}

			@Override
			public void dispose() throws CacheException {

			}
		});
	}

}
