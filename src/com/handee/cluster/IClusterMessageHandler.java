/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * IClusterMessageHandler.java
 * 
 * 2013 2013-5-29 下午5:08:14
 * 
 *******************************************************************/
package com.handee.cluster;

import org.jgroups.Message;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public interface IClusterMessageHandler {
	/**
	 * 集群消息
	 * 
	 * @param message
	 *            消息
	 */
	void onMessage(Message message);
}
