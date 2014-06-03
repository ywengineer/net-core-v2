/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * ScheduleJob.java
 *
 * 2013 2013-5-30 下午4:35:52
 *
 *******************************************************************/
package com.handee.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 定时任务。
 *
 * @author Mark
 */
public abstract class ScheduleJob implements Job {

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        this.execute();
    }

    /**
     * 定时任务执行逻辑
     */
    protected abstract void execute();
}
