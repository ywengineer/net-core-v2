/**
 * 
 */
package com.handee.utils;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author wang
 * 
 */
public class HandlerRegistry<K, V> {

	protected static final Logger _log = Logger.getLogger(HandlerRegistry.class);

	private final Map<K, V> _map;

	public HandlerRegistry(boolean sorted) {
		_map = sorted ? new TreeMap<K, V>() : new HashMap<K, V>();
	}

	public HandlerRegistry() {
		this(false);
	}

	public K standardizeKey(K key) {
		return key;
	}

	public final void register(K key, V handler) {
		key = standardizeKey(key);
		V old = _map.put(key, handler);

		if (old != null && !old.equals(handler))
			_log.warn(getClass().getSimpleName() + ": Replaced type(" + key + "), " + old + " -> " + handler + ".");
	}

	@SafeVarargs
	public final void registerAll(V handler, K... keys) {
		for (K key : keys)
			register(key, handler);
	}

	public final V get(K key) {
		key = standardizeKey(key);
		return _map.get(key);
	}

	public final int size() {
		return _map.size();
	}

	public final Map<K, V> getHandlers() {
		return Collections.unmodifiableMap(_map);
	}
}
