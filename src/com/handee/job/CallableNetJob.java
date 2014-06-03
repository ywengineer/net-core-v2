/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * com.handee.job.CallableNetJob.java
 * 
 * 2013-6-25 上午11:16:51
 * 
 *******************************************************************/
package com.handee.job;

import com.handee.event.JobProcessEvent;
import com.handee.event.emitter.EventEmitter;

import java.util.concurrent.Callable;

/**
 * 
 * 工作类，需要在JobQueue里执行。
 * 
 * 多用于同步操作。
 * 
 * @author Mark
 * @param <V>
 *            返回值类型
 * 
 */
public abstract class CallableNetJob<V> extends EventEmitter implements Callable<V> {

	/**
	 * 具体工作执行
	 */
	protected abstract V process();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public final V call() throws Exception {
		emmit(new JobProcessEvent(JobProcessEvent.EVENT_BEFORE_JOB_PROCESS));
		V returnValue = process();
		emmit(new JobProcessEvent(JobProcessEvent.EVENT_AFTER_JOB_PROCESS));
		removeAllListeners();
		return returnValue;
	}
}
