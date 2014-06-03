/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * EmptyHandler.java
 * 
 * 2013 2013-5-23 下午4:27:52
 * 
 *******************************************************************/
package com.handee;

import com.handee.event.listener.INetHandler;
import com.handee.net.message.HttpRequestMessage;
import com.handee.net.message.NetMessage;
import com.handee.network.NetConnection;
import org.apache.mina.core.session.IoSession;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class EmptyHandler implements INetHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.network.NetServerListener#connectionOpened(com.handee.network
	 * .NetConnection)
	 */
	@Override
	public void connectionOpened(NetConnection connection) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.network.NetServerListener#connectionClosed(com.handee.network
	 * .NetConnection)
	 */
	@Override
	public void connectionClosed(NetConnection connection) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.network.NetConnectionListener#messageArrived(com.handee.network
	 * .NetConnection, com.handee.net.message.Message)
	 */
	@Override
	public void messageArrived(NetConnection conn, NetMessage msg) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.event.listener.IHandler#httpMessageArrived(org.apache.mina
	 * .core.session.IoSession, com.handee.net.message.HttpRequestMessage)
	 */
	@Override
	public void httpMessageArrived(IoSession session, HttpRequestMessage message) {

	}
}
