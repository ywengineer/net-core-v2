/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 *
 * GlobalEventEmitter.java
 *
 * 2013 2013-5-17 下午2:43:22
 *
 *******************************************************************/
package com.handee.event.emitter;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class GlobalEventEmitter extends EventEmitter {
	private static final GlobalEventEmitter emitter;

	static {
		emitter = new GlobalEventEmitter();
	}

	public static final GlobalEventEmitter getInstance() {
		return emitter;
	}

	private GlobalEventEmitter() {

	}
}
