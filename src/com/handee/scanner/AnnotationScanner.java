/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * AScanner.java
 *
 * 2013 2013-5-30 下午3:16:01
 *
 *******************************************************************/
package com.handee.scanner;

import com.github.jmkgreen.morphia.utils.ReflectionUtils;
import com.handee.annotations.MessageJob;
import com.handee.helper.Reference;
import com.handee.job.RunnableNetJob;
import com.handee.scheduler.Schedule;
import org.apache.log4j.Logger;
import org.quartz.Job;

import java.util.*;

/**
 * class description
 *
 * @author Mark
 */
public final class AnnotationScanner {
    private static final Logger logger = Logger.getLogger(AnnotationScanner.class);

    /**
     * 扫描所有{@link com.handee.scheduler.Schedule}注解的类。
     * <p/>
     * 该类必须实现接口org.quartz.Job
     *
     * @param classes 所有类。
     * @return 具有Schedule注解的所有类列表
     */
    @SuppressWarnings("unchecked")
    public static List<Reference<Schedule, Class<? extends Job>>> scanScheduleJob(Set<Class<?>> classes) {
        List<Reference<Schedule, Class<? extends Job>>> maps = new ArrayList<>();
        if (classes == null || classes.size() < 1) {
            return maps;
        }
        for (Class<?> cls : classes) {
            if (cls == null || !ReflectionUtils.implementsInterface(cls, Job.class)) {
                continue;
            }
            Schedule job = cls.getAnnotation(Schedule.class);
            if (job == null) {
                continue;
            }
            maps.add(new Reference<Schedule, Class<? extends Job>>(job, (Class<? extends Job>) cls));
        }
        return maps;
    }

    /**
     * 扫描所有{@link com.handee.annotations.MessageJob}注解的类。
     *
     * @param classes 所有类.
     * @return 所有MessageJob注解的所有类列
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Reference<MessageJob, Class<RunnableNetJob<?>>>> scanMessageJob(Set<Class<?>> classes) {
        Map<String, Reference<MessageJob, Class<RunnableNetJob<?>>>> maps = new HashMap<>();
        if (classes == null || classes.size() < 1) {
            return maps;
        }
        for (Class<?> cls : classes) {
            if (cls == null || !(RunnableNetJob.class.isAssignableFrom(cls))) {
                continue;
            }
            MessageJob job = cls.getAnnotation(MessageJob.class);
            Deprecated deprecated = cls.getAnnotation(Deprecated.class);
            if (job == null || deprecated != null) {
                continue;
            }
            if (maps.containsKey(job.code())) {
                logger.warn("[before] override message job [" + maps.get(job.code()).value.getName() + "],  code :: " + job.code());
                logger.warn("[after] override message job [" + cls.getName() + "],  code :: " + job.code());
            }
            logger.info("mapped message job [" + cls.getName() + "],  code :: " + job.code());
            maps.put(job.code(), new Reference<>(job, (Class<RunnableNetJob<?>>) cls));
        }
        return maps;
    }
}
