/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * NIOSocketServer.java
 * 
 * 2013 2013-6-8 上午10:04:04
 * 
 *******************************************************************/
package com.handee;

import com.handee.net.message.NetMessage;
import com.handee.network.MinaConnection;
import com.handee.network.NetConnection;
import com.handee.utils.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import java.net.InetSocketAddress;

/**
 * 
 * 使用Mina消息编解码器的NIO服务。
 * 
 * @author Mark
 * 
 */
public class MinaCodecSocketServer extends BaseServer implements IoHandler {
	private static final Logger logger = Logger.getLogger(MinaCodecSocketServer.class);

	public MinaCodecSocketServer(int port, ProtocolCodecFactory factory) {
		super(port, factory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#sessionCreated(org.apache.mina
	 * .core.session.IoSession)
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#sessionOpened(org.apache.mina.
	 * core.session.IoSession)
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.debug("connection open [session=" + session.getId() + "]");

		MinaConnection connection = new MinaConnection(session);
		connection.setTimeout(ContextProperties.TIME_CLIENT_TIMEOUT);
		connection.setPingTime(ContextProperties.TIME_PING);
		connection.setMaxBufferSize(ContextProperties.MAX_BUFFER_SIZE);
		session.setAttribute("conn", connection);

		handlerWrapper.connectionOpened(connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#sessionClosed(org.apache.mina.
	 * core.session.IoSession)
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.debug("connection closed [session=" + session.getId() + "]");
		MinaConnection conn = (MinaConnection) session.getAttribute("conn");
		session.setAttribute("conn", null);
		// 连接信息不存在
		if (conn == null) {
			return;
		}
		// 回调
		handlerWrapper.connectionClosed(conn);
		// 清除数据
		conn.removeAllListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#sessionIdle(org.apache.mina.core
	 * .session.IoSession, org.apache.mina.core.session.IdleStatus)
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.IServer#getConnectionCount()
	 */
	public int getConnectionCount() {
		return acceptor.getManagedSessionCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#exceptionCaught(org.apache.mina
	 * .core.session.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("error in data transport :: ", cause);
		sessionClosed(session);
		// MinaConnection conn = (MinaConnection) session.getAttribute("conn");
		// conn.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#messageReceived(org.apache.mina
	 * .core.session.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (!(message instanceof NetMessage)) {
			return;
		}
		MinaConnection conn = (MinaConnection) session.getAttribute("conn");
		NetMessage request = (NetMessage) message;
		request.setSessionId(conn.getSessionId());
		request.setAttributes(conn.getAttributes());
		handlerWrapper.messageArrived(conn, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#messageSent(org.apache.mina.core
	 * .session.IoSession, java.lang.Object)
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.IServer#start()
	 */
	@Override
	public final void start() {
		try {
			if (isRunning()) {
				return;
			}
			acceptor.setHandler(this);
			acceptor.bind(new InetSocketAddress(this.port));
			running = true;
			SystemUtils.printSection("nio socket service listening on port [" + getPort() + "] ");
		} catch (java.io.IOException e) {
			// e.printStackTrace();
			logger.error("start nio socket server error :: ", e);
			running = false;
		}
	}

	@Override
	public NetConnection getConnection(long sessionId) {
		IoSession session = acceptor.getManagedSessions().get(sessionId);
		if (session == null) {
			return null;
		}
		return (MinaConnection) session.getAttribute("conn");
	}
}
