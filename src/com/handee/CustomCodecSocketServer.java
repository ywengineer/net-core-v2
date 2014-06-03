/*******************************************************************
 * @Version : 0.5
 * 
 * 
 *          MainNetServer.java
 * 
 * 
 *          2011-10-11 下午03:05:52
 * 
 *******************************************************************/
package com.handee;

import com.handee.net.message.HttpRequestMessage;
import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageCodecFactory;
import com.handee.network.INetMessageListener;
import com.handee.network.INetServerListener;
import com.handee.network.MinaNetServer;
import com.handee.network.NetConnection;
import com.handee.utils.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 自定义消息编解码服务。
 * 
 * @author Mark
 * 
 */
public class CustomCodecSocketServer extends BaseServer implements INetMessageListener, INetServerListener {
	private static final Logger logger = Logger.getLogger(CustomCodecSocketServer.class);
	private static final Map<Long, NetConnection> allConnection = new ConcurrentHashMap<>();

	protected MinaNetServer netServer;
	protected MessageCodecFactory factory;

	public CustomCodecSocketServer(int port, MessageCodecFactory factory) {
		this.port = port;
		this.factory = factory;
	}

	public final void start() {
		if (running)
			return;
		netServer = new MinaNetServer();
		netServer.setMaxConnections(getMaxConnections());
		netServer.init(port, false);
		netServer.addNetServerListener(this);
		netServer.setMessageCodecFactory(factory);
		running = netServer.start();
		SystemUtils.printSection("socket server started, listen on port : " + getPort());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.network.AbstractNetServer#stop()
	 */
	@Override
	public final void stop() {
		if (!running) {
			return;
		}
		running = false;
		try {
			allConnection.clear();
			netServer.removeAllNetServerListeners();
			netServer.stop(); // 停止网络服务器
		} catch (Exception e) {
			logger.error("stop socket server error :: ", e);
		}
	}

	/**
	 * 
	 * 连接关闭
	 * 
	 */
	public final void connectionClosed(NetConnection connection) {
		logger.debug("connection closed [session=" + connection.getSessionId() + "]");
		allConnection.remove(connection.getSessionId());
		handlerWrapper.connectionClosed(connection);
	}

	/**
	 * 打开连接
	 */
	public final void connectionOpened(NetConnection connection) {
		logger.debug("connection open [session=" + connection.getSessionId() + "]");
		allConnection.put(connection.getSessionId(), connection);
		connection.addListener(this);
		connection.setTimeout(ContextProperties.TIME_CLIENT_TIMEOUT);
		connection.setPingTime(ContextProperties.TIME_PING);
		connection.setMaxBufferSize(ContextProperties.MAX_BUFFER_SIZE);
		handlerWrapper.connectionOpened(connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.network.NetConnectionListener#messageArrived(com.handee.network
	 * .NetConnection, com.handee.net.message.Message)
	 */
	@Override
	public final void messageArrived(NetConnection conn, NetMessage msg) {
		msg.setSessionId(conn.getSessionId());
		msg.setAttributes(conn.getAttributes());
		handlerWrapper.messageArrived(conn, msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.HandeeServer#getConnectionCount()
	 */
	@Override
	public final int getConnectionCount() {
		return allConnection.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.HandeeServer#writeMessage(com.handee.net.message.HDMessage,
	 * boolean)
	 */
	@Override
	public void writeMessage(NetMessage message, boolean closeAfterWrite) {
		NetConnection session = allConnection.get(message.getSessionId());
		if (session == null) {
			logger.error("the destination of this message for write not exsit.[session=" + message.getSessionId() + "]");
			return;
		}
		session.sendMessage(message);
		if (closeAfterWrite) {
			session.close();
		}
	}

	@Override
	public NetConnection getConnection(long sessionId) {
		return allConnection.get(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.network.INetMessageListener#httpMessageArrived(org.apache.
	 * mina.core.session.IoSession, com.handee.net.message.HttpRequestMessage)
	 */
	@Override
	public void httpMessageArrived(IoSession session, HttpRequestMessage message) {

	}
}
