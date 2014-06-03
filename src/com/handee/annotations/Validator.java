/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 *
 * Validator.java
 *
 * 2013 2013-5-21 下午3:23:46
 *
 *******************************************************************/
package com.handee.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 服务器参数验证器。
 * 
 * 表达式验证.
 * 
 * @author Mark
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface Validator {
	/**
	 * 默认值。
	 * 
	 * 验证失败时，会重新赋值为此值。
	 * 
	 * @return
	 */
	public String defaultValue() default "";

	/**
	 * 验证失败消息
	 * 
	 * @return
	 */
	public String failureMessage() default "";

	/**
	 * 验证脚本
	 * 
	 * @return
	 */
	public String script() default "";

	/**
	 * 表达式中的变量名
	 * 
	 * @return
	 */
	public String placeHolder() default "";

	/**
	 * 验证失败时，是否终止运行程序。
	 * 
	 * @return
	 */
	public boolean failureToExit() default false;
}
