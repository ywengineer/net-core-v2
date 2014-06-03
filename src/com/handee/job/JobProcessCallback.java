/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * com.handee.service.ServiceCallback.java
 * 
 * 2013-6-25 下午1:19:46
 * 
 *******************************************************************/
package com.handee.job;


/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public interface JobProcessCallback<K> {
	void call(K value);
}
