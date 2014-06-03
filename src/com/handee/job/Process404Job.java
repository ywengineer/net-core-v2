/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * ProcessErrorCodeJob.java
 * 
 * 2013 2013-6-13 下午5:16:04
 * 
 *******************************************************************/
package com.handee.job;

import com.handee.net.message.NetMessage;
import com.handee.utils.SystemUtils;

/**
 * 
 * 错误消息处理。
 * 
 * @author Mark
 * 
 */
public class Process404Job<T extends NetMessage> extends RunnableNetJob<T> {
	private T message;

	public Process404Job(T msg) {
		this.message = msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.network.NetJob#process()
	 */
	@Override
	protected T process() {
		SystemUtils.printSection("404 in " + this.message.toString());
		return null;
	}
}
