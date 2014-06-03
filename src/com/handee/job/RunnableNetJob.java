package com.handee.job;

import com.handee.event.JobProcessEvent;
import com.handee.event.emitter.EventEmitter;

/**
 * 工作类，需要在JobQueue里执行。
 * 
 * 多用于异步操作。
 * 
 * @author Mark
 * 
 */
public abstract class RunnableNetJob<T> extends EventEmitter implements Runnable {

	/**
	 * 具体工作执行
	 */
	protected abstract T process();

	private T returnValue;

	/**
	 * 不能重写
	 */
	public final void run() {
		emmit(new JobProcessEvent(JobProcessEvent.EVENT_BEFORE_JOB_PROCESS));
		returnValue = process();
		emmit(new JobProcessEvent(JobProcessEvent.EVENT_AFTER_JOB_PROCESS));
		removeAllListeners();
	}

	public final T get() {
		return returnValue;
	}
}
