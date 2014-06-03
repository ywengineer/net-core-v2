/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * com.handee.annotations.DB.java
 *
 * 2013-6-28 下午4:39:19
 *
 *******************************************************************/
package com.handee.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库引用注解类。
 * <p/>
 * 用此注解标注的类表示此类表示的数据对象需要保存到注解参数数据库。
 *
 * @author Mark
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface DBReference {
    public String value() default "";
}
