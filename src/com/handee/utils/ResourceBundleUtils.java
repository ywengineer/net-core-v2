/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * ResourceBundleUtils.java
 * 
 * 2013 2013-5-17 下午1:33:04
 * 
 *******************************************************************/
package com.handee.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class ResourceBundleUtils {
	/**
	 * 
	 * @param resource
	 * @param key
	 * @return
	 */
	public static final Integer getInteger(ResourceBundle resource, String key) {
		if (resource == null || !resource.keySet().contains(key)) {
			return 0;
		}
		return MathUtils.getInteger(resource.getString(key));
	}

	/**
	 * 
	 * @param resource
	 * @param key
	 * @return
	 */
	public static final String getString(ResourceBundle resource, String key) {
		if (resource == null || !resource.keySet().contains(key)) {
			return null;
		}
		return resource.getString(key);
	}

	/**
	 * 
	 * @param resource
	 * @param key
	 * @return
	 */
	public static final boolean getBoolean(ResourceBundle resource, String key) {
		if (resource == null || !resource.keySet().contains(key)) {
			return false;
		}
		return MathUtils.getBoolean(resource.getString(key));
	}

	/**
	 * 
	 * @param resource
	 * @param key
	 * @return
	 */
	public static final <T> Class<T> getClass(ResourceBundle resource, String key) {
		if (resource == null || !resource.keySet().contains(key)) {
			return null;
		}
		return ClassUtils.getClass(resource.getString(key));
	}

	/**
	 * @param resource
	 * @param key
	 * @param initargs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T getClassInstance(ResourceBundle resource, String key, Object... initargs) {
		return (T) ClassUtils.getClassInstance(getClass(resource, key), initargs);
	}

	/**
	 * 把一个Properties文件转换成Map
	 * 
	 * @param resource
	 *            Properties文件的ResourceBundle
	 * @return java.util.Map
	 */
	public static final Map<String, Object> toMap(ResourceBundle resource) {
		Map<String, Object> map = new HashMap<>();
		if (resource == null) {
			return map;
		}
		for (String key : resource.keySet()) {
			map.put(key, resource.getObject(key));
		}
		return Collections.unmodifiableMap(map);
	}
}
