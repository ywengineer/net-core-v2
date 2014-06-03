/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * HandeeServerListener.java
 * 
 * 2013 2013-5-23 下午3:44:45
 * 
 *******************************************************************/
package com.handee.event.listener;

import com.handee.network.INetMessageListener;
import com.handee.network.INetServerListener;

/**
 * 
 * 网络服务、网络连接、网络消息监听器。
 * 
 * @author Mark
 * 
 */
public interface INetHandler extends INetServerListener, INetMessageListener {
}