/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * JobProcessEvent.java
 *
 * 2013 2013-5-22 下午3:08:12
 *
 *******************************************************************/
package com.handee.event;


/**
 * <p>
 * NetJob执行相关事件
 * </p>
 *
 * @author Mark
 */
public class JobProcessEvent extends Event {
    /**
     * job执行完成之后
     */
    public static final String EVENT_AFTER_JOB_PROCESS = "event_after_job_process";
    /**
     * job执行之前
     */
    public static final String EVENT_BEFORE_JOB_PROCESS = "event_before_job_process";

    /**
     * NetJob执行相关事件
     *
     * @param type 事件类型
     */
    public JobProcessEvent(String type) {
        super(type);
    }
}
