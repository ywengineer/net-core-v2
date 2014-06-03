/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * KryoEntity.java
 *
 * 14-1-21 下午3:13
 *
 *******************************************************************/
package com.handee.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.handee.annotations.ManagedByKryo.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-1-21 下午3:13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface ManagedByKryo {
    /**
     * 仅在远程服务接口时有用。
     *
     * @return 远程服务id
     */
    public int objectId() default 0;
}
