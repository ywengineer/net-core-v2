/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 *
 * HandeeScheduler.java
 *
 * 2013 2013-5-22 下午3:31:12
 *
 *******************************************************************/
package com.handee.scheduler;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * 任务调度管理器。
 * 
 * @author Mark
 * 
 */
public class ScheduleManager {
	private static final Logger logger = Logger.getLogger(ScheduleManager.class);

	/**
	 * 获取平台任务调度器。
	 * 
	 * 默认任务调度器。
	 * 
	 * @return
	 */
	public static final Scheduler getPlatformScheduler() {
		try {
			StdSchedulerFactory sf = new StdSchedulerFactory();
			sf.initialize("handee-quartz.properties");
			return sf.getScheduler();
		} catch (SchedulerException e) {
			logger.error("HandeeScheduler Initialize failure :: ", e);
		}
		return null;
	}

	/**
	 * 启动任务调度器。
	 * 
	 * @param scheduler
	 *            调度器。
	 * @return 启动的任务调度器
	 */
	public static final Scheduler start(Scheduler scheduler) {
		try {
			if (scheduler != null && (!scheduler.isStarted() || scheduler.isInStandbyMode())) {
				scheduler.start();
				com.handee.utils.SystemUtils.printSection("Scheduler[" + scheduler.getSchedulerName() + "] service started.");
			}
		} catch (SchedulerException e) {
			logger.error("Scheduler start error :: ", e);
		}
		return scheduler;
	}

	/**
	 * 停止任务调度器。
	 * 
	 * @param scheduler
	 *            任务调度器
	 */
	public static final void shutdownScheduler(Scheduler scheduler) {
		try {
			if (scheduler != null && !scheduler.isShutdown()) {
				scheduler.shutdown();
				com.handee.utils.SystemUtils.printSection("Scheduler[" + scheduler.getSchedulerName() + "] service was shutdown.");
			}
		} catch (SchedulerException e) {
			logger.error("Scheduler shutdown error :: ", e);
		}
	}
}
