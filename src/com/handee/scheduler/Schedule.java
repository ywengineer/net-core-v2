/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * SchedulerJob.java
 * 
 * 2013 2013-5-22 下午3:21:14
 * 
 *******************************************************************/
package com.handee.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 定时工作类注解.
 * 
 * <p>
 * 工作类必须实现接口org.quartz.Job
 * </p>
 * 
 * <p>
 * Cron表达式详见：
 * </p>
 * <p>
 * <a>http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/
 * crontrigger </a>
 * </p>
 * 
 * @author Mark
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface Schedule {
	/**
	 * cron表达式.
	 * 
	 * @return cron expression
	 */
	public String cronExpression();
}
