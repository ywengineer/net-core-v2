/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * IServer.java
 * 
 * 2013 2013-5-28 下午5:23:49
 * 
 *******************************************************************/
package com.handee;

import com.handee.event.listener.INetHandler;
import com.handee.net.message.HttpRequestMessage;
import com.handee.net.message.NetMessage;
import com.handee.network.NetConnection;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * 
 * 基础功能网络提供。
 * 
 * @author Mark
 * 
 */
public abstract class BaseServer {
	private static final Logger logger = Logger.getLogger(BaseServer.class);
	protected int port;
	protected int maxConnections;
	protected IoAcceptor acceptor;
	protected boolean running = false;
	private INetHandler handler;

	protected INetHandler handlerWrapper = new INetHandler() {

		@Override
		public void messageArrived(NetConnection conn, NetMessage msg) {
			if (ContextProperties.IS_ACTIVE && handler != null) {
				handler.messageArrived(conn, msg);
			}
		}

		@Override
		public void httpMessageArrived(IoSession session, HttpRequestMessage message) {
			if (ContextProperties.IS_ACTIVE && handler != null) {
				handler.httpMessageArrived(session, message);
			}
		}

		@Override
		public void connectionOpened(NetConnection connection) {
			if (ContextProperties.IS_ACTIVE && handler != null) {
				handler.connectionOpened(connection);
			}
		}

		@Override
		public void connectionClosed(NetConnection connection) {
			if (ContextProperties.IS_ACTIVE && handler != null) {
				handler.connectionClosed(connection);
			}
		}
	};

	/**
	 * 不做任何事情.
	 * 
	 * 使用此构造函数，表明其派生类不需要直接使用Mina的IoAcceptor。
	 * 
	 */
	public BaseServer() {
	}

	/**
	 * 
	 * 使用Mina的IoAcceptor提供网络服务。
	 * 
	 * @param port
	 *            端口
	 * @param factory
	 *            协议编解码创建器
	 */
	public BaseServer(int port, ProtocolCodecFactory factory) {
		this.port = port;
		acceptor = new NioSocketAcceptor();
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(factory));
		if (logger.getLevel() == Level.INFO) {
			acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMaxConnections(int max) {
		maxConnections = max;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setHandler(INetHandler handler) {
		this.handler = handler;
	}

	public void stop() {
		if (!running || acceptor.isDisposing() || acceptor.isDisposed()) {
			return;
		}
		running = false;
		acceptor.dispose();
		acceptor = null;
		handler = null;
		handlerWrapper = null;
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 *            消息
	 * @param closeAfterWrite
	 *            消息发送之后是否关闭连接
	 */
	public void writeMessage(NetMessage message, boolean closeAfterWrite) {
		IoSession session = acceptor.getManagedSessions().get(message.getSessionId());
		if (session == null) {
			logger.error("the destination of this message for write not exsit.[session=" + message.getSessionId() + "]");
			return;
		}
		session.write(message);
		if (closeAfterWrite) {
			session.close(false);
		}
	}

	/**
	 * 启动服务器。
	 */
	public abstract void start();

	/**
	 * 获取当前连接数
	 * 
	 * @return 当前连接数
	 */
	public abstract int getConnectionCount();

	/**
	 * 获取连接。
	 * 
	 * @param sessionId
	 *            会话ID
	 * @return 连接
	 */
	public abstract NetConnection getConnection(long sessionId);
}
