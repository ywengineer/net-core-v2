/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * Job.java
 * 
 * 2013 2013-5-17 下午2:37:19
 * 
 *******************************************************************/
package com.handee.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 只能用于类的注解。
 * 
 * <p>
 * 表示注解类为一消息处理任务，自动被MessageHelper处理。
 * </p>
 * 
 * @see com.handee.helper.MessageHelper
 * 
 * @author Mark
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface MessageJob {

	/**
	 * 执行该注解所用类的协议。
	 * 
	 * @return
	 */
	public String code();

	/**
	 * 注解所用类应该在哪一个队列执行。
	 * 
	 * -1表示默认规则，在job最少的队列执行。
	 * 
	 * 其它表示在指定队列执行。
	 * 
	 * @return
	 */
	public int queue();
}
