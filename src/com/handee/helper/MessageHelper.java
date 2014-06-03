/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * CommandHelper.java
 *
 * 2013 2013-5-17 下午3:04:22
 *
 *******************************************************************/
package com.handee.helper;

import com.handee.ContextProperties;
import com.handee.annotations.MessageJob;
import com.handee.event.JobProcessEvent;
import com.handee.event.listener.EventListener;
import com.handee.job.JobProcessCallback;
import com.handee.job.RunnableNetJob;
import com.handee.utils.ClassUtils;
import com.handee.utils.JobQueueOB;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * class description
 *
 * @author Mark
 */
public class MessageHelper {
    private static final Logger logger = Logger.getLogger(MessageHelper.class);
    private static final MessageHelper instance = new MessageHelper();

    private Map<String, Reference<MessageJob, Class<RunnableNetJob<?>>>> command = new HashMap<>();

    private MessageHelper() {

    }

    public static MessageHelper getInstance() {
        return instance;
    }

    /**
     * 命令集合。
     *
     * @param commandMap 命令映射集合
     */
    public void setCommand(Map<String, Reference<MessageJob, Class<RunnableNetJob<?>>>> commandMap) {
        command.clear();
        command.putAll(commandMap);
    }

    /**
     * 处理一个Job
     *
     * @param <K>    Job执行返回结果类型
     * @param code   job的编码
     * @param params 实例化Job时的构造参数。
     */
    public <K> void processJob(String code, final JobProcessCallback<K> callback, Object... params) {
        // new message process job
        RunnableNetJob<?> job;
        // find config for message process job
        Reference<MessageJob, Class<RunnableNetJob<?>>> reference = command.get(code);
        // queue index
        int queue = -1;
        // error code.
        if (reference == null) {
            logger.error("error code :: [" + code + "]");
            job = ClassUtils.getClassInstance(ContextProperties.PROCESS_FOR_ERROR_MESSAGE, params);
            // return;
        } else {
            job = ClassUtils.getClassInstance(reference.value, params);
            // add job to queue.
            queue = reference.key.queue();
        }
        // 如果Job初始化失败
        if (job == null) {
            // 如果Job初始化失败
            logger.error("create message job error, code :: [" + code + "]");
            return;
        }

        if (queue < 0) {
            queue = Math.abs(job.hashCode() % JobQueueOB.getInstance().getOBSize());
        }

        if (callback != null) {
            job.addListener(JobProcessEvent.EVENT_AFTER_JOB_PROCESS, new EventListener<JobProcessEvent>() {
                @Override
                public void on(JobProcessEvent event) {
                    if (event.getType().equals(JobProcessEvent.EVENT_AFTER_JOB_PROCESS)) {
                        @SuppressWarnings("unchecked")
                        RunnableNetJob<K> executedJob = (RunnableNetJob<K>) event.emitter();
                        callback.call(executedJob.get());
                    }
                }
            });
        }

        final TestTime runningTime = new TestTime();

        // TODO：for test
        job.addListener(JobProcessEvent.EVENT_BEFORE_JOB_PROCESS, new EventListener<JobProcessEvent>() {
            @Override
            public void on(JobProcessEvent event) {
                if (event.getType().equals(JobProcessEvent.EVENT_BEFORE_JOB_PROCESS)) {
                    runningTime.runningTime = System.currentTimeMillis();
                }
            }
        });

        job.addListener(JobProcessEvent.EVENT_AFTER_JOB_PROCESS, new EventListener<JobProcessEvent>() {

            @Override
            public void on(JobProcessEvent event) {
                if (event.getType().equals(JobProcessEvent.EVENT_AFTER_JOB_PROCESS)) {
                    long runTime = System.currentTimeMillis() - runningTime.runningTime;
                    logger.warn(event.emitter().getClass().getName() + " run time :: " + runTime + " ms");
                }
            }
        });

        JobQueueOB.getInstance().addJob(queue, job);
    }

    public static class TestTime {
        public long runningTime;
    }
}
