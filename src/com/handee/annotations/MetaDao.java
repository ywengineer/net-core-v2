/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * com.handee.annotations.MetaDao.java
 *
 * 2013-7-2 上午9:42:36
 *
 *******************************************************************/
package com.handee.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MetaDao注解。
 * <p/>
 * 此注解主要用于标记各种数据库数据访问对象。
 * <p/>
 * 用此注解标注的类表示在启动应用时需要执行访问数据库操作。
 *
 * @author Mark
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface MetaDao {

}
